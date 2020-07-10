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

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import io.austinray.habits.model.Habit
import io.austinray.habits.model.Theme
import java.time.LocalDate
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ThemeViewModel(application: Application) : AndroidViewModel(application), ThemeCallback,
    AddHabitCallback, AddThemeCallback {
    private val db =
        Room.databaseBuilder(this.getApplication(), ThemeDatabase::class.java, "theme-database")
            .build()

    private var _themes: MutableLiveData<List<Theme>> = MutableLiveData()

    val themes: LiveData<List<Theme>>
        get() = _themes

    init {
        db.themeDao().getAllThemeHabits().observeForever { themes ->
            val newThemes = themes.map { theme ->
                val habits = theme.habits.map { habitJoin ->
                    Habit(
                        habitJoin.habit.habitName,
                        habitJoin.habit.createDate,
                        habitJoin.dates.map { dataSchema -> dataSchema.date }.toMutableList()
                    )
                }
                Theme(theme.theme.themeName, habits.toMutableList())
            }

            _themes.value = newThemes
        }
    }

    override fun removeTheme(theme: Theme) {
        GlobalScope.launch { db.removeTheme(theme) }
    }

    override fun removeHabit(habit: Habit, theme: Theme) {
        GlobalScope.launch { db.removeHabit(habit, theme) }
    }

    override fun addDate(theme: Theme, habit: Habit, date: LocalDate) {
        GlobalScope.launch { db.addDate(habit, date) }
    }

    override fun removeDate(theme: Theme, habit: Habit, date: LocalDate) {
        GlobalScope.launch { db.removeDate(habit, date) }
    }

    override fun addTheme(name: String) {
        val newTheme = Theme(name)
        GlobalScope.launch { db.addTheme(newTheme) }
    }

    override fun addHabit(name: String, theme: Theme) {
        val newHabit = Habit(name = name, createDate = LocalDate.now())
        GlobalScope.launch { db.addHabit(newHabit, theme) }
    }
}
