package com.example.pomodo

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.pomodo.MainActivity.Companion.database
import androidx.recyclerview.widget.RecyclerView
import com.example.pomodo.MainActivity.Companion.todosCompleteToday
import com.example.pomodo.MainActivity.Companion.timeCompleteToday
import com.example.pomodo.TodoDialog
import java.util.*
import java.util.concurrent.TimeUnit

class TodosContainerAdapter(private val todos: ArrayList<Todo>, private val context: Context) :
    RecyclerView.Adapter<TodosContainerAdapter.ViewHolder>() {
    private var activeTodo: Todo = Todo("", "")
    private lateinit var timer: CountDownTimer

    private var activeTodoWidget: LinearLayout = (context as Activity).findViewById<View>(R.id.pomodoro_widget) as LinearLayout
    private var activeTodoNameTextView: TextView = (context as Activity).findViewById<View>(R.id.pomodoro_widget_name) as TextView
    private var activeTodoDateTextView: TextView = (context as Activity).findViewById<TextView>(R.id.pomodoro_widget_date) as TextView
    private var activeTodoDurationTextView: TextView = (context as Activity).findViewById<TextView>(R.id.pomodoro_widget_duration) as TextView
    private var activeTodoCheckbox: CheckBox = (context as Activity).findViewById<CheckBox>(R.id.pomodoro_widget_checkbox) as CheckBox
    private var activeTodoTimer: TextView = (context as Activity).findViewById<TextView>(R.id.pomodoro_widget_timer) as TextView
    private var activeTodoStartButton: Button = (context as Activity).findViewById<Button>(R.id.pomodoro_widget_start_button) as Button
    private var activeTodoCompleteButton: Button = (context as Activity).findViewById<Button>(R.id.pomodoro_widget_complete_button) as Button


    class ViewHolder(val todoWidget: LinearLayout) : RecyclerView.ViewHolder (todoWidget) {
        val name: TextView = todoWidget.findViewById<TextView>(R.id.name)
        val date: TextView = todoWidget.findViewById<TextView>(R.id.date)
        val duration: TextView = todoWidget.findViewById<TextView>(R.id.duration)
        val checkbox: CheckBox = todoWidget.findViewById<CheckBox>(R.id.checkbox)
    }

    init {
        activeTodoWidget.setOnClickListener {
            if (isActiveTodoInitialized()) {
                val editTodoDialog: TodoDialog = TodoDialog(context, activeTodo, true)
                editTodoDialog.showDialog()
            }
        }

        activeTodoStartButton.setOnClickListener {
            if (isActiveTodoInitialized()) {
                startPomodoro()
                activeTodoStartButton.visibility = View.GONE
            } else {
                Toast.makeText(context, "Long press on a todo to start a Pomodoro session for it", Toast.LENGTH_SHORT).show()
            }
        }

        activeTodoCheckbox.isEnabled = false

        activeTodoCheckbox.setOnClickListener {
            completeActiveTodo()
        }

        activeTodoCompleteButton.setOnClickListener {
            completeActiveTodo()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val todoWidget = LayoutInflater.from(parent.context).inflate(R.layout.widget_todo, parent, false) as LinearLayout
        return ViewHolder(todoWidget)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var todo = todos[position]
        holder.name.text = todo.name
        holder.checkbox.isChecked = todo.checked()

        if (todo.hasDate()) {
            holder.date.visibility = View.VISIBLE
            holder.date.text = todo.date
        } else {
            holder.date.visibility = View.GONE
        }

        if (todo.hasDuration()) {
            holder.duration.visibility = View.VISIBLE
            holder.duration.text = "${todo.duration} mins"
        } else {
            holder.duration.visibility = View.GONE
        }

        holder.todoWidget.setOnClickListener {
            val editTodoDialog: TodoDialog = TodoDialog(context, todo)
            editTodoDialog.showDialog()
        }

        holder.todoWidget.setOnLongClickListener {
            todoLongPressListener(todo)
            true
        }

        holder.checkbox.setOnClickListener {
            todoCheckboxListener(todo)
        }
    }

    fun addItem(newTodo: Todo) {
        if (newTodo.checked()) {
            todos.add(newTodo)
        } else {
            addTodoToFront(newTodo)
        }
        notifyDataSetChanged()
    }

    fun removeItem(todo: Todo) {
        todos.remove(todo)
        notifyDataSetChanged()
    }

    fun updateItem(todo: Todo) {
        if (isActiveTodoInitialized() && todo.id == activeTodo.id) {
            todos.add(todo)
            activeTodo.resetTodo()
        } else {
            val index = findIndexOfTodo(todo)

            if (index == -1) {
                Toast.makeText(context, "Could not update todo", Toast.LENGTH_SHORT).show()
                return
            }

            todos[index] = todo
        }
        notifyDataSetChanged()
    }

    private fun todoCheckboxListener(todo: Todo) {
        if (todo.checked()) {
            todo.uncheck()
            todos.remove(todo)
            addTodoToFront(todo)
            todosCompleteToday--

            if (todo.hasDuration()) {
                timeCompleteToday -= todo.duration?.toInt() ?: 0
            }
        } else {
            todo.check()
            todos.remove(todo)
            todos.add(todo)
            todosCompleteToday++

            if (todo.hasDuration()) {
                timeCompleteToday += todo.duration?.toInt() ?: 0
            }
        }
        notifyDataSetChanged()
        database.updateTodo(todo)
    }

    private fun addTodoToFront(todo: Todo) {
        todos.add(0, todo)
    }

    private fun findIndexOfTodo(todo: Todo) : Int {
        for ((index, currentTodo: Todo) in todos.withIndex()) {
            if (currentTodo.id === todo.id) {
                return index
            }
        }
        return -1
    }

    private fun todoLongPressListener(todo: Todo) {
        if (todo.checked()) {
            Toast.makeText(context, "Todo must be incomplete in order to start a Pomodoro", Toast.LENGTH_SHORT).show()
            return
        }

        if (!todo.hasDuration()) {
            Toast.makeText(context, "Todo must have duration in order to start a Pomodoro", Toast.LENGTH_SHORT).show()
            val editTodoDialog: TodoDialog = TodoDialog(context, todo)
            editTodoDialog.showDialog()
            return
        }

        if (isActiveTodoInitialized()) {
            addTodoToFront(activeTodo)
        }

        activeTodo = todo
        displayActiveTodo()

        todos.remove(todo)
        notifyDataSetChanged()
    }

    private fun displayActiveTodo() {
        activeTodoNameTextView.text = activeTodo.name
        activeTodoCheckbox.isChecked = activeTodo.checked()
        activeTodoCheckbox.isEnabled = true

        activeTodoDurationTextView.text = "${activeTodo.duration} mins"
        activeTodoTimer.text = "${activeTodo.duration}:00"


        if (activeTodo.hasDate()) {
            activeTodoDateTextView.text = activeTodo.date
            activeTodoDateTextView.visibility = View.VISIBLE
        } else {
            activeTodoDateTextView.visibility = View.GONE
        }
    }

    private fun resetActiveTodoDisplay() {
        val defaultText = "--"
        activeTodoNameTextView.text = defaultText

        activeTodoDurationTextView.text = defaultText

        activeTodoDateTextView.text = defaultText
        activeTodoDateTextView.visibility = View.VISIBLE

        activeTodoTimer.text = defaultText
        activeTodoCheckbox.isChecked = false
        activeTodoCheckbox.isEnabled = false
    }

    private fun isActiveTodoInitialized() = activeTodo.id != ""

    private fun startPomodoro() {
        val durationInMillis = activeTodo.duration?.toLong()?.times(60000)
        timer = object: CountDownTimer(durationInMillis!!, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val durationRemainingString = String.format(
                    "%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
                )
                activeTodoTimer.text = durationRemainingString
            }

            override fun onFinish() {
                Toast.makeText(context, "Finished", Toast.LENGTH_SHORT).show()
            }
        }
        timer.start()
        activeTodoCompleteButton.visibility = View.VISIBLE
    }

    private fun completeActiveTodo() {
        if (isActiveTodoInitialized()) {
            resetActiveTodoDisplay()
            activeTodo.check()

            todosCompleteToday++

            if (activeTodo.hasDuration()) {
                timeCompleteToday += activeTodo.duration?.toInt() ?: 0
            }

            database.updateTodo(activeTodo)
            timer.cancel()
        }

        activeTodoCompleteButton.visibility = View.GONE
        activeTodoStartButton.visibility = View.VISIBLE
    }

    override fun getItemCount() = todos.size
}