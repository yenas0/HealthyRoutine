package com.example.healthyroutine

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ChecklistAdapter(
    private val checklist: MutableList<ChecklistItem>,
    private val onItemClicked: (ChecklistItem, View) -> Unit
) : RecyclerView.Adapter<ChecklistAdapter.ChecklistViewHolder>() {

    private var selectedItem: ChecklistItem? = null

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

        holder.container.setOnClickListener {
            selectedItem = item
            onItemClicked(item, holder.container)
            notifyDataSetChanged()
        }

        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            item.isCompleted = isChecked
            if (isChecked) {
                holder.container.setBackgroundResource(R.drawable.item_background_checked)
            } else {
                holder.container.setBackgroundResource(R.drawable.item_background)
            }
            // RecyclerView가 레이아웃이나 스크롤을 계산할 때 호출되지 않도록 합니다.
            holder.container.post {
                notifyItemChanged(position)
            }
        }

        if (item == selectedItem) {
            holder.container.setBackgroundResource(R.drawable.item_border_selected)
        } else {
            if (item.isCompleted) {
                holder.container.setBackgroundResource(R.drawable.item_background_checked)
            } else {
                holder.container.setBackgroundResource(R.drawable.item_background)
            }
        }
    }

    override fun getItemCount() = checklist.size

    fun updateChecklist(newChecklist: List<ChecklistItem>) {
        checklist.clear()
        checklist.addAll(newChecklist)
        notifyDataSetChanged()
    }

    fun removeChecklistItem(item: ChecklistItem) {
        val position = checklist.indexOf(item)
        if (position >= 0) {
            checklist.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clearSelection() {
        selectedItem = null
        notifyDataSetChanged()
    }
}
