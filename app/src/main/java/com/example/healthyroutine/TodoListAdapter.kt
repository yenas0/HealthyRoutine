package com.example.healthyroutine
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TodoListAdapter(private var todoList: List<String>) :
    RecyclerView.Adapter<TodoListAdapter.TodoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = todoList[position]
        holder.todoText.text = todo
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    fun updateTodoList(newTodoList: List<String>) {
        todoList = newTodoList
        notifyDataSetChanged()
    }

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val todoText: TextView = itemView.findViewById(R.id.todo_text)
    }
}
