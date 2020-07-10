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
import androidx.fragment.app.Fragment
import io.austinray.habits.R
import io.austinray.habits.viewmodel.AddThemeCallback
import kotlinx.android.synthetic.main.fragment_add_theme.*

class AddThemeFragment(private val callback: AddThemeCallback, private val parentDialog: Dialog?) :
    Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_theme, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        addThemeBtn.setOnClickListener {
            val newThemeName = addThemeEditText.text.toString()
            callback.addTheme(newThemeName)
            parentDialog?.dismiss()
        }
        super.onViewCreated(view, savedInstanceState)
    }
}
