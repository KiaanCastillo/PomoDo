package com.example.pomodo

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pomodo.Todo.Companion.createTodoFromSnapshot
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.example.pomodo.MainActivity.Companion.todosContainer
import com.example.pomodo.MainActivity.Companion.todosContainerAdapter
import com.example.pomodo.MainActivity.Companion.database
import com.example.pomodo.MainActivity.Companion.timeCompleteToday
import com.example.pomodo.MainActivity.Companion.todosCompleteToday

fun MainActivity.initDataListeners() {
    todosContainer = findViewById(R.id.todos_container)
    todosContainer.layoutManager = LinearLayoutManager(this)

    todosContainerAdapter = TodosContainerAdapter(ArrayList<Todo>(), this)
    todosContainer.adapter = todosContainerAdapter

    updateStats()

    database.setChildEventListener(object :
        ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            var addedTodo: Todo = createTodoFromSnapshot(snapshot)
            todosContainerAdapter.addItem(addedTodo)

            if (addedTodo.checked()) {
                if (addedTodo.completedToday()) {
                    todosCompleteToday++
                }

                if (addedTodo.hasDuration()) {
                    timeCompleteToday += addedTodo.duration!!.toInt()
                }
            }
            updateStats()
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            var updatedTodo = createTodoFromSnapshot(snapshot)
            todosContainerAdapter.updateItem(updatedTodo)
            updateStats()
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            var removedTodo = createTodoFromSnapshot(snapshot)
            todosContainerAdapter.removeItem(removedTodo)
            updateStats()
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            Log.i("PomoDo", "Not yet implemented")
        }

        override fun onCancelled(error: DatabaseError) {
            Log.i("PomoDo", "Not yet implemented")
        }
    })
}