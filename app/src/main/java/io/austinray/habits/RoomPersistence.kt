package io.austinray.habits

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import java.time.LocalDate

@Database(
    entities = [ThemeSchema::class, HabitSchema::class, CompleteDateSchema::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ThemeDatabase : RoomDatabase() {
    abstract fun themeDao(): ThemeDao
}

@Dao
abstract class ThemeDao {
    @Transaction
    @Query("SELECT * FROM themes")
    abstract fun getAllThemeHabits(): LiveData<List<ThemeHabitJoin>>

    @Insert
    abstract fun addTheme(theme: ThemeSchema)

    @Insert
    abstract fun addHabit(habit: HabitSchema)

    @Insert
    abstract fun addCompleteDate(completeDateSchema: CompleteDateSchema)

    @Delete
    abstract fun deleteTheme(theme: ThemeSchema)

    @Delete
    abstract fun deleteHabit(habit: HabitSchema)

    @Delete
    abstract fun deleteCompleteDate(completeDateSchema: CompleteDateSchema)

    fun addHabit(habit: Habit, theme: Theme) {
        val habitSchema = HabitSchema(habit.name, theme.name, habit.createDate)
        addHabit(habitSchema)

        habit.completeDates
            .asSequence()
            .forEach { date -> addDate(habit, date) }

    }

    fun addTheme(theme: Theme) {
        val themeSchema = ThemeSchema(theme.name)
        addTheme(themeSchema)

        theme.habits.forEach { habit ->
            addHabit(habit, theme)
        }
    }

    fun addDate(habit: Habit, date: LocalDate) {
        val completeDateSchema = CompleteDateSchema(habit.name, date)
        addCompleteDate(completeDateSchema)
    }

    fun removeDate(habit: Habit, date: LocalDate) {
        val completeDateSchema = CompleteDateSchema(habit.name, date)
        deleteCompleteDate(completeDateSchema)
    }

    fun removeHabit(habit: Habit, theme: Theme) {
        val habitSchema = HabitSchema(habit.name, theme.name, habit.createDate)
        habit.completeDates.forEach { date -> removeDate(habit, date) }
        deleteHabit(habitSchema)
    }

    fun removeTheme(theme: Theme) {
        val themeSchema = ThemeSchema(theme.name)
        theme.habits.forEach { removeHabit(it, theme) }
        deleteTheme(themeSchema)
    }
}

@Entity(tableName = "themes")
data class ThemeSchema(@PrimaryKey val themeName: String)

@Entity(tableName = "habits", primaryKeys = ["habitName", "themeName"])
data class HabitSchema(val habitName: String, val themeName: String, val createDate: LocalDate)

@Entity(tableName = "completions", primaryKeys = ["habitName", "date"])
data class CompleteDateSchema(val habitName: String, val date: LocalDate)

data class HabitCompleteDataJoin(
    @Embedded val habit: HabitSchema,
    @Relation(
        parentColumn = "habitName",
        entityColumn = "habitName"
    )
    val dates: List<CompleteDateSchema>
)

data class ThemeHabitJoin(
    @Embedded val theme: ThemeSchema,
    @Relation(
        parentColumn = "themeName",
        entityColumn = "themeName",
        entity = HabitSchema::class
    )
    val habits: List<HabitCompleteDataJoin>
)

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }
}

