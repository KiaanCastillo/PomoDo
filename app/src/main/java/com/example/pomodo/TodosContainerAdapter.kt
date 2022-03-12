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
        val name: TextView = todoWidget.findViewById<TextView>(R.id.name)
        val date: TextView = todoWidget.findViewById<TextView>(R.id.date)
        val duration: TextView = todoWidget.findViewById<TextView>(R.id.duration)
        val checkbox: CheckBox = todoWidget.findViewById<CheckBox>(R.id.checkbox)
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
        val index = findIndexOfTodo(todo)

        if (index == -1) {
            return
        }

        todos[index] = todo
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

    override fun getItemCount() = todos.size
}