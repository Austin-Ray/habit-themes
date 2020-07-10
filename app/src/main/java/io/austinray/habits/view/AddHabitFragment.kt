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

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import io.austinray.habits.R
import io.austinray.habits.model.Theme
import io.austinray.habits.viewmodel.AddHabitCallback
import kotlinx.android.synthetic.main.fragment_add_habit.*

class AddHabitFragment(
    private val themes: List<Theme>,
    private val callback: AddHabitCallback,
    private val parentDialog: Dialog?
) :
    Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_habit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val themeNames = themes.map { it.name }.toList()
        themeSpinner.adapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            themeNames
        )

        habitAdd.setOnClickListener {
            val selected = themeSpinner.selectedItemPosition
            callback.addHabit(habitEditText.text.toString(), themes[selected])
            parentDialog?.dismiss()
        }

        super.onViewCreated(view, savedInstanceState)
    }
}
