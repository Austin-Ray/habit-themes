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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.austinray.habits.R
import io.austinray.habits.model.Habit
import io.austinray.habits.model.Theme
import io.austinray.habits.viewmodel.HabitCallback
import io.austinray.habits.viewmodel.ThemeCallback
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.android.synthetic.main.theme_layout.view.*

class ThemeAdapter(private var themes: List<Theme>, private val themeCallback: ThemeCallback) :
    RecyclerView.Adapter<ThemeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThemeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.theme_layout, parent, false)

        return ThemeViewHolder(view)
    }

    override fun getItemCount(): Int = themes.count()

    override fun onBindViewHolder(holder: ThemeViewHolder, position: Int) {
        val theme = themes[position]
        holder.registerCallback(themeCallback)
        holder.configureView(theme)
    }
}

class ThemeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val themeEditText = view.themeNameText
    private val habitList = view.habitList
    private val datesConstraint = view.datesConstraint

    private var callback: ThemeCallback? = null

    private val dateFormatter = DateTimeFormatter.ofPattern("M/dd")

    init {
        habitList.layoutManager = LinearLayoutManager(view.context)
        habitList.adapter = HabitAdapter()

        var date = LocalDate.now()

        view.date0.text = date.format(dateFormatter)
        date = date.minusDays(1)
        view.date1.text = date.format(dateFormatter)
        date = date.minusDays(1)
        view.date2.text = date.format(dateFormatter)
        date = date.minusDays(1)
        view.date3.text = date.format(dateFormatter)
        date = date.minusDays(1)
        view.date4.text = date.format(dateFormatter)
    }

    fun configureView(theme: Theme) {
        themeEditText.text = theme.name
        themeEditText.setOnLongClickListener {
            callback?.removeTheme(theme)
            true
        }
        (habitList.adapter as HabitAdapter).updateDataSet(theme.habits)
        (habitList.adapter as HabitAdapter).callback = object : HabitCallback {
            override fun removeHabit(habit: Habit) {
                callback?.removeHabit(habit, theme)
            }

            override fun addDate(habit: Habit, date: LocalDate) {
                callback?.addDate(theme, habit, date)
            }

            override fun removeDate(habit: Habit, date: LocalDate) {
                callback?.removeDate(theme, habit, date)
            }
        }

        if (theme.habits.isEmpty()) {
            datesConstraint.isVisible = false
        }
    }

    fun registerCallback(callback: ThemeCallback) {
        this.callback = callback
    }
}
