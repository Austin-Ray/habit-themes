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
package io.austinray.habits.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.commit
import io.austinray.habits.R
import io.austinray.habits.model.Theme
import io.austinray.habits.viewmodel.AddHabitCallback
import io.austinray.habits.viewmodel.AddThemeCallback
import kotlinx.android.synthetic.main.add_theme_habit.*

class AddHabitThemeDialog(
    private val themes: List<Theme>,
    private val habitCallback: AddHabitCallback,
    private val themeCallback: AddThemeCallback
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_theme_habit, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val fragMan = childFragmentManager
        val parentDialog = this.dialog
        btnTheme.setOnClickListener {
            fragMan.commit {
                replace(
                    R.id.addHabitThemeFragmentHolder,
                    AddThemeFragment(themeCallback, parentDialog)
                )
            }
        }
        btnHabit.setOnClickListener {
            fragMan.commit {
                replace(
                    R.id.addHabitThemeFragmentHolder,
                    AddHabitFragment(themes, habitCallback, parentDialog)
                )
            }
        }
        btnTheme.performClick()
    }
}
