package com.estebi.fogo1

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.estebi.fogo1.databinding.ActivityMain2Binding
import com.estebi.fogo1.repository.auth.AuthRepository.Companion.db
import com.estebi.fogo1.repository.user.CheckUserData.Companion.checkUserDataGoogle
import com.estebi.fogo1.ui.auth.LoginActivity
import com.estebi.fogo1.ui.userSignUpData.UserDataActivity
import com.estebi.fogo1.ui.userSignUpData.UserDataSignUpActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


enum class ProviderType {
    BASIC,
    GOOGLE
}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayShowHomeEnabled(true);



        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        //Setup
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")

        Log.d("TAG", "onCreate: " + checkUserDataGoogle())

        //Guardado de datos
        val prefs = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

        if (FirebaseAuth.getInstance().currentUser?.email == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            prefs.clear()
            prefs.apply()
            finish()
        }

        checkUserData()

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main2)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_search,
                R.id.navigation_addPost,
                R.id.navigation_user
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun checkUserData() {
        val currentUse = FirebaseAuth.getInstance().currentUser?.email

        val mFireStore = FirebaseFirestore.getInstance()
        val docRef = mFireStore.collection("Users").document("$currentUse")

        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null) {
                    if (document.exists()) {
                        Log.d("TAG", "Document already exists.")
                    } else {
                        Log.d("TAG", "Document doesn't exist.")
                        Intent(this, UserDataActivity::class.java).apply {
                            startActivity(this)
                        }
                        finish()
                    }
                }
            } else {
                Log.d("TAG", "Error: ", task.exception)
            }
        }

        db.collection("Users")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                val checkStringImg =""
                if (snapshot != null) {
                    for (document in snapshot) {
                        if (document["userImg"].toString() == checkStringImg) {
                            Intent(this, UserDataSignUpActivity::class.java).apply {
                                startActivity(this)
                            }
                            finish()
                        }
                    }
                }
            }
    }
}