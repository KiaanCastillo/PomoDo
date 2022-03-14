package com.example.pomodo

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.example.pomodo.MainActivity.Companion.database
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*

class TodoDialog {
    lateinit var dialog: Dialog
    private var context: Context
    private var inflater: LayoutInflater

    lateinit var nameEditText: EditText
    lateinit var actionButton: Button
    lateinit var durationButton: Button
    lateinit var dateButton: Button
    lateinit var deleteButton: Button

    private var todo: Todo = Todo("", "")

    private val durationsStrings = arrayOf("1 min", "3 mins", "5 mins", "10 mins", "15 mins", "20 mins", "25 mins", "30 mins", "35 mins", "40 mins", "45 mins", "50 mins", "55 mins", "60 mins")
    private val durations = arrayOf(1, 3, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60)

    constructor(context: Context) {
        this.context = context
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        initDialog()
        initFields()

        actionButton.setOnClickListener {
            addNewTodo()
        }

        deleteButton.visibility = View.GONE
    }

    constructor(context: Context, currentTodo: Todo, isActiveTodo: Boolean = false) {
        this.context = context
        this.todo = currentTodo
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        initDialog()
        initFields()

        actionButton.setOnClickListener {
            updateTodo(isActiveTodo)
        }

        actionButton.text = "Save"

        nameEditText.setText(todo.name)
        if (todo.hasDuration()) {
            val durationIndex = durations.indexOf(todo.duration)
            showDurationTag(durationIndex)
        }

        if (todo.hasDate()) {
            showDateTag(todo.date.toString())
        }
    }

    private fun initDialog() {
        dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_add_new_todo)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun initFields() {
        nameEditText = dialog.findViewById(R.id.add_new_todo_input_name) as EditText
        actionButton = dialog.findViewById(R.id.add_new_todo_button) as Button
        durationButton = dialog.findViewById(R.id.add_new_todo_duration_button) as Button
        dateButton = dialog.findViewById(R.id.add_new_todo_date_button) as Button
        deleteButton = dialog.findViewById(R.id.add_new_todo_delete_button) as Button

        durationButton.setOnClickListener {
            showDurationDialog()
        }

        dateButton.setOnClickListener {
            showDateDialog()
        }

        deleteButton.setOnClickListener {
            database.deleteTodo(todo)
            dialog.dismiss()
        }
    }

    fun showDialog() { dialog.show() }

    private fun showDurationDialog() {
        val durationDialogBuilder = AlertDialog.Builder(context)
        durationDialogBuilder.setTitle("Pomodoro Duration")
        durationDialogBuilder.setItems(durationsStrings) { _, selectedDurationIndex: Int -> showDurationTag(selectedDurationIndex) }

        val durationsDialog = durationDialogBuilder.create()
        durationsDialog.show()
    }

    private fun showDurationTag(selectedDurationIndex: Int) {
        val durationTagContainer: FrameLayout = dialog.findViewById(R.id.add_new_todo_duration_tag_container) as FrameLayout
        durationTagContainer.removeAllViews()

        val durationTag = inflater.inflate(R.layout.component_add_new_todo_detail_tag, null)
        val durationTagText = durationTag.findViewById<TextView>(R.id.add_new_todo_detail_tag_text) as TextView
        val durationTagDeleteButton = durationTag.findViewById<Button>(R.id.add_new_todo_detail_tag_delete_button) as Button

        durationTagText.text = durationsStrings[selectedDurationIndex]
        durationTagDeleteButton.setOnClickListener {
            durationTagContainer.removeView(durationTag)
            todo.clearDuration()
        }

        durationTagContainer.addView(durationTag, durationTagContainer.childCount - 1)
        todo.duration = durations[selectedDurationIndex]
    }

    private fun showDateDialog() {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->
            formatSelectedDate(year, month, day)
        }

        DatePickerDialog(
            context,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun formatSelectedDate(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        val dateFormat = "EEE, MMM d"
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)

        val date: String = SimpleDateFormat(dateFormat, Locale.CANADA).format(calendar.time)
        showDateTag(date)
    }

    private fun showDateTag(date: String) {
        val dateTagContainer: FrameLayout = dialog.findViewById(R.id.add_new_todo_date_tag_container) as FrameLayout
        dateTagContainer.removeAllViews()

        val dateTag = inflater.inflate(R.layout.component_add_new_todo_detail_tag, null)
        val dateTagText = dateTag.findViewById<TextView>(R.id.add_new_todo_detail_tag_text) as TextView
        val dateTagDeleteButton = dateTag.findViewById<Button>(R.id.add_new_todo_detail_tag_delete_button) as Button

        dateTagDeleteButton.setOnClickListener {
            dateTagContainer.removeView(dateTag)
            todo.clearDate()
        }

        dateTagContainer.addView(dateTag, dateTagContainer.childCount - 1)

        dateTagText.text = date
        todo.date = date
    }

    private fun addNewTodo() {
        if (isNameEditTextEmpty()) { return }

        val newTodoName: String = nameEditText.text.toString()
        todo.name = newTodoName

        database.addNewTodo(todo)
        dialog.dismiss()
    }

    private fun isNameEditTextEmpty(): Boolean {
        if (nameEditText.text.toString().isEmpty()) {
            Toast.makeText(context, "Todo must have a name", Toast.LENGTH_SHORT).show()
            return true
        }
        return false
    }

    private fun updateTodo(isActiveTodo: Boolean = false) {
        if (isNameEditTextEmpty()) { return }

        if (isActiveTodo && !todo.hasDuration()) {
            Toast.makeText(context, "Active todo must have a duration", Toast.LENGTH_SHORT).show()
            return
        }

        val newTodoName: String = nameEditText.text.toString()
        todo.name = newTodoName

        database.updateTodo(todo)
        dialog.dismiss()
    }
}