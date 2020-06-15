package io.austinray.habits

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_theme_habit.*
import kotlinx.android.synthetic.main.fragment_add_habit.*
import kotlinx.android.synthetic.main.habit_layout.view.*
import kotlinx.android.synthetic.main.theme_layout.view.*
import java.time.LocalDate

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
                    model as AddHabitCallback
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

data class Theme(val name: String, val habits: MutableList<Habit> = mutableListOf())

class ThemeViewModel : ViewModel(), ThemeCallback, AddHabitCallback {
    val themes: MutableLiveData<MutableList<Theme>> = MutableLiveData(SAMPLE_DATA.toMutableList())


    override fun addDate(theme: Theme, habit: Habit, date: LocalDate) {
        val targetTheme = themes.value?.find { it == theme }
        val targetHabit = targetTheme?.habits?.find { it == habit }
        targetHabit?.completeDates?.add(date)
    }

    override fun removeDate(theme: Theme, habit: Habit, date: LocalDate) {
        val targetTheme = themes.value?.find { it == theme }
        val targetHabit = targetTheme?.habits?.find { it == habit }
        targetHabit?.completeDates?.remove(date)
    }

    override fun addHabit(name: String, theme: Theme) {
        val newHabit = Habit(name, LocalDate.now())
        val foundTheme = themes.value?.find { it == theme }
        foundTheme?.habits?.add(newHabit)
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
        (habitList.adapter as HabitAdapter).updateDataSet(theme.habits)
        (habitList.adapter as HabitAdapter).callback = object : HabitCallback {
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
    }

    fun updateDataSet(habits: List<Habit>) {
        this.habits = habits
        this.notifyDataSetChanged()
    }

}

interface AddHabitCallback {
    fun addHabit(name: String, theme: Theme)
}

class AddHabitThemeDialog(
    private val themes: List<Theme>,
    private val habitCallback: AddHabitCallback,
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
