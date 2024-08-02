package com.example.healthyroutine

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class PostWriteActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var routineSwitch: Switch
    private lateinit var routineContainer: LinearLayout
    private lateinit var routineNameEditText: EditText
    private lateinit var dayButtons: List<TextView>
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_write)

        titleEditText = findViewById(R.id.title_edit_text)
        contentEditText = findViewById(R.id.content_edit_text)
        routineSwitch = findViewById(R.id.routine_switch)
        routineContainer = findViewById(R.id.routine_container)
        routineNameEditText = findViewById(R.id.routine_name_edit_text)
        dayButtons = listOf(
            findViewById(R.id.day_mon),
            findViewById(R.id.day_tue),
            findViewById(R.id.day_wed),
            findViewById(R.id.day_thu),
            findViewById(R.id.day_fri),
            findViewById(R.id.day_sat),
            findViewById(R.id.day_sun)
        )

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser

        routineSwitch.setOnCheckedChangeListener { _, isChecked ->
            routineContainer.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        dayButtons.forEach { dayButton ->
            dayButton.setOnClickListener {
                toggleDayButton(dayButton)
            }
        }

        val submitButton: Button = findViewById(R.id.btn_submit_post)
        submitButton.setOnClickListener {
            savePost()
        }

        val backButton: ImageView = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun savePost() {
        val title = titleEditText.text.toString().trim()
        val content = contentEditText.text.toString().trim()

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "제목과 내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (title.length > 15) {
            Toast.makeText(this, "제목은 15글자 이내로 작성해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        if (content.length > 50) {
            Toast.makeText(this, "내용은 50글자 이내로 작성해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val routineName = if (routineSwitch.isChecked) routineNameEditText.text.toString().trim() else null
        val routineDays = if (routineSwitch.isChecked) getSelectedDays() else null

        // 현재 사용자 UID를 가져와서 사용
        val userId = currentUser?.uid ?: ""

        if (userId.isEmpty()) {
            Toast.makeText(this, "사용자 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val post = Post(
            title = title,
            content = content,
            routine = routineName,
            routineDays = routineDays,
            userId = userId
        )

        val firestoreHelper = FirestoreHelper()
        firestoreHelper.addPost(post)

        Toast.makeText(this, "게시글이 작성되었습니다.", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun getSelectedDays(): String {
        return dayButtons.filter { it.tag == "active" }
            .joinToString(",") { it.text.toString() }
    }

    private fun toggleDayButton(button: TextView) {
        val isActive = button.tag == "active"
        if (isActive) {
            button.background = resources.getDrawable(R.drawable.circle_background_inactive, null)
            button.tag = "inactive"
        } else {
            button.background = resources.getDrawable(R.drawable.circle_background_active, null)
            button.tag = "active"
        }
    }
}
