/*
 *  This file is part of Habit Themes.
 * 
 *  Habit Themes is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Habit Themes is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Habit Themes. If not, see <https://www.gnu.org/licenses/>.
 */
package io.austinray.habits.viewmodel

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import io.austinray.habits.model.Habit
import io.austinray.habits.model.Theme
import java.time.LocalDate

@Database(
    entities = [ThemeSchema::class, HabitSchema::class, CompleteDateSchema::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ThemeDatabase : RoomDatabase() {
    abstract fun themeDao(): ThemeDao

    fun addHabit(habit: Habit, theme: Theme) {
        val habitSchema =
            HabitSchema(habit.name, theme.name, habit.createDate)
        themeDao().addHabit(habitSchema)

        habit.completeDates
            .asSequence()
            .forEach { date -> addDate(habit, date) }
    }

    fun addTheme(theme: Theme) {
        val themeSchema = ThemeSchema(theme.name)
        themeDao().addTheme(themeSchema)

        theme.habits.forEach { habit ->
            addHabit(habit, theme)
        }
    }

    fun addDate(habit: Habit, date: LocalDate) {
        val completeDateSchema = CompleteDateSchema(habit.name, date)
        themeDao().addCompleteDate(completeDateSchema)
    }

    fun removeDate(habit: Habit, date: LocalDate) {
        val completeDateSchema = CompleteDateSchema(habit.name, date)
        themeDao().deleteCompleteDate(completeDateSchema)
    }

    fun removeHabit(habit: Habit, theme: Theme) {
        val habitSchema =
            HabitSchema(habit.name, theme.name, habit.createDate)
        habit.completeDates.forEach { date -> removeDate(habit, date) }
        themeDao().deleteHabit(habitSchema)
    }

    fun removeTheme(theme: Theme) {
        val themeSchema = ThemeSchema(theme.name)
        theme.habits.forEach { removeHabit(it, theme) }
        themeDao().deleteTheme(themeSchema)
    }
}

@Dao
interface ThemeDao {
    @Transaction
    @Query("SELECT * FROM themes")
    fun getAllThemeHabits(): LiveData<List<ThemeHabitJoin>>

    @Insert
    fun addTheme(theme: ThemeSchema)

    @Insert
    fun addHabit(habit: HabitSchema)

    @Insert
    fun addCompleteDate(completeDateSchema: CompleteDateSchema)

    @Delete
    fun deleteTheme(theme: ThemeSchema)

    @Delete
    fun deleteHabit(habit: HabitSchema)

    @Delete
    fun deleteCompleteDate(completeDateSchema: CompleteDateSchema)
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
