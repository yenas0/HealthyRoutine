package com.example.healthyroutine

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class EditProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var currentUser: FirebaseUser

    private lateinit var profileImageView: ImageView
    private lateinit var uploadPhotoButton: Button
    private lateinit var emailEditText: EditText
    private lateinit var nicknameEditText: EditText
    private lateinit var updateProfileButton: Button

    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        currentUser = auth.currentUser!!

        val backButton: ImageView = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        profileImageView = findViewById(R.id.profileImageView)
        uploadPhotoButton = findViewById(R.id.uploadPhotoButton)
        emailEditText = findViewById(R.id.emailEditText)
        nicknameEditText = findViewById(R.id.nicknameEditText)
        updateProfileButton = findViewById(R.id.updateProfileButton)

        // 프로필 이미지 둥글게 만들기
        Glide.with(this)
            .load(R.drawable.ic_profile)
            .apply(RequestOptions.circleCropTransform())
            .into(profileImageView)

        uploadPhotoButton.setOnClickListener {
            openGallery()
        }

        // 사용자 정보 설정
        emailEditText.setText(currentUser.email)
        loadUserProfile(nicknameEditText)

        updateProfileButton.setOnClickListener {
            Log.d("EditProfileActivity", "Update profile button clicked")
            updateProfile(nicknameEditText)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedImageUri: Uri? = data.data
            try {
                val bitmap: Bitmap = android.provider.MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                // 프로필 이미지 뷰 업데이트
                Glide.with(this)
                    .load(bitmap)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImageView)

                // 프로필 이미지 업로드
                uploadProfileImage(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadProfileImage(bitmap: Bitmap) {
        val storageRef = storage.reference.child("profileImages/${currentUser.uid}.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = storageRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                saveProfileImageUri(uri.toString())
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "프로필 이미지 URL 가져오기 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("EditProfileActivity", "Error getting download URL", exception)
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "프로필 이미지 업로드에 실패했습니다: ${exception.message}", Toast.LENGTH_SHORT).show()
            Log.e("EditProfileActivity", "Error uploading image", exception)
        }
    }

    private fun saveProfileImageUri(uri: String) {
        val userRef = firestore.collection("users").document(currentUser.uid)
        userRef.set(mapOf("profileImageUrl" to uri), SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "프로필 이미지가 업데이트되었습니다.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "프로필 이미지 URL 저장 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("EditProfileActivity", "Error updating document", e)
            }
    }

    private fun loadUserProfile(nicknameEditText: EditText) {
        firestore.collection("users").document(currentUser.uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val nickname = document.getString("nickname")
                    val profileImageUrl = document.getString("profileImageUrl")
                    nicknameEditText.setText(nickname)
                    profileImageUrl?.let {
                        Glide.with(this)
                            .load(it)
                            .apply(RequestOptions.circleCropTransform())
                            .into(profileImageView)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("EditProfileActivity", "Error getting documents: ", exception)
            }
    }

    private fun updateProfile(nicknameEditText: EditText) {
        val nickname = nicknameEditText.text.toString()

        if (nickname.isEmpty()) {
            Toast.makeText(this, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("EditProfileActivity", "Updating nickname to: $nickname")
        updateNickname(nickname)
    }

    private fun updateNickname(nickname: String) {
        val userRef = firestore.collection("users").document(currentUser.uid)
        userRef.set(mapOf("nickname" to nickname), SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "회원 정보가 수정되었습니다.", Toast.LENGTH_SHORT).show()
                val resultIntent = Intent()
                resultIntent.putExtra("nickname", nickname)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "회원 정보 수정에 실패했습니다.", Toast.LENGTH_SHORT).show()
                Log.w("EditProfileActivity", "Error updating document", e)
            }
    }
}
