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

import io.austinray.habits.model.Habit
import io.austinray.habits.model.Theme
import java.time.LocalDate

interface ThemeCallback {
    fun removeTheme(theme: Theme)
    fun removeHabit(habit: Habit, theme: Theme)
    fun addDate(theme: Theme, habit: Habit, date: LocalDate)
    fun removeDate(theme: Theme, habit: Habit, date: LocalDate)
}
