package com.example.pomodo

import android.text.format.DateUtils
import com.google.firebase.database.DataSnapshot
import java.util.*

class Todo(
    var id: String,
    var name: String,
    var duration: Number? = null,
    var date: String? = null,
    var completeDate: Long? = null) {

    companion object {
        fun createTodoFromSnapshot(snapshot: DataSnapshot) : Todo {
            var readId: String = snapshot.key.toString()
            var readName: String = snapshot.child("name").value.toString()
            var todo: Todo = Todo(readId, readName)

            if (snapshot.child("duration").exists()) {
                todo.duration = snapshot.child("duration").value.toString().toInt()
            }

            if (snapshot.child("date").exists()) {
                todo.date = snapshot.child("date").value.toString()
            }

            if (snapshot.child("completeDate").exists()) {
                todo.completeDate = snapshot.child("completeDate").value.toString().toLong()
            }

            return todo
        }
    }

    fun hasDuration() = duration != null

    fun hasDate() = date != null

    fun checked() = completeDate != null

    fun completedToday() = DateUtils.isToday(completeDate!!)

    fun clearDuration() { duration = null }

    fun clearDate() { date = null }

    fun clearCompleteDate() { completeDate = null }

    fun resetTodo() {
        clearDuration()
        clearDate()
        clearCompleteDate()
        id = ""
        name = ""
    }

    fun check() {
        val calendar = Calendar.getInstance()
        completeDate = calendar.timeInMillis
    }

    fun uncheck() { completeDate = null }

}