package com.example.healthyroutine

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthyroutine.Post

class PostsAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.post_title)
        val contentTextView: TextView = itemView.findViewById(R.id.post_content)
        val likesTextView: TextView = itemView.findViewById(R.id.post_likes)
        val likeIcon: ImageView = itemView.findViewById(R.id.post_like_icon)

        fun bind(post: Post) {
            titleTextView.text = post.title
            contentTextView.text = post.content
            likesTextView.text = post.likes.toString()
            likeIcon.setImageResource(if (post.likes > 0) R.drawable.ic_like_filled else R.drawable.ic_like_empty)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size
}
