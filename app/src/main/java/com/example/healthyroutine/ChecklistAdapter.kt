package com.example.healthyroutine

import ChecklistItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ChecklistAdapter(
    private val checklist: MutableList<ChecklistItem>,
    private val onItemClicked: (ChecklistItem, View) -> Unit // View parameter 추가
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
            onItemClicked(item, holder.container) // View를 콜백으로 전달
            selectedItem = item
            notifyDataSetChanged()
        }

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            item.isCompleted = isChecked
            if (isChecked) {
                holder.container.setBackgroundResource(R.drawable.item_background_checked)
            } else {
                holder.container.setBackgroundResource(R.drawable.item_background)
            }
            holder.checkBox.post {
                notifyItemChanged(position)
            }
        }

        if (item == selectedItem) {
            holder.container.setBackgroundResource(R.drawable.item_border_selected) // 검정색 외곽선 추가
        } else {
            holder.container.setBackgroundResource(
                if (item.isCompleted) R.drawable.item_background_checked
                else R.drawable.item_background
            )
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
