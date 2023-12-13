package com.codingstuff.loginandsignup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codingstuff.loginandsignup.databinding.ActivitySignInBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import android.util.Base64

fun encryptAES(plainText: String, key: String): String {
    val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
    val secretKeySpec = SecretKeySpec(key.toByteArray(), "AES")
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
    val encryptedBytes = cipher.doFinal(plainText.toByteArray())
    return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
}

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val mobileno = binding.mobileEt.text.toString()
            val pass = binding.passET.text.toString()

            if (mobileno.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(mobileno, pass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = task.result.user
                            val userId = user?.phoneNumber // Use email as user ID

                            // Trigger login event with user ID
                            val bundle = Bundle()
                            bundle.putString("user_id", userId)
                            firebaseAnalytics.logEvent("login", bundle)

                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (firebaseAuth.currentUser != null) {
            val user = firebaseAuth.currentUser
            val userId = user?.email

            // Trigger login event with user ID
            val bundle = Bundle()
            bundle.putString("user_id", userId)
            firebaseAnalytics.logEvent("login", bundle)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }



}
