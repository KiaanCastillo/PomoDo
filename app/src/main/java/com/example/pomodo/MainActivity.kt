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
        lateinit var database: DatabaseReference
        lateinit var sharedPreferences: SharedPreferences
        lateinit var uid: String

//        lateinit var activeTodo: Todo

        lateinit var todosContainer: RecyclerView
        lateinit var todosContainerAdapter: TodosContainerAdapter

        var todosList = ArrayList<Todo>()
        var addNewTodoDuration = 0
        var addNewTodoDate = ""

        fun addNewTodo(name: String,
                       duration: Int? = null,
                       date: String? = null) {
            val newTodoKey: String = database.child("users").child(uid).child("todos").push().key.toString()
            val newTodo = Todo(newTodoKey, name, duration, date, null)
            database.child("users").child(uid).child("todos").child(newTodoKey!!).setValue(newTodo)

            addNewTodoDuration = 0
            addNewTodoDate = ""
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
            val addNewTodoDialog: TodoDialog = TodoDialog(this)
            addNewTodoDialog.showDialog()
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

    fun createTodoFromSnapshot(snapshot: DataSnapshot) : Todo {
        val readId: String = snapshot.key.toString()
        val readName: String = snapshot.child("name").value.toString()
        val readDuration: Number = snapshot.child("duration").value.toString().toInt()
        val readDate: String = snapshot.child("date").value.toString()
        val readCompleteDate: Number
        var todo: Todo

        if (snapshot.child("completeDate").exists()) {
            readCompleteDate = snapshot.child("completeDate").value.toString().toLong()
            todo = Todo(readId, readName, readDuration, readDate, readCompleteDate)
        } else {
            todo = Todo(readId, readName, readDuration, readDate, null)
        }

        return todo
    }
}
