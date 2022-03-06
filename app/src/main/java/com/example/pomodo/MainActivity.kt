package com.example.pomodo

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

data class Todo(
    val name: String,
//    val duration: Int? = null,
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
    }

    private fun initUser() {
        if (sharedPreferences.contains(getString(R.string.shared_preferences_uid_key))) {
            uid = sharedPreferences.getString(getString(R.string.shared_preferences_uid_key), "")!!
        } else {
            createUser()
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
//                   duration: Int? = null,
                   date: String? = null) {
        val newTodo = Todo(name, date, null)
        val newTodoKey = database.child(getString(R.string.database_users_collection_key)).child(uid).child(getString(R.string.database_todos_collection_key)).push().key
        database.child(getString(R.string.database_users_collection_key)).child(uid).child(getString(R.string.database_todos_collection_key)).child(newTodoKey!!).setValue(newTodo)
    }

    fun showAddNewTodoDialog(view: View) {
        val addNewTodoDialog: Dialog = Dialog(this)
        addNewTodoDialog.setContentView(R.layout.dialog_add_new_todo)
        addNewTodoDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val nameInput: EditText = addNewTodoDialog.findViewById(R.id.add_new_todo_input_name) as EditText
        val addNewTodoButton: Button = addNewTodoDialog.findViewById(R.id.add_new_todo_button) as Button
        val durationInputButton: Button = addNewTodoDialog.findViewById(R.id.add_new_todo_duration_button) as Button
        val dateInputButton: Button = addNewTodoDialog.findViewById(R.id.add_new_todo_date_button) as Button

        val calendar = Calendar.getInstance()
        val dateFormat = "EEE, MMM d"

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        dateInputButton.setOnClickListener {
            val dateSetListener = DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val dateTagContainer: FrameLayout = addNewTodoDialog.findViewById(R.id.add_new_todo_duration_tag_container) as FrameLayout
                dateTagContainer.removeAllViews()

                val dateTag = inflater.inflate(R.layout.component_add_new_todo_detail_tag, null)
                val dateTagText = dateTag.findViewById<TextView>(R.id.add_new_todo_detail_tag_text) as TextView
                val dateTagDeleteButton = dateTag.findViewById<Button>(R.id.add_new_todo_detail_tag_delete_button) as Button

                dateTagText.text = SimpleDateFormat(dateFormat, Locale.CANADA).format(calendar.time)
                dateTagDeleteButton.setOnClickListener {
                    dateTagContainer.removeView(dateTag)
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, monthOfYear)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    addNewTodoDate = ""
                }

                dateTagContainer.addView(dateTag, dateTagContainer.childCount - 1)
                addNewTodoDate = SimpleDateFormat(dateFormat, Locale.CANADA).format(calendar.time)
            }

            DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        addNewTodoButton.setOnClickListener {
            val name = nameInput.text.toString()

            addNewTodo(name, addNewTodoDate)

            addNewTodoDialog.dismiss()
        }

        addNewTodoDialog.show()
    }
}