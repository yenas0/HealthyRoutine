package com.example.healthyroutine

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.LocalDate

data class Routine(
    val id: Int = 0,
    val name: String,
    val notificationEnabled: Boolean
)

data class RoutineCheck(
    val routineId: Int,
    val date: String,
    var isChecked: Boolean
)

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createPostTable = ("CREATE TABLE " + TABLE_POSTS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_CONTENT + " TEXT,"
                + COLUMN_LIKES + " INTEGER,"
                + COLUMN_ROUTINE + " TEXT,"
                + COLUMN_ROUTINE_DAYS + " TEXT,"
                + COLUMN_USER_ID + " INTEGER)")

        val createLikesTable = ("CREATE TABLE " + TABLE_LIKES + "("
                + COLUMN_LIKE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_POST_ID + " INTEGER,"
                + COLUMN_USER_ID + " INTEGER)")

        val createRoutinesTable = ("CREATE TABLE " + TABLE_ROUTINES + "("
                + COLUMN_ROUTINE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ROUTINE_NAME + " TEXT,"
                + COLUMN_NOTIFICATION_ENABLED + " INTEGER)")

        val createRoutineChecksTable = ("CREATE TABLE " + TABLE_ROUTINE_CHECKS + "("
                + COLUMN_ROUTINE_ID + " INTEGER,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_IS_CHECKED + " INTEGER,"
                + "PRIMARY KEY($COLUMN_ROUTINE_ID, $COLUMN_DATE))")

        val createPointsTable = ("CREATE TABLE " + TABLE_POINTS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_POINTS + " INTEGER)")

        db.execSQL(createPostTable)
        db.execSQL(createLikesTable)
        db.execSQL(createRoutinesTable)
        db.execSQL(createRoutineChecksTable)
        db.execSQL(createPointsTable)

        // 포인트 초기화 예시 (userId 1번 사용자에 대해)
        val initialPoints = ContentValues().apply {
            put(COLUMN_USER_ID, 1)
            put(COLUMN_POINTS, 0)
        }
        db.insert(TABLE_POINTS, null, initialPoints)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_POSTS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_LIKES")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_ROUTINES")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_ROUTINE_CHECKS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_POINTS")
            onCreate(db)
        }
    }

    fun addRoutine(routine: Routine) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ROUTINE_NAME, routine.name)
            put(COLUMN_NOTIFICATION_ENABLED, if (routine.notificationEnabled) 1 else 0)
        }
        db.insert(TABLE_ROUTINES, null, values)
        db.close()
    }

    fun updateRoutine(routine: Routine) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ROUTINE_NAME, routine.name)
            put(COLUMN_NOTIFICATION_ENABLED, if (routine.notificationEnabled) 1 else 0)
        }
        db.update(TABLE_ROUTINES, values, "$COLUMN_ROUTINE_ID = ?", arrayOf(routine.id.toString()))
        db.close()
    }

    // 루틴 삭제
    fun deleteRoutine(routineId: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_ROUTINES, "$COLUMN_ROUTINE_ID = ?", arrayOf(routineId.toString()))
        db.close()
    }


    fun getAllRoutines(): List<Routine> {
        val routines = mutableListOf<Routine>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ROUTINES", null)
        if (cursor.moveToFirst()) {
            do {
                val routine = Routine(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ROUTINE_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROUTINE_NAME)),
                    notificationEnabled = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTIFICATION_ENABLED)) == 1
                )
                routines.add(routine)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return routines
    }

    fun addPost(post: Post) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, post.title)
            put(COLUMN_CONTENT, post.content)
            put(COLUMN_LIKES, post.likes)
            put(COLUMN_ROUTINE, post.routine)
            put(COLUMN_ROUTINE_DAYS, post.routineDays)
            put(COLUMN_USER_ID, post.userId)
        }
        db.insert(TABLE_POSTS, null, values)
        db.close()
    }

    fun addRoutineCheck(routineCheck: RoutineCheck) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ROUTINE_ID, routineCheck.routineId)
            put(COLUMN_DATE, routineCheck.date)
            put(COLUMN_IS_CHECKED, if (routineCheck.isChecked) 1 else 0)
        }
        db.insert(TABLE_ROUTINE_CHECKS, null, values)
        db.close()
    }

    fun updateRoutineCheck(routineCheck: RoutineCheck) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IS_CHECKED, if (routineCheck.isChecked) 1 else 0)
        }
        db.update(TABLE_ROUTINE_CHECKS, values, "$COLUMN_ROUTINE_ID = ? AND $COLUMN_DATE = ?", arrayOf(routineCheck.routineId.toString(), routineCheck.date))
        db.close()
    }

    fun getRoutineCheck(routineId: Int, date: String): RoutineCheck? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ROUTINE_CHECKS WHERE $COLUMN_ROUTINE_ID = ? AND $COLUMN_DATE = ?", arrayOf(routineId.toString(), date))
        var routineCheck: RoutineCheck? = null
        if (cursor.moveToFirst()) {
            routineCheck = RoutineCheck(
                routineId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ROUTINE_ID)),
                date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                isChecked = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_CHECKED)) == 1
            )
        }
        cursor.close()
        db.close()
        return routineCheck
    }

    fun getCheckCountForDate(date: LocalDate): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_ROUTINE_CHECKS WHERE $COLUMN_IS_CHECKED = 1 AND $COLUMN_DATE = ?",
            arrayOf(date.toString())
        )
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }

    fun updatePoints(userId: Int, pointsChange: Int) {
        val db = this.writableDatabase
        db.execSQL("UPDATE $TABLE_POINTS SET $COLUMN_POINTS = $COLUMN_POINTS + $pointsChange WHERE $COLUMN_USER_ID = $userId")
    }

    fun getPoints(userId: Int): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT $COLUMN_POINTS FROM $TABLE_POINTS WHERE $COLUMN_USER_ID = ?", arrayOf(userId.toString()))
        if (cursor.moveToFirst()) {
            val points = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_POINTS))
            cursor.close()
            return points
        }
        cursor.close()
        return 0
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
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)).toString(),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                    content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)),
                    likes = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LIKES)),
                    routine = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROUTINE)),
                    routineDays = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROUTINE_DAYS)),
                    userId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
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
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)).toString(),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                    content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)),
                    likes = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LIKES)),
                    routine = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROUTINE)),
                    routineDays = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROUTINE_DAYS)),
                    userId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
                )
                popularPosts.add(post)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return popularPosts
    }

    fun addLike(postId: String) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_POST_ID, postId.toInt())
            put(COLUMN_USER_ID, 1)
        }
        db.insert(TABLE_LIKES, null, values)
        db.close()
    }

    fun removeLike(postId: String) {
        val db = this.writableDatabase
        db.delete(TABLE_LIKES, "$COLUMN_POST_ID = ? AND $COLUMN_USER_ID = ?", arrayOf(postId, "1"))
        db.close()
    }

    fun isLiked(postId: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_LIKES WHERE $COLUMN_POST_ID = ? AND $COLUMN_USER_ID = ?", arrayOf(postId, "1"))
        val isLiked = cursor.count > 0
        cursor.close()
        db.close()
        return isLiked
    }

    fun getMyPosts(): List<Post> {
        val posts = mutableListOf<Post>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_POSTS WHERE $COLUMN_USER_ID = ? ORDER BY $COLUMN_ID DESC", arrayOf("1"))
        if (cursor.moveToFirst()) {
            do {
                val post = Post(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)).toString(),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                    content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)),
                    likes = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LIKES)),
                    routine = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROUTINE)),
                    routineDays = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROUTINE_DAYS)),
                    userId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
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
        val cursor = db.rawQuery("SELECT * FROM $TABLE_POSTS WHERE $COLUMN_ID IN (SELECT $COLUMN_POST_ID FROM $TABLE_LIKES WHERE $COLUMN_USER_ID = ?)", arrayOf("1"))
        if (cursor.moveToFirst()) {
            do {
                val post = Post(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)).toString(),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                    content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)),
                    likes = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LIKES)),
                    routine = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROUTINE)),
                    routineDays = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROUTINE_DAYS)),
                    userId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
                )
                posts.add(post)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return posts
    }

    companion object {
        private const val DATABASE_VERSION = 9 // 버전 증가
        private const val DATABASE_NAME = "healthyroutine.db"

        const val TABLE_POSTS = "posts"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_LIKES = "likes"
        const val COLUMN_ROUTINE = "routine"
        const val COLUMN_ROUTINE_DAYS = "routine_days"
        const val COLUMN_USER_ID = "user_id" // 여기를 추가

        const val TABLE_LIKES = "likes"
        const val COLUMN_LIKE_ID = "like_id"
        const val COLUMN_POST_ID = "post_id"

        const val TABLE_ROUTINES = "routines"
        const val COLUMN_ROUTINE_ID = "routine_id"
        const val COLUMN_ROUTINE_NAME = "name"
        const val COLUMN_NOTIFICATION_ENABLED = "notification_enabled"

        const val TABLE_ROUTINE_CHECKS = "routine_checks"
        const val COLUMN_DATE = "date"
        const val COLUMN_IS_CHECKED = "is_checked"

        const val TABLE_POINTS = "points"
        const val COLUMN_POINTS = "points"
    }
}
