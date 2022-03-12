package com.example.pomodo

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

data class Todo(
    var id: String,
    var name: String,
    var duration: Number? = null,
    var date: String? = null,
    var completeDate: Long? = null
)

class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var database: Database
        lateinit var sharedPreferences: SharedPreferences
        lateinit var uid: String

        lateinit var todosContainer: RecyclerView
        lateinit var todosContainerAdapter: TodosContainerAdapter

        var todosCompleteToday = 0;
        var timeCompleteToday = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = this?.getPreferences(Context.MODE_PRIVATE) ?: return

        val calendar = Calendar.getInstance()
        val dateFormat = "EEE, MMM d"
        val statsWidgetDate: TextView = findViewById(R.id.stats_widget_date)
        statsWidgetDate.text = SimpleDateFormat(dateFormat, Locale.CANADA).format(calendar.time)

        initUser()
        initData()
        initListeners()
    }

    private fun initUser() {
        val sharedPreferencesUidKey = "uid"
        if (sharedPreferences.contains(sharedPreferencesUidKey)) {
            uid = sharedPreferences.getString(sharedPreferencesUidKey, "")!!
            Log.i("PomoDo", "Exists: $uid")
            database = Database(uid)
        } else {
            database = Database()
            uid = database.uid
            Log.i("PomoDo", "New User: $uid")
            with (sharedPreferences.edit()) {
                putString(sharedPreferencesUidKey, uid)
                apply()
            }
        }
    }

    private fun initListeners() {
        val addNewTodoButton: Button = findViewById(R.id.show_add_new_todo_button)
        addNewTodoButton.setOnClickListener {
            val addNewTodoDialog: TodoDialog = TodoDialog(this)
            addNewTodoDialog.showDialog()
        }
    }

    fun displayActiveTodo(activeTodo: Todo) {
        val activeTodoNameTextView: TextView = findViewById(R.id.todo_widget_name)
        val activeTodoDateTextView: TextView = findViewById(R.id.pomodoro_widget_todo_date)
        val activeTodoDurationTextView: TextView = findViewById(R.id.pomodoro_widget_todo_duration)

        activeTodoNameTextView.text = activeTodo.name
        activeTodoDateTextView.text = activeTodo.date
        activeTodoDurationTextView.text = "${activeTodo.duration} mins"

        if (activeTodo.date == "") {
            activeTodoDateTextView.visibility = View.GONE
        }
    }

    fun updateStats() {
        val statsWidgetRatio: TextView = findViewById(R.id.stats_widget_stats_ratio)
        statsWidgetRatio.text = todosCompleteToday.toString()

        val statsWidgetDuration: TextView = findViewById(R.id.stats_widget_stats_duration)
        statsWidgetDuration.text = durationFormat()
    }

    private fun durationFormat() : String {
        if (timeCompleteToday > 60) {
            return "(${timeCompleteToday / 60} hrs)"
        }
        return "($timeCompleteToday mins)"
    }
}
