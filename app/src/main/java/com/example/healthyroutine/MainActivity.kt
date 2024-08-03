package com.example.healthyroutine

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize UI components
        val googleLoginImageView = findViewById<ImageView>(R.id.googleLoginImageView)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val signUpTextView = findViewById<TextView>(R.id.signUpTextView)
        val forgotPasswordTextView = findViewById<TextView>(R.id.forgotPasswordTextView)

        // Set up the login button click listener
        loginButton.setOnClickListener {
            val input = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (input.isNotEmpty() && password.isNotEmpty()) {
                // Check if the input is an email or username
                if (Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                    // Input is an email, proceed with login
                    loginWithEmail(input, password)
                } else {
                    // Input is a username, retrieve the associated email from Firestore
                    val db = FirebaseFirestore.getInstance()
                    db.collection("users").whereEqualTo("username", input)
                        .get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val documents = task.result?.documents
                                if (documents != null && documents.isNotEmpty()) {
                                    val document = documents.first()
                                    val email = document.getString("email")
                                    if (email != null) {
                                        loginWithEmail(email, password)
                                    } else {
                                        Toast.makeText(this, "아이디에 해당하는 이메일을 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(this, "아이디가 잘못되었습니다", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this, "쿼리가 실패하였습니다: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            } else {
                Toast.makeText(this, "아이디 또는 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up the Google login button click listener
        googleLoginImageView.setOnClickListener {
            signInWithGoogle()
        }

        // Set up the sign-up text view click listener
        signUpTextView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Set up the forgot password text view click listener
        forgotPasswordTextView.setOnClickListener {
            val intent = Intent(this, FindAccountActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.result
                if (account != null) {
                    handleSignInResult(account)
                } else {
                    Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleSignInResult(account: GoogleSignInAccount) {
        try {
            if (account != null) {
                firebaseAuthWithGoogle(account)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Google sign-in successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Firebase authentication failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loginWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "비밀번호가 잘못되었습니다", Toast.LENGTH_SHORT).show()
                }
            }
    }
}