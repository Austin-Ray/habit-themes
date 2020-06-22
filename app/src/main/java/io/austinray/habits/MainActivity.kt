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

import android.app.Application
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import java.time.LocalDate
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_theme_habit.*
import kotlinx.android.synthetic.main.fragment_add_habit.*
import kotlinx.android.synthetic.main.fragment_add_theme.*
import kotlinx.android.synthetic.main.habit_layout.view.*
import kotlinx.android.synthetic.main.theme_layout.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val model: ThemeViewModel by viewModels()

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

data class Habit(
    val name: String,
    val createDate: LocalDate,
    val completeDates: MutableList<LocalDate> = mutableListOf()
)

data class Theme(
    val name: String,
    val habits: MutableList<Habit> = mutableListOf()
)

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

class ThemeViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val themeEditText = view.themeNameText
    private val habitList = view.habitList

    private var callback: ThemeCallback? = null

    init {
        habitList.layoutManager = LinearLayoutManager(view.context)
        habitList.adapter = HabitAdapter()
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
    }

    fun registerCallback(callback: ThemeCallback) {
        this.callback = callback
    }
}

interface ThemeCallback {
    fun removeTheme(theme: Theme)
    fun removeHabit(habit: Habit, theme: Theme)
    fun addDate(theme: Theme, habit: Habit, date: LocalDate)
    fun removeDate(theme: Theme, habit: Habit, date: LocalDate)
}

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

interface HabitCallback {
    fun removeHabit(habit: Habit)
    fun addDate(habit: Habit, date: LocalDate)
    fun removeDate(habit: Habit, date: LocalDate)
}

class HabitViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val habitNameEditText: TextView = view.habitNameText

    private val habitDateCheckBox1 = view.habitDateCheckBox1
    private val habitDateCheckBox2 = view.habitDateCheckBox2
    private val habitDateCheckBox3 = view.habitDateCheckBox3
    private val habitDateCheckBox4 = view.habitDateCheckBox4
    private val habitDateCheckBox5 = view.habitDateCheckBox5

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

interface AddThemeCallback {
    fun addTheme(name: String)
}

interface AddHabitCallback {
    fun addHabit(name: String, theme: Theme)
}

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
