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
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.austinray.habits.R
import io.austinray.habits.model.Habit
import io.austinray.habits.viewmodel.HabitCallback
import java.time.LocalDate
import kotlinx.android.synthetic.main.habit_layout.view.*

class HabitAdapter(
    private var habits: List<Habit> = listOf(),
    var callback: HabitCallback? = null
) :
    RecyclerView.Adapter<HabitViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.habit_layout, parent, false)

        return HabitViewHolder(view)
    }

    override fun getItemCount(): Int = habits.count()

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]

        holder.habitNameEditText.text = habit.name
        holder.dateCheckBoxMap.forEach { (date, checkbox) ->
            checkbox.isChecked = habit.completeDates.contains(date)
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    callback?.addDate(habit, date)
                } else {
                    callback?.removeDate(habit, date)
                }
            }
        }
        holder.habitNameEditText.setOnLongClickListener {
            callback?.removeHabit(habit)
            true
        }
    }

    fun updateDataSet(habits: List<Habit>) {
        this.habits = habits
        this.notifyDataSetChanged()
    }
}

class HabitViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val habitNameEditText: TextView = view.habitNameText

    private val habitDateCheckBox1 = view.habitDateCheckBox0
    private val habitDateCheckBox2 = view.habitDateCheckBox1
    private val habitDateCheckBox3 = view.habitDateCheckBox2
    private val habitDateCheckBox4 = view.habitDateCheckBox3
    private val habitDateCheckBox5 = view.habitDateCheckBox4

    val dateCheckBoxMap: Map<LocalDate, CheckBox>

    init {
        val checkBoxList = listOf(
            habitDateCheckBox1,
            habitDateCheckBox2,
            habitDateCheckBox3,
            habitDateCheckBox4,
            habitDateCheckBox5
        )

        val tempMap: MutableMap<LocalDate, CheckBox> = mutableMapOf()

        var currDate = LocalDate.now()
        checkBoxList.forEach { checkbox ->
            tempMap[currDate] = checkbox
            currDate = currDate.minusDays(1)
        }

        dateCheckBoxMap = tempMap.toMap()
    }
}
