package com.example.pomodo

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*

fun TodosContainerAdapter.showEditTodoDialog(view: View, currentTodo: Todo) {
    val calendar = Calendar.getInstance()
    val dateFormat = "EEE, MMM d"
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val addNewTodoDialog: Dialog = Dialog(context)
    addNewTodoDialog.setContentView(R.layout.dialog_todo)
    addNewTodoDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    val nameInput: EditText = addNewTodoDialog.findViewById(R.id.add_new_todo_input_name) as EditText
    val addNewTodoButton: Button = addNewTodoDialog.findViewById(R.id.add_new_todo_button) as Button
    val durationInputButton: Button = addNewTodoDialog.findViewById(R.id.add_new_todo_duration_button) as Button
    val dateInputButton: Button = addNewTodoDialog.findViewById(R.id.add_new_todo_date_button) as Button

    nameInput.setText(currentTodo.name)



    durationInputButton.setOnClickListener {
        val durationDialogBuilder = AlertDialog.Builder(context)
        val durationsStrings = arrayOf("5 mins", "10 mins", "15 mins", "20 mins", "25 mins", "30 mins", "35 mins", "40 mins", "45 mins", "50 mins", "55 mins", "60 mins")
        val durations = arrayOf(5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60)

        durationDialogBuilder.setTitle("Pomodoro Duration")
        durationDialogBuilder.setItems(durationsStrings) { _, durationIndex: Int ->
            val durationTagContainer: FrameLayout = addNewTodoDialog.findViewById(R.id.add_new_todo_duration_tag_container) as FrameLayout
            durationTagContainer.removeAllViews()

            val durationTag = inflater.inflate(R.layout.component_add_new_todo_detail_tag, null)
            val durationTagText = durationTag.findViewById<TextView>(R.id.add_new_todo_detail_tag_text) as TextView
            val durationTagDeleteButton = durationTag.findViewById<Button>(R.id.add_new_todo_detail_tag_delete_button) as Button

            durationTagText.text = durationsStrings[durationIndex]
            durationTagDeleteButton.setOnClickListener {
                durationTagContainer.removeView(durationTag)
                MainActivity.addNewTodoDuration = 0
            }

            durationTagContainer.addView(durationTag, durationTagContainer.childCount - 1)
            MainActivity.addNewTodoDuration = durations[durationIndex]
        }

        val durationsDialog = durationDialogBuilder.create()
        durationsDialog.show()
    }

    dateInputButton.setOnClickListener {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val dateTagContainer: FrameLayout = addNewTodoDialog.findViewById(R.id.add_new_todo_date_tag_container) as FrameLayout
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
                MainActivity.addNewTodoDate = ""
            }

            dateTagContainer.addView(dateTag, dateTagContainer.childCount - 1)
            MainActivity.addNewTodoDate = SimpleDateFormat(dateFormat, Locale.CANADA).format(calendar.time)
        }

        DatePickerDialog(
            context,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    addNewTodoButton.setOnClickListener {
        val name = nameInput.text.toString()

        MainActivity.addNewTodo(name, MainActivity.addNewTodoDuration, MainActivity.addNewTodoDate)
        addNewTodoDialog.dismiss()
    }

    addNewTodoDialog.show()
}