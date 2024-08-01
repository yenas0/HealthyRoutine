package com.example.healthyroutine

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Post(val id: Int, var title: String, var content: String, var likes: Int, val routine: String? = null, val routineDays: String? = null)

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createPostTable = ("CREATE TABLE " + TABLE_POSTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_CONTENT + " TEXT,"
                + COLUMN_LIKES + " INTEGER,"
                + COLUMN_ROUTINE + " TEXT,"
                + COLUMN_ROUTINE_DAYS + " TEXT,"
                + "$COLUMN_USER_ID INTEGER)")  // 사용자 ID 컬럼 추가

        val createLikesTable = ("CREATE TABLE " + TABLE_LIKES + "("
                + COLUMN_LIKE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_POST_ID + " INTEGER,"
                + "$COLUMN_USER_ID INTEGER)")  // 사용자 ID 컬럼 추가

        db.execSQL(createPostTable)
        db.execSQL(createLikesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            db.execSQL("ALTER TABLE $TABLE_POSTS ADD COLUMN $COLUMN_USER_ID INTEGER DEFAULT 1")
            db.execSQL("ALTER TABLE $TABLE_LIKES ADD COLUMN $COLUMN_USER_ID INTEGER DEFAULT 1")
        }
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    // 현재 로그인한 사용자 ID를 관리할 변수
    private var currentUserId: Int = 1  // 예시로 1로 설정, 실제로는 로그인된 사용자 ID를 설정

    fun addPost(title: String, content: String, routine: String?, routineDays: String?) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_CONTENT, content)
            put(COLUMN_LIKES, 0)
            put(COLUMN_ROUTINE, routine)
            put(COLUMN_ROUTINE_DAYS, routineDays)
            put(COLUMN_USER_ID, currentUserId)  // 사용자 ID 추가
        }
        db.insert(TABLE_POSTS, null, values)
        db.close()
    }

    fun updatePost(post: Post) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, post.title)
            put(COLUMN_CONTENT, post.content)
            put(COLUMN_LIKES, post.likes)
            put(COLUMN_ROUTINE, post.routine)
            put(COLUMN_ROUTINE_DAYS, post.routineDays)
        }
        db.update(TABLE_POSTS, values, "$COLUMN_ID = ?", arrayOf(post.id.toString()))
        db.close()
    }

    fun getAllPosts(): List<Post> {
        val posts = mutableListOf<Post>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_POSTS ORDER BY $COLUMN_ID DESC", null)
        if (cursor.moveToFirst()) {
            do {
                val post = Post(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                    content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)),
                    likes = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LIKES)),
                    routine = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROUTINE)),
                    routineDays = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROUTINE_DAYS))
                )
                posts.add(post)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return posts
    }

    fun getPopularPosts(): List<Post> {
        val popularPosts = mutableListOf<Post>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_POSTS WHERE $COLUMN_LIKES >= 5 ORDER BY $COLUMN_LIKES DESC", null)
        if (cursor.moveToFirst()) {
            do {
                val post = Post(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                    content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)),
                    likes = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LIKES)),
                    routine = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROUTINE)),
                    routineDays = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROUTINE_DAYS))
                )
                popularPosts.add(post)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return popularPosts
    }

    fun addLike(postId: Int) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_POST_ID, postId)
            put(COLUMN_USER_ID, currentUserId)  // 사용자 ID 추가
        }
        db.insert(TABLE_LIKES, null, values)
        db.close()
    }

    fun removeLike(postId: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_LIKES, "$COLUMN_POST_ID = ? AND $COLUMN_USER_ID = ?", arrayOf(postId.toString(), currentUserId.toString()))
        db.close()
    }

    fun isLiked(postId: Int): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_LIKES WHERE $COLUMN_POST_ID = ? AND $COLUMN_USER_ID = ?", arrayOf(postId.toString(), currentUserId.toString()))
        val isLiked = cursor.count > 0
        cursor.close()
        db.close()
        return isLiked
    }

    fun getMyPosts(): List<Post> {
        val posts = mutableListOf<Post>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_POSTS WHERE $COLUMN_USER_ID = ? ORDER BY $COLUMN_ID DESC", arrayOf(currentUserId.toString()))
        if (cursor.moveToFirst()) {
            do {
                val post = Post(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                    content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)),
                    likes = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LIKES)),
                    routine = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROUTINE)),
                    routineDays = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROUTINE_DAYS))
                )
                posts.add(post)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return posts
    }

    fun getLikedPosts(): List<Post> {
        val posts = mutableListOf<Post>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_POSTS WHERE $COLUMN_ID IN (SELECT $COLUMN_POST_ID FROM $TABLE_LIKES WHERE $COLUMN_USER_ID = ?)", arrayOf(currentUserId.toString()))
        if (cursor.moveToFirst()) {
            do {
                val post = Post(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                    content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)),
                    likes = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LIKES)),
                    routine = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROUTINE)),
                    routineDays = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROUTINE_DAYS))
                )
                posts.add(post)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return posts
    }

    companion object {
        private const val DATABASE_VERSION = 5
        private const val DATABASE_NAME = "healthyroutine.db"
        const val TABLE_POSTS = "posts"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_LIKES = "likes"
        const val COLUMN_ROUTINE = "routine"
        const val COLUMN_ROUTINE_DAYS = "routine_days"

        const val TABLE_LIKES = "likes"
        const val COLUMN_LIKE_ID = "like_id"
        const val COLUMN_POST_ID = "post_id"
        const val COLUMN_USER_ID = "user_id"
    }
}
