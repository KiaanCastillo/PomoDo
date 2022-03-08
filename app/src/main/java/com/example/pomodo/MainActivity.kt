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
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList

data class Todo(
    val id: String,
    val name: String,
    val duration: Int? = null,
    val date: String? = null,
    val completeDate: Calendar? = null
)

class MainActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    lateinit var sharedPreferences: SharedPreferences
    lateinit var uid: String

    lateinit var activeTodo: Todo

    lateinit var todosContainer: RecyclerView
    lateinit var todosContainerAdapter: TodosContainerAdapter

    var todosList = ArrayList<Todo>()
    var addNewTodoDuration = 0
    var addNewTodoDate = ""

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

    private fun initData() {
        todosContainer = findViewById(R.id.todos_container)
        todosContainer.layoutManager = LinearLayoutManager(this)

        todosContainerAdapter = TodosContainerAdapter(todosList)
        todosContainer.adapter = todosContainerAdapter

        database
            .child(getString(R.string.database_users_collection_key))
            .child(uid)
            .child(getString(R.string.database_todos_collection_key)).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val readId: String = snapshot.key.toString()
                val readName: String = snapshot.child("name").value.toString()
                val readDuration: Int = snapshot.child("duration").value.toString().toInt()
                val readDate: String = snapshot.child("date").value.toString()


                val readTodo = Todo(readId, readName, readDuration, readDate)

                if (previousChildName.toString() == "null") {
                    activeTodo = readTodo
                    displayActiveTodo()
                } else {
                    todosContainerAdapter.addItem(readTodo)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.i("PomoDo", "Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.i("PomoDo", "Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.i("PomoDo", "Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("PomoDo", "Not yet implemented")
            }

        })
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

    private fun displayActiveTodo() {
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
