package com.example.pomodo

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

data class Todo(
    val name: String,
    val duration: Int? = null,
    val date: String? = null,
    val completeDate: Calendar? = null
)

class MainActivity : AppCompatActivity() {
    lateinit var database: DatabaseReference
    lateinit var sharedPreferences: SharedPreferences
    lateinit var uid: String

    var addNewTodoDuration = 0
    var addNewTodoDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = Firebase.database.reference
        sharedPreferences = this?.getPreferences(Context.MODE_PRIVATE) ?: return

        initUser()
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

    fun addNewTodo(name: String,
                   duration: Int? = null,
                   date: String? = null) {
        val newTodo = Todo(name, duration, date, null)
        val newTodoKey = database.child(getString(R.string.database_users_collection_key)).child(uid).child(getString(R.string.database_todos_collection_key)).push().key
        database.child(getString(R.string.database_users_collection_key)).child(uid).child(getString(R.string.database_todos_collection_key)).child(newTodoKey!!).setValue(newTodo)

        addNewTodoDuration = 0
        addNewTodoDate = ""
    }
}