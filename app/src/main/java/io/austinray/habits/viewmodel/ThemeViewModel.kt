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
import io.austinray.habits.data.HabitThemeRepo
import io.austinray.habits.model.Habit
import io.austinray.habits.model.Theme
import java.time.LocalDate

class ThemeViewModel(
    private val repo: HabitThemeRepo,
    application: Application
) :
    AndroidViewModel(application), ThemeCallback,
    AddHabitCallback, AddThemeCallback {

    val themes: LiveData<List<Theme>> = repo.getAllThemeHabits()

    override fun removeTheme(theme: Theme) {
        repo.removeTheme(theme)
    }

    override fun removeHabit(habit: Habit, theme: Theme) {
        repo.removeHabit(habit, theme)
    }

    override fun addDate(theme: Theme, habit: Habit, date: LocalDate) {
        repo.addDate(theme, habit, date)
    }

    override fun removeDate(theme: Theme, habit: Habit, date: LocalDate) {
        repo.removeDate(theme, habit, date)
    }

    override fun addTheme(name: String) {
        repo.addTheme(name)
    }

    override fun addHabit(name: String, theme: Theme) {
        repo.addHabit(name, theme)
    }
}
