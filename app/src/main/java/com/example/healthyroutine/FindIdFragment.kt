import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.example.healthyroutine.R
import android.util.Log

class FindIdFragment : Fragment() {
    private lateinit var emailEditText: EditText
    private lateinit var findIdBtn: Button
    private lateinit var resultTextView: TextView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_find_id, container, false)

        // Initialize views
        emailEditText = view.findViewById(R.id.et_email)
        findIdBtn = view.findViewById(R.id.btn_find_id)
        resultTextView = view.findViewById(R.id.tv_result)

        findIdBtn.setOnClickListener {
            val email = emailEditText.text.toString()

            // Find the ID using the email
            findIdByEmail(email) { username ->
                if (username != null) {
                    resultTextView.text = "Your ID is: $username"
                } else {
                    resultTextView.text = "No account found for this email."
                }
            }
        }

        return view
    }

    private fun findIdByEmail(email: String, callback: (String?) -> Unit) {
        Log.d("FindIdFragment", "Starting Firestore query for email: $email")

        db.collection("users").whereEqualTo("email", email).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val userDocument = documents.documents[0]
                    val username = userDocument.getString("username")
                    Log.d("FindIdFragment", "Username found: $username")
                    callback(username)
                } else {
                    Log.d("FindIdFragment", "No documents found")
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FindIdFragment", "Error getting documents: ", exception)
                callback(null)
            }
    }
}
