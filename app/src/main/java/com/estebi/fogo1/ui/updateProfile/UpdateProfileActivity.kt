package com.estebi.fogo1.ui.updateProfile

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import com.estebi.fogo1.MainActivity
import com.estebi.fogo1.R
import com.estebi.fogo1.repository.auth.AuthRepository.Companion.db
import com.estebi.fogo1.repository.user.GetUserData.Companion.getMyUserData
import com.estebi.fogo1.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.squareup.picasso.Picasso
import java.util.*

class UpdateProfileActivity : AppCompatActivity() {
    private var imageReferece = Firebase.storage.reference
    private var currentFile: Uri? = null
    private var url: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        setContentView(R.layout.activity_update_profile)
        retrieveData()
        selectImageGallery()
        uploadImageToStorageAndUpdateProfile()
        deleteAcconunt()
        changePassword()
    }

    private fun retrieveData() {
        val updateProfileImage = findViewById<ImageView>(R.id.updateProfileImage)
        val updateProfileName = findViewById<EditText>(R.id.updateMyUserName)
        getMyUserData().observe(this@UpdateProfileActivity) { user ->
            user.forEach {
                Picasso.get().load(it.userImg).into(updateProfileImage)
                updateProfileName.setText(it.userName)
            }
        }
    }

    private fun selectImageGallery() {
        val profileImage = findViewById<ImageView>(R.id.updateProfileImage)
        profileImage.setOnClickListener {
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI).also {
                it.type = "image/*"
                imageLauncher.launch(it)
            }
        }
    }

    private val imageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val updateProfileImage = findViewById<ImageView>(R.id.updateProfileImage)
                result?.data?.data?.let {
                    currentFile = it
                    updateProfileImage.setImageURI(it)
                }
            } else {
                Log.d("AddPostFragment", "Image not selected")
            }
        }

    private fun uploadImageToStorageAndUpdateProfile() {
        val filename = UUID.randomUUID().toString()
        val updateButton = findViewById<Button>(R.id.updateProfileBtn)
        val updateProfileName = findViewById<EditText>(R.id.updateMyUserName)

        updateButton.setOnClickListener {
            if (url == "") {
                val user = FirebaseAuth.getInstance().currentUser?.email.toString()
                db.collection("Users").document(user).update(
                    "userName", updateProfileName.text.toString()
                ).addOnSuccessListener {
                    Log.d("AddPostFragment", "Updated")
                    Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show()
                    Intent(this, MainActivity::class.java).also {
                        startActivity(it)
                    }
                    finish()
                }.addOnFailureListener {
                    Log.d("AddPostFragment", "Failed to update: ${it.message}")
                }
            }
            try {
                currentFile?.let {
                    imageReferece.child("images/$filename").putFile(it).addOnSuccessListener {
                        Log.d("AddPostFragment", "File Location: $it")
                        imageReferece.child("images/$filename").downloadUrl.addOnSuccessListener {
                            url = it.toString()
                            Log.d("AddPostFragment", "File Location url: $url")
                            val user = FirebaseAuth.getInstance().currentUser?.email.toString()
                            db.collection("Users").document(user).update(
                                "userImg", url,
                                "userName", updateProfileName.text.toString()
                            ).addOnSuccessListener {
                                Log.d("AddPostFragment", "Updated")
                                Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show()
                                Intent(this, MainActivity::class.java).also {
                                    startActivity(it)
                                }
                                finish()
                            }.addOnFailureListener {
                                Log.d("AddPostFragment", "Failed to update: ${it.message}")
                            }

                            db.collection("Posts").whereEqualTo("userEmailPosts", user).get()
                                .addOnSuccessListener { documents ->
                                    for (document in documents) {
                                        db.collection("Posts").document(document.id).update(
                                            "userImgProfile", url,
                                            "userNamePosts", updateProfileName.text.toString()
                                        ).addOnSuccessListener {
                                            Log.d("AddPostFragment", "Updated")
                                        }.addOnFailureListener {
                                            Log.d(
                                                "AddPostFragment",
                                                "Failed to update: ${it.message}"
                                            )
                                        }
                                    }
                                }.addOnFailureListener {
                                    Log.d("AddPostFragment", "Failed to update: ${it.message}")
                                }
                        }
                    }.addOnFailureListener {
                        Log.d("AddPostFragment", "Failed to upload image to storage: ${it.message}")
                    }
                }
            } catch (e: Exception) {
                Log.d("AddPostFragment", "Error uploading image to storage: ${e.message}")
            }
        }
    }

    private fun deleteAcconunt() {
        val prefs = this.getSharedPreferences(
            getString(R.string.prefs_file),
            Context.MODE_PRIVATE
        ).edit()
        val deleteTextView = findViewById<TextView>(R.id.deleteProfileTextView)
        deleteTextView.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            with(builder)
            {
                setMessage("Want to delete?")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        Intent(this@UpdateProfileActivity, LoginActivity::class.java).also {
                            startActivity(it)
                        }
                        this@UpdateProfileActivity?.finish()

                        db.collection("Users")
                            .document(FirebaseAuth.getInstance().currentUser.toString()).delete()

                        db.collection("Posts")
                            .whereEqualTo(
                                "userEmailPosts",
                                FirebaseAuth.getInstance().currentUser.toString()
                            ).get()
                            .addOnSuccessListener {
                                for (document in it) {
                                    db.collection("Posts").document(document.id).delete()
                                }
                            }.addOnFailureListener { _ ->
                                Log.d("AddPostFragment", "Failed to delete posts")
                            }
                        val user2 = Firebase.auth.currentUser!!
                        Thread.sleep(500)
                        user2.delete()
                        FirebaseAuth.getInstance().signOut()
                        prefs.clear()
                        prefs.apply()
                    }
                    .setNegativeButton(
                        "CANCEL"
                    ) { dialog, _ ->
                        dialog.cancel()
                    }
                show()
            }
        }
    }

    private fun changePassword() {
        val changePasswordTextView = findViewById<EditText>(R.id.textPasswordUpdate2)
        val changePasswordConfirm = findViewById<EditText>(R.id.textPasswordUpdateRepeat)
        val changePasswordButton = findViewById<Button>(R.id.updatePassword)
        val prefs = this@UpdateProfileActivity.getSharedPreferences(
            getString(R.string.prefs_file),
            Context.MODE_PRIVATE
        ).edit()

        if (changePasswordTextView.text.toString() == changePasswordConfirm.text.toString()) {
            changePasswordButton.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                with(builder)
                {
                    setMessage("Want to change password?")
                        .setPositiveButton(
                            "OK"
                        ) { _, _ ->
                            val user = Firebase.auth.currentUser
                            var newPassword = changePasswordTextView.text.toString()
                            user?.updatePassword(newPassword)
                                ?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d("AddPostFragment", "User password updated.")
                                        Toast.makeText(
                                            this@UpdateProfileActivity,
                                            "Password updated!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        FirebaseAuth.getInstance().signOut()
                                        prefs.clear()
                                        prefs.apply()
                                        Intent(
                                            this@UpdateProfileActivity,
                                            LoginActivity::class.java
                                        )
                                            .also {
                                                startActivity(it)
                                            }
                                        finish()
                                    }
                                }
                        }
                        .setNegativeButton(
                            "CANCEL"
                        ) { dialog, _ ->
                            dialog.cancel()
                        }
                    show()
                }
            }

        }
    }
}