package com.estebi.fogo1.ui.userSignUpData

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.estebi.fogo1.MainActivity
import com.estebi.fogo1.R
import com.estebi.fogo1.repository.user.AddImg.Companion.addImgSignUp
import com.google.firebase.auth.FirebaseAuth

class UserDataSignUpActivity : AppCompatActivity() {
    private val url1 =
        "https://firebasestorage.googleapis.com/v0/b/fogo1-3b489.appspot.com/o/profileIcons%2Ficonprofile1.png?alt=media&token=181d4926-8434-445d-adf7-4bef392f287b"
    private val url2 =
        "https://firebasestorage.googleapis.com/v0/b/fogo1-3b489.appspot.com/o/profileIcons%2Ficonprofile2.png?alt=media&token=70de94fe-83f3-4a55-9b87-f55086f80124"
    private val email = FirebaseAuth.getInstance().currentUser?.email.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_data_signup)
        supportActionBar?.hide();
        val gotItBtn = findViewById<Button>(R.id.gotItUserData)
        gotItBtn.setOnClickListener {
            addImgSignUp(email, setImg())
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setImg(): String {
        val switchIcon1 = findViewById<Switch>(R.id.switchIcon1)
        val switchIcon2 = findViewById<Switch>(R.id.switchIcon2)
        var url = ""
        if (switchIcon1.isChecked) {
            url = url1
        } else if (switchIcon2.isChecked) {
            url = url2
        }
        return url
    }
}