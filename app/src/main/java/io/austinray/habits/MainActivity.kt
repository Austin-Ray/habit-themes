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
package io.austinray.habits

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import io.austinray.habits.data.newInstance
import io.austinray.habits.view.AddHabitThemeDialog
import io.austinray.habits.view.ThemeAdapter
import io.austinray.habits.viewmodel.AddHabitCallback
import io.austinray.habits.viewmodel.AddThemeCallback
import io.austinray.habits.viewmodel.ThemeCallback
import io.austinray.habits.viewmodel.ThemeViewModel
import io.austinray.habits.viewmodel.ThemeViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val model: ThemeViewModel by viewModels {
        ThemeViewModelFactory(
            this,
            newInstance(this.application),
            this.application,
            intent.extras
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model.themes.observe(this, Observer { themes ->
            themeList.layoutManager = LinearLayoutManager(this)
            themeList.adapter = ThemeAdapter(themes.toList(), model as ThemeCallback)

            val fab = mainAddFab
            fab.setOnClickListener {
                val dialog = AddHabitThemeDialog(
                    themes.toList(),
                    model as AddHabitCallback,
                    model as AddThemeCallback
                )
                dialog.show(supportFragmentManager, "AddHabitDialog")
            }
        })
    }
}
