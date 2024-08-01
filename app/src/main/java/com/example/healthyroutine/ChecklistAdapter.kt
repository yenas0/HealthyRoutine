package com.example.healthyroutine

import ChecklistItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ChecklistAdapter(private val checklist: MutableList<ChecklistItem>) :
    RecyclerView.Adapter<ChecklistAdapter.ChecklistViewHolder>() {

    class ChecklistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val routineName: TextView = itemView.findViewById(R.id.routine_name)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
        val container: View = itemView.findViewById(R.id.container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_checklist, parent, false)
        return ChecklistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        val item = checklist[position]
        holder.routineName.text = item.name
        holder.checkBox.isChecked = item.isCompleted

        // 체크박스 상태에 따라 UI 변경
        updateItemUI(holder, item.isCompleted)

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            item.isCompleted = isChecked
            updateItemUI(holder, isChecked)
        }
    }

    private fun updateItemUI(holder: ChecklistViewHolder, isCompleted: Boolean) {
        if (isCompleted) {
            holder.container.setBackgroundResource(R.drawable.item_background_checked)
            holder.routineName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        } else {
            holder.container.setBackgroundResource(R.drawable.item_background)
            holder.routineName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
        }
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
