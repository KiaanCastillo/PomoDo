package com.example.pomodo

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import com.google.firebase.database.*

class TodosContainerAdapter(val todos : ArrayList<Todo>, val context : Context) :
    RecyclerView.Adapter<TodosContainerAdapter.ViewHolder>() {
    lateinit var activeTodo: Todo

    class ViewHolder(val todoWidget: LinearLayout) : RecyclerView.ViewHolder (todoWidget) {
        val name: TextView = todoWidget.findViewById<TextView>(R.id.pomodoro_widget_todo_name)
        val date: TextView = todoWidget.findViewById<TextView>(R.id.pomodoro_widget_todo_date)
        val duration: TextView = todoWidget.findViewById<TextView>(R.id.pomodoro_widget_todo_duration)
        val checkbox: CheckBox = todoWidget.findViewById<CheckBox>(R.id.pomodoro_widget_checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val todoWidget = LayoutInflater.from(parent.context).inflate(R.layout.widget_todo, parent, false) as LinearLayout
        return ViewHolder(todoWidget)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var todo = todos[position]
        holder.name.text = todo.name
        holder.checkbox.isChecked = todo.completeDate.toString() != "null"

        if (todo.date.toString() == "") {
            holder.date.visibility = View.GONE
        } else {
            holder.date.text = todo.date
        }

        if (todo.duration == 0) {
            holder.duration.visibility = View.GONE
        } else {
            holder.duration.text = "${todo.duration} mins"
        }

        holder.todoWidget.setOnClickListener {
            showEditTodoDialog(holder.todoWidget, todo)
        }

//        holder.todoWidget.setOnLongClickListener {
//            Log.i("PomoDo", "Long Pressed: ${todo.name}")
//        }

        holder.checkbox.setOnClickListener {
            if (todo.completeDate.toString() != "null") {
                todo.completeDate = null
            } else {
                val calendar = Calendar.getInstance()
                todo.completeDate = calendar.timeInMillis
                todos.remove(todo)
                todos.add(todo)
                notifyDataSetChanged()
            }

            MainActivity.database.child("users").child(MainActivity.uid).child("todos").child(todo.id).setValue(todo)
        }
    }

    fun addItem(newTodo: Todo) {
        todos.add(0, newTodo)
        notifyDataSetChanged()
    }

    override fun getItemCount() = todos.size
}