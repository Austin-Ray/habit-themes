package io.austinray.habits

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.habit_layout.view.*
import kotlinx.android.synthetic.main.theme_layout.view.*
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val model: ThemeViewModel by viewModels()

        themeList.layoutManager = LinearLayoutManager(this)
        themeList.adapter = ThemeAdapter(model.themes, model as ThemeCallback)
    }
}

data class Habit(
    val name: String,
    val createDate: LocalDate,
    val completeDates: MutableList<LocalDate>
)

data class Theme(val name: String, val habits: List<Habit>)

class ThemeViewModel : ViewModel(), ThemeCallback {
    val themes: List<Theme> = SAMPLE_DATA


    override fun addDate(theme: Theme, habit: Habit, date: LocalDate) {
        val targetTheme = themes.find { it == theme }
        if (targetTheme != null) {
            println("Theme Found!")
        }
        val targetHabit = targetTheme?.habits?.find { it == habit }
        if (targetHabit != null) {
            println("Habit Found! ${targetHabit.name}")
        }
        targetHabit?.completeDates?.add(date)
        println(targetHabit?.completeDates)
    }

    override fun removeDate(theme: Theme, habit: Habit, date: LocalDate) {
        val targetTheme = themes.find { it == theme }
        if (targetTheme != null) {
            println("Theme Found!")
        }
        val targetHabit = targetTheme?.habits?.find { it == habit }
        if (targetHabit != null) {
            println("Habit Looking for ${habit.name}")
            println("Habit Found! ${targetHabit.name}")
        }
        targetHabit?.completeDates?.remove(date)
        println(targetHabit?.completeDates)
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
            println(date)
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
