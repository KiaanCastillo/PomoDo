package com.example.pomodo

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

fun MainActivity.initData() {
    MainActivity.todosContainer = findViewById(R.id.todos_container)
    MainActivity.todosContainer.layoutManager = LinearLayoutManager(this)

    MainActivity.todosContainerAdapter = TodosContainerAdapter(MainActivity.todosList, this)
    MainActivity.todosContainer.adapter = MainActivity.todosContainerAdapter

    MainActivity.database
        .child(getString(R.string.database_users_collection_key))
        .child(MainActivity.uid)
        .child(getString(R.string.database_todos_collection_key)).addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var addedTodo: Todo = createTodoFromSnapshot(snapshot)

                if (previousChildName.toString() == "null") {
                    displayActiveTodo(addedTodo)
                } else {
                    MainActivity.todosContainerAdapter.addItem(addedTodo)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                var updatedTodo = createTodoFromSnapshot(snapshot)
                MainActivity.todosContainerAdapter.updateItem(updatedTodo)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                var removedTodo = createTodoFromSnapshot(snapshot)
                MainActivity.todosContainerAdapter.removeItem(removedTodo)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.i("PomoDo", "Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("PomoDo", "Not yet implemented")
            }

        })
}