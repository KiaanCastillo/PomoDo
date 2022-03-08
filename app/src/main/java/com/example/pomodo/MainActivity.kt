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
    val id: String,
    val name: String,
    val duration: Number? = null,
    val date: String? = null,
    var completeDate: Number? = null
)

class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var database: DatabaseReference
        lateinit var sharedPreferences: SharedPreferences
        lateinit var uid: String

        lateinit var activeTodo: Todo

        lateinit var todosContainer: RecyclerView
        lateinit var todosContainerAdapter: TodosContainerAdapter

        var todosList = ArrayList<Todo>()
        var addNewTodoDuration = 0
        var addNewTodoDate = ""

        fun showAddNewTodoDialog() {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = Firebase.database.reference
        sharedPreferences = this?.getPreferences(Context.MODE_PRIVATE) ?: return

        initUser()
        initData()
        initListeners()
    }

    private fun initUser() {
        if (sharedPreferences.contains(getString(R.string.shared_preferences_uid_key))) {
            uid = sharedPreferences.getString(getString(R.string.shared_preferences_uid_key), "")!!
        } else {
            createUser()
        }
    }

    private fun initListeners() {
        val addNewTodoButton: Button = findViewById(R.id.show_add_new_todo_button)
        addNewTodoButton.setOnClickListener {
            showAddNewTodoDialog(addNewTodoButton)
        }
    }

    private fun createUser() {
        with (sharedPreferences.edit()) {
            val newUid = database.child(getString(R.string.database_users_collection_key)).push().key
            putString(getString(R.string.shared_preferences_uid_key), newUid)
            apply()
            uid = newUid!!
            database.child(getString(R.string.database_users_collection_key)).child(uid).child("uid").setValue(uid)
        }
    }

    fun displayActiveTodo() {
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

    fun addNewTodo(name: String,
                   duration: Int? = null,
                   date: String? = null) {
        val newTodoKey: String = database.child(getString(R.string.database_users_collection_key)).child(uid).child(getString(R.string.database_todos_collection_key)).push().key.toString()
        val newTodo = Todo(newTodoKey, name, duration, date, null)
        database.child(getString(R.string.database_users_collection_key)).child(uid).child(getString(R.string.database_todos_collection_key)).child(newTodoKey!!).setValue(newTodo)

        addNewTodoDuration = 0
        addNewTodoDate = ""
    }
}
