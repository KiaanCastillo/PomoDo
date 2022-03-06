package com.example.pomodo

import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

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