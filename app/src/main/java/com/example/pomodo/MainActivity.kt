package com.example.pomodo

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.collections.ArrayList

data class Todo(
    var id: String,
    var name: String,
    var duration: Number? = null,
    var date: String? = null,
    var completeDate: Number? = null
)

class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var database: Database
        lateinit var sharedPreferences: SharedPreferences
        lateinit var uid: String

        lateinit var todosContainer: RecyclerView
        lateinit var todosContainerAdapter: TodosContainerAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = this?.getPreferences(Context.MODE_PRIVATE) ?: return

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
        val activeTodoNameTextView: TextView = findViewById(R.id.pomodoro_widget_todo_name)
        val activeTodoDateTextView: TextView = findViewById(R.id.pomodoro_widget_todo_date)
        val activeTodoDurationTextView: TextView = findViewById(R.id.pomodoro_widget_todo_duration)

        activeTodoNameTextView.text = activeTodo.name
        activeTodoDateTextView.text = activeTodo.date
        activeTodoDurationTextView.text = "${activeTodo.duration} mins"

        if (activeTodo.date == "") {
            activeTodoDateTextView.visibility = View.GONE
        }
    }
}
