package com.example.pomodo

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

fun MainActivity.initData() {
    MainActivity.todosContainer = findViewById(R.id.todos_container)
    MainActivity.todosContainer.layoutManager = LinearLayoutManager(this)

    MainActivity.todosContainerAdapter = TodosContainerAdapter(MainActivity.todosList)
    MainActivity.todosContainer.adapter = MainActivity.todosContainerAdapter

    MainActivity.database
        .child(getString(R.string.database_users_collection_key))
        .child(MainActivity.uid)
        .child(getString(R.string.database_todos_collection_key)).addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val readId: String = snapshot.key.toString()
                val readName: String = snapshot.child("name").value.toString()
                val readDuration: Number = snapshot.child("duration").value.toString().toInt()
                val readDate: String = snapshot.child("date").value.toString()
                val readCompleteDate: Number
                var readTodo: Todo

                if (snapshot.child("completeDate").exists()) {
                    readCompleteDate = snapshot.child("completeDate").value.toString().toLong()
                    readTodo = Todo(readId, readName, readDuration, readDate, readCompleteDate)
                } else {
                    readTodo = Todo(readId, readName, readDuration, readDate, null)
                }


                if (previousChildName.toString() == "null") {
                    MainActivity.activeTodo = readTodo
                    displayActiveTodo()
                } else {
                    MainActivity.todosContainerAdapter.addItem(readTodo)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.i("PomoDo", "Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.i("PomoDo", "Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.i("PomoDo", "Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("PomoDo", "Not yet implemented")
            }

        })
}