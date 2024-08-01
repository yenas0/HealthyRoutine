import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.healthyroutine.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.example.healthyroutine.R

class FindPasswordFragment : Fragment() {
    private lateinit var idEditText: EditText
    private lateinit var findPwBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 프래그먼트 레이아웃 설정
        val view = inflater.inflate(R.layout.fragment_find_password, container, false)

        idEditText = view.findViewById(R.id.etEmailPassword)
        findPwBtn = view.findViewById(R.id.btnFindPassword)

        findPwBtn.setOnClickListener {
            val email = idEditText.text.toString().trim()

            // 이메일이 입력되었는지 확인
            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "이메일을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase에서 비밀번호 재설정 이메일 보내기
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "비밀번호 재설정 이메일을 전송했습니다.", Toast.LENGTH_SHORT).show()
                        // 비밀번호 재설정 이메일을 보냈으므로 메인 액티비티로 이동
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Firebase의 예외 처리
                        try {
                            throw task.exception!!
                        } catch (e: FirebaseAuthInvalidUserException) {
                            Toast.makeText(requireContext(), "존재하지 않는 이메일입니다.", Toast.LENGTH_SHORT).show()
                        } catch (e: FirebaseAuthRecentLoginRequiredException) {
                            Toast.makeText(requireContext(), "최근에 로그인한 기기가 아닙니다.", Toast.LENGTH_SHORT).show()
                        } catch (e: FirebaseAuthUserCollisionException) {
                            Toast.makeText(requireContext(), "사용자 충돌이 발생했습니다.", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "비밀번호 재설정 이메일 전송에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }

        return view
    }
}