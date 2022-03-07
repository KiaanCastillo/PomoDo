package com.example.pomodo

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList

class TodosContainerAdapter(private val todos : ArrayList<Todo>) :
    RecyclerView.Adapter<TodosContainerAdapter.ViewHolder>() {
    class ViewHolder(todoWidget: LinearLayout) : RecyclerView.ViewHolder (todoWidget) {
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
        val todo = todos[position]
        holder.name.text = todo.name
        holder.date.text = todo.date
        holder.duration.text = "${todo.duration} mins"
        holder.checkbox.isChecked = todo.completeDate != null
    }

    override fun getItemCount() = todos.size
}