package com.example.healthyroutine

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*

class FirestoreHelper {
    private val db = FirebaseFirestore.getInstance()

    // 게시글 추가
    fun addPost(post: Post) {
        val postMap = hashMapOf(
            "title" to post.title,
            "content" to post.content,
            "likes" to post.likes,
            "routine" to post.routine,
            "routineDays" to post.routineDays,
            "userId" to post.userId,
            "createdAt" to post.createdAt
        )

        db.collection("posts").add(postMap)
    }

    // 게시글 업데이트
    fun updatePost(post: Post) {
        val postMap = hashMapOf(
            "title" to post.title,
            "content" to post.content,
            "likes" to post.likes,
            "routine" to post.routine,
            "routineDays" to post.routineDays,
            "userId" to post.userId,
            "createdAt" to post.createdAt
        )

        db.collection("posts").document(post.id).set(postMap)
    }

    // 모든 게시글 가져오기
    fun getAllPosts(callback: (List<Post>) -> Unit) {
        db.collection("posts").orderBy("createdAt", Query.Direction.DESCENDING).get().addOnSuccessListener { result ->
            val posts = result.map { document ->
                document.toObject(Post::class.java).apply { id = document.id }
            }
            callback(posts)
        }
    }

    // 인기 게시글 가져오기
    fun getPopularPosts(callback: (List<Post>) -> Unit) {
        db.collection("posts").whereGreaterThanOrEqualTo("likes", 5).orderBy("likes").get().addOnSuccessListener { result ->
            val posts = result.map { document ->
                document.toObject(Post::class.java).apply { id = document.id }
            }
            callback(posts)
        }
    }

    // 좋아요 추가
    fun addLike(postId: String, userId: String) {
        val likeMap = hashMapOf("userId" to userId)
        db.collection("posts").document(postId).collection("likes").document(userId).set(likeMap)
    }

    // 좋아요 제거
    fun removeLike(postId: String, userId: String) {
        db.collection("posts").document(postId).collection("likes").document(userId).delete()
    }

    // 좋아요 여부 확인
    fun isLiked(postId: String, userId: String, callback: (Boolean) -> Unit) {
        db.collection("posts").document(postId).collection("likes").document(userId).get().addOnSuccessListener { document ->
            callback(document.exists())
        }
    }

    // 내가 쓴 글 가져오기
    fun getMyPosts(userId: String, callback: (List<Post>) -> Unit) {
        db.collection("posts").whereEqualTo("userId", userId).orderBy("createdAt", Query.Direction.DESCENDING).get().addOnSuccessListener { result ->
            val posts = result.map { document ->
                document.toObject(Post::class.java).apply { id = document.id }
            }
            callback(posts)
        }
    }

    // 좋아요 한 글 가져오기
    fun getLikedPosts(userId: String, callback: (List<Post>) -> Unit) {
        db.collection("posts").get().addOnSuccessListener { result ->
            val likedPosts = mutableListOf<Post>()
            val tasks = result.map { document ->
                document.reference.collection("likes").document(userId).get().addOnSuccessListener { likeDoc ->
                    if (likeDoc.exists()) {
                        likedPosts.add(document.toObject(Post::class.java).apply { id = document.id })
                    }
                }
            }

            Tasks.whenAllSuccess<Void>(tasks).addOnSuccessListener {
                callback(likedPosts)
            }
        }
    }
}
