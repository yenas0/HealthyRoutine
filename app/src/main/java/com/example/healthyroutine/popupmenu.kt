package com.example.healthyroutine

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class popupmenu {
}// 루틴 항목을 나타내는 데이터 클래스
data class Routine(val id: Int, val name: String)

// RecyclerView 어댑터
class RoutineAdapter(private val routines: List<Routine>, private val onClick: (Routine) -> Unit) :
    RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder>(), Parcelable {

    inner class RoutineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(routine: Routine) {
            itemView.findViewById<TextView>(R.id.routine_name).text = routine.name
            itemView.setOnClickListener {
                onClick(routine)
            }
        }
    }

    constructor(parcel: Parcel) : this(
        TODO("routines"),
        TODO("onClick")
    ) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_routine, parent, false)
        return RoutineViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        holder.bind(routines[position])
    }

    override fun getItemCount(): Int = routines.size
    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RoutineAdapter> {
        override fun createFromParcel(parcel: Parcel): RoutineAdapter {
            return RoutineAdapter(parcel)
        }

        override fun newArray(size: Int): Array<RoutineAdapter?> {
            return arrayOfNulls(size)
        }
    }
}

// MainActivity.kt
class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val routines = listOf(Routine(1, "Routine 1"), Routine(2, "Routine 2"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RoutineAdapter(routines) { routine ->
            showPopupMenu(routine)
        }
    }

    private fun showPopupMenu(routine: Routine) {
        val popupMenu = PopupMenu(this, findViewById(R.id.recycler_view))
        popupMenu.menuInflater.inflate(R.menu.routine_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_monthly_stats -> {
                    showMonthlyStats(routine)
                    true
                }
                R.id.menu_edit -> {
                    // 수정하기 로직
                    true
                }
                R.id.menu_delete -> {
                    // 삭제하기 로직
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun showMonthlyStats(routine: Routine) {
        // 월간 통계 표시 로직
        val intent = Intent(this, MonthlyStatsActivity::class.java)
        intent.putExtra("ROUTINE_ID", routine.id)
        startActivity(intent)
    }
}
