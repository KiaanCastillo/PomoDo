package com.example.pomodo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.example.pomodo.MainActivity.Companion.database
import androidx.recyclerview.widget.RecyclerView
import com.example.pomodo.MainActivity.Companion.todosCompleteToday
import com.example.pomodo.MainActivity.Companion.timeCompleteToday
import java.util.*

class TodosContainerAdapter(private val todos: ArrayList<Todo>, private val context: Context) :
    RecyclerView.Adapter<TodosContainerAdapter.ViewHolder>() {
    lateinit var activeTodo: Todo

    class ViewHolder(val todoWidget: LinearLayout) : RecyclerView.ViewHolder (todoWidget) {
        val name: TextView = todoWidget.findViewById<TextView>(R.id.todo_widget_name)
        val date: TextView = todoWidget.findViewById<TextView>(R.id.pomodoro_widget_todo_date)
        val duration: TextView = todoWidget.findViewById<TextView>(R.id.pomodoro_widget_todo_duration)
        val checkbox: CheckBox = todoWidget.findViewById<CheckBox>(R.id.todo_widget_checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val todoWidget = LayoutInflater.from(parent.context).inflate(R.layout.widget_todo, parent, false) as LinearLayout
        return ViewHolder(todoWidget)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var todo = todos[position]
        holder.name.text = todo.name
        holder.checkbox.isChecked = todo.completeDate.toString() != "null"

        if (todo.date.toString().isEmpty()) {
            holder.date.visibility = View.GONE
        } else {
            holder.date.visibility = View.VISIBLE
            holder.date.text = todo.date
        }

        if (todo.duration == 0) {
            holder.duration.visibility = View.GONE
        } else {
            holder.duration.visibility = View.VISIBLE
            holder.duration.text = "${todo.duration} mins"
        }

        holder.todoWidget.setOnClickListener {
            val editTodoDialog: TodoDialog = TodoDialog(context, todo)
            editTodoDialog.showDialog()
        }

        holder.checkbox.setOnClickListener {
            todoCheckboxListener(todo)
        }

    }

    fun addItem(newTodo: Todo) {

        todos.add(0, newTodo)
        notifyDataSetChanged()
    }

    fun removeItem(todo: Todo) {
        todos.remove(todo)
        notifyDataSetChanged()
    }

    fun updateItem(todo: Todo) {
        todos[todos.indexOf(todo)] = todo
        notifyDataSetChanged()
    }

    private fun todoCheckboxListener(todo: Todo) {
        if (todo.completeDate != null) {
            todo.completeDate = null
            todos.remove(todo)
            todos.add(0, todo)
            todosCompleteToday--

            if (todo.duration != 0) {
                timeCompleteToday -= todo.duration?.toInt() ?: 0
            }

        } else {
            val calendar = Calendar.getInstance()
            todo.completeDate = calendar.timeInMillis
            todos.remove(todo)
            todos.add(todo)
            todosCompleteToday++

            if (todo.duration != 0) {
                timeCompleteToday += todo.duration?.toInt() ?: 0
            }
        }
        notifyDataSetChanged()
        database.updateTodo(todo)
    }

    override fun getItemCount() = todos.size
}