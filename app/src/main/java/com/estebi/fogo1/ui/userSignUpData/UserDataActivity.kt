package com.estebi.fogo1.ui.userSignUpData

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.estebi.fogo1.MainActivity
import com.estebi.fogo1.R
import com.estebi.fogo1.models.User
import com.estebi.fogo1.repository.auth.AuthRepository.Companion.addUserCollection
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


class UserDataActivity : AppCompatActivity() {
    val url1 =
        "https://firebasestorage.googleapis.com/v0/b/fogo1-3b489.appspot.com/o/profileIcons%2Ficonprofile1.png?alt=media&token=181d4926-8434-445d-adf7-4bef392f287b"
    val url2 =
        "https://firebasestorage.googleapis.com/v0/b/fogo1-3b489.appspot.com/o/profileIcons%2Ficonprofile2.png?alt=media&token=70de94fe-83f3-4a55-9b87-f55086f80124"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_data)
        supportActionBar?.hide();
        val gotItBtn = findViewById<Button>(R.id.gotItUserData)

        gotItBtn.setOnClickListener {
            addUserCollection2()
        }
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private fun addUserCollection2() {
        val userEmail = FirebaseAuth.getInstance().currentUser?.email.toString()
        val userName = findViewById<EditText>(R.id.addUserNameData);
        val switchIcon1 = findViewById<Switch>(R.id.switchIcon1)
        val switchIcon2 = findViewById<Switch>(R.id.switchIcon2)

        var url = ""
        if (switchIcon1.isChecked) {
            url = url1
        } else if (switchIcon2.isChecked) {
            url = url2
        }

        lifecycleScope.launch {
            val user = User(userEmail, userName.text.toString(), url)
            addUserCollection(user)
        }
        startActivity(Intent(this, MainActivity::class.java))
        finish()

    }
}