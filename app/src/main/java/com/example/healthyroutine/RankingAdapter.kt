package com.example.healthyroutine

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RankingAdapter : RecyclerView.Adapter<RankingAdapter.ViewHolder>() {

    private var mRankerList = ArrayList<RankingUserActivity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ranking_recyclerview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 순위를 4부터 시작하도록 설정
        val rank = position + 4
        holder.onBind(mRankerList[position], rank)
    }

    fun setUserList(list: ArrayList<RankingUserActivity>) {
        this.mRankerList = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mRankerList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ranking: TextView = itemView.findViewById(R.id.ranking)
        private val profile: ImageView = itemView.findViewById(R.id.profile)
        private val name: TextView = itemView.findViewById(R.id.name)
        private val points: TextView = itemView.findViewById(R.id.point)

        fun onBind(item: RankingUserActivity, rank: Int) {
            ranking.text = rank.toString() // 4위부터 순위 설정
            profile.setImageResource(item.resourceId)
            name.text = item.name
            points.text = "${item.points}p"
        }
    }
}