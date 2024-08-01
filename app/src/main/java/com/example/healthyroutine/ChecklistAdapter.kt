package com.example.healthyroutine

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChecklistAdapter(private val checklist: MutableList<ChecklistItem>) :
    RecyclerView.Adapter<ChecklistAdapter.ChecklistViewHolder>() {

    class ChecklistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskName: TextView = itemView.findViewById(R.id.routine_name)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_checklist, parent, false)
        return ChecklistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        val item = checklist[position]
        holder.taskName.text = item.name
        holder.checkBox.isChecked = item.isCompleted
    }

    override fun getItemCount() = checklist.size

    fun updateChecklist(newChecklist: List<ChecklistItem>) {
        checklist.clear()
        checklist.addAll(newChecklist)
        notifyDataSetChanged()
    }

    fun addRoutine(routine: Routine) {
        checklist.add(ChecklistItem(routine.name, false))
        notifyDataSetChanged()
    }
}
