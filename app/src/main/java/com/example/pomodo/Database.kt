package com.example.pomodo

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Database {
    var uid: String
    private val userCollectionKey = "users"
    private val todosCollectionKey = "todos"

    private var database : DatabaseReference = Firebase.database.reference
    private var userTodosDatabaseReference: DatabaseReference

    companion object {
        fun createTodoFromSnapshot(snapshot: DataSnapshot) : Todo {
            val readId: String = snapshot.key.toString()
            val readName: String = snapshot.child("name").value.toString()
            val readDuration: Number = snapshot.child("duration").value.toString().toInt()
            val readDate: String = snapshot.child("date").value.toString()
            val readCompleteDate: Number
            var todo: Todo

            if (snapshot.child("completeDate").exists()) {
                readCompleteDate = snapshot.child("completeDate").value.toString().toLong()
                todo = Todo(readId, readName, readDuration, readDate, readCompleteDate)
            } else {
                todo = Todo(readId, readName, readDuration, readDate, null)
            }

            return todo
        }
    }

    constructor(uid: String) {
        this.uid = uid
        userTodosDatabaseReference = database.child(userCollectionKey).child(uid).child(todosCollectionKey)
        database.child(userCollectionKey).child(uid).child("uid").setValue(uid)
    }

    constructor() {
        uid = database.child(userCollectionKey).push().key.toString()
        userTodosDatabaseReference = database.child(userCollectionKey).child(uid).child(todosCollectionKey)
        database.child(userCollectionKey).child(uid).child("uid").setValue(uid)
    }

    fun addNewTodo(newTodo: Todo) {
        val newTodoKey: String = userTodosDatabaseReference.push().key.toString()
        newTodo.id = newTodoKey
        userTodosDatabaseReference.child(newTodoKey!!).setValue(newTodo)
    }

    fun deleteTodo(deletedTodo: Todo) { userTodosDatabaseReference.child(deletedTodo.id).removeValue() }

    fun updateTodo(updatedTodo: Todo) { userTodosDatabaseReference.child(updatedTodo.id).setValue(updatedTodo) }

    fun setChildEventListener(listener : ChildEventListener) {
        userTodosDatabaseReference.addChildEventListener(listener)
    }
}