package com.example.pomodo

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pomodo.Database.Companion.createTodoFromSnapshot
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.example.pomodo.MainActivity.Companion.todosContainer
import com.example.pomodo.MainActivity.Companion.todosContainerAdapter
import com.example.pomodo.MainActivity.Companion.database


fun MainActivity.initData() {
    todosContainer = findViewById(R.id.todos_container)
    todosContainer.layoutManager = LinearLayoutManager(this)

    todosContainerAdapter = TodosContainerAdapter(ArrayList<Todo>(), this)
    todosContainer.adapter = todosContainerAdapter

    database.setChildEventListener(object :
        ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            var addedTodo: Todo = createTodoFromSnapshot(snapshot)

            Log.i("onChildAdded", "$addedTodo")

            if (previousChildName.toString() == "null") {
                displayActiveTodo(addedTodo)
            } else {
                todosContainerAdapter.addItem(addedTodo)
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            var updatedTodo = createTodoFromSnapshot(snapshot)
            todosContainerAdapter.updateItem(updatedTodo)
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            var removedTodo = createTodoFromSnapshot(snapshot)
            todosContainerAdapter.removeItem(removedTodo)
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            Log.i("PomoDo", "Not yet implemented")
        }

        override fun onCancelled(error: DatabaseError) {
            Log.i("PomoDo", "Not yet implemented")
        }

    })
}