package com.example.healthyroutine

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate

data class Routine(val name: String, val startDate: LocalDate, val days: String, val notificationEnabled: Boolean)
class ChecklistAdapter(private val routines: MutableList<Routine>) : RecyclerView.Adapter<ChecklistAdapter.RoutineViewHolder>() {

    fun addRoutine(routine: Routine) {
        routines.add(routine)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_checklist, parent, false)
        return RoutineViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        val routine = routines[position]
        holder.bind(routine)
    }

    override fun getItemCount() = routines.size

    inner class RoutineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
        private val routineNameTextView: TextView = itemView.findViewById(R.id.routine_name)

        fun bind(routine: Routine) {
            routineNameTextView.text = routine.name
            // 다른 루틴 정보를 표시하거나 활용할 수 있습니다.
        }
    }
}
