package com.example.pomodo

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

data class Todo(
    val name: String,
    val duration: Int? = null,
    val date: Date? = null,
    val isChecked: Boolean,
//    val createdAt: Date,
//    val updatedAt: Date
)

class MainActivity : AppCompatActivity() {
    lateinit var database: DatabaseReference
    lateinit var sharedPreferences: SharedPreferences
    lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = Firebase.database.reference
        sharedPreferences = this?.getPreferences(Context.MODE_PRIVATE) ?: return

        initUser()
    }

    private fun initUser() {
        if (sharedPreferences.contains(getString(R.string.shared_preferences_uid_key))) {
            uid = sharedPreferences.getString(getString(R.string.shared_preferences_uid_key), "")!!
        } else {
            createUser()
        }
        Toast.makeText(this, "My UID: $uid", Toast.LENGTH_LONG).show()
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

//    fun addNewTodo(name: String,
//                   duration: Int? = null,
//                   date: Date? = null,
//                   isChecked: Boolean,
//                   createdAt: Date,
//                   updatedAt: Date) {
//        val newTodo = Todo(name, duration, date, isChecked)
//        database.child("users").child(userId).setValue(user)
//    }

    fun showAddNewTodoDialog(view: View) {
        val addNewTodoDialog: Dialog = Dialog(this)
        addNewTodoDialog.setContentView(R.layout.dialog_new_todo)
        addNewTodoDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val nameInput: EditText = addNewTodoDialog.findViewById(R.id.add_new_todo_input_name) as EditText
        val addNewTodoButton: Button = addNewTodoDialog.findViewById(R.id.add_new_todo_button) as Button
        val durationInputButton: Button = addNewTodoDialog.findViewById(R.id.add_new_todo_duration_button) as Button
        val dateInputButton: Button = addNewTodoDialog.findViewById(R.id.add_new_todo_date_button) as Button

        addNewTodoButton.setOnClickListener {
            Toast.makeText(this, "Added new todo ${nameInput.text}", Toast.LENGTH_LONG).show()
        }

        addNewTodoDialog.show()
    }
}