package com.estebi.fogo1.ui.updateProfile

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.estebi.fogo1.MainActivity
import com.estebi.fogo1.R
import com.estebi.fogo1.repository.auth.AuthRepository
import com.estebi.fogo1.repository.user.GetUserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.util.*

class UpdateGoogleProfileActivity : AppCompatActivity() {
    private var imageReferece = Firebase.storage.reference
    private var currentFile: Uri? = null
    private var url: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_google_profile)

        supportActionBar?.hide()

        retrieveData()
        selectImageGallery()
        uploadImageToStorageAndUpdateProfile()
    }

    private fun retrieveData() {
        val updateProfileImage = findViewById<ImageView>(R.id.updateProfileImage3)
        val updateProfileName = findViewById<EditText>(R.id.updateMyUserName3)
        GetUserData.getMyUserData().observe(this@UpdateGoogleProfileActivity) { user ->
            user.forEach {
                Picasso.get().load(it.userImg).into(updateProfileImage)
                updateProfileName.setText(it.userName)
            }
        }
    }

    private fun selectImageGallery() {
        val profileImage = findViewById<ImageView>(R.id.updateProfileImage3)
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
                val updateProfileImage = findViewById<ImageView>(R.id.updateProfileImage3)
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
        val updateButton = findViewById<Button>(R.id.updateProfileBtn3)
        val updateProfileName = findViewById<EditText>(R.id.updateMyUserName3)

        updateButton.setOnClickListener {
            if (url == "") {
                val user = FirebaseAuth.getInstance().currentUser?.email.toString()
                AuthRepository.db.collection("Users").document(user).update(
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

                AuthRepository.db.collection("Posts").whereEqualTo("userEmailPosts", user).get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            AuthRepository.db.collection("Posts").document(document.id).update(
                                "userNamePosts", updateProfileName.text.toString()
                            ).addOnSuccessListener {
                                Log.d("AddPostFragment", "Updated")
                            }.addOnFailureListener {
                                Log.d("AddPostFragment", "Failed to update: ${it.message}")
                            }
                        }
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
                            AuthRepository.db.collection("Users").document(user).update(
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

                            AuthRepository.db.collection("Posts").whereEqualTo("userEmailPosts", user).get()
                                .addOnSuccessListener { documents ->
                                    for (document in documents) {
                                        AuthRepository.db.collection("Posts").document(document.id).update(
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

}