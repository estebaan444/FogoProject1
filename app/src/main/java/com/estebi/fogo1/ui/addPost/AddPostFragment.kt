package com.estebi.fogo1.ui.addPost

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.estebi.fogo1.R
import com.estebi.fogo1.databinding.FragmentAddPostBinding
import com.estebi.fogo1.models.Posts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import java.io.ByteArrayOutputStream
import java.util.*


class AddPostFragment : Fragment() {

    private var _binding: FragmentAddPostBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var imageReferece = Firebase.storage.reference
    private var currentFile: Uri? = null
    private var url: String = ""
    private val db = Firebase.firestore
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email.toString()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPostBinding.inflate(inflater, container, false)
        val root: View = binding.root
        (activity as AppCompatActivity).supportActionBar?.hide()

        binding.openGalleryBtn.setOnClickListener {
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI).also {
                it.type = "image/*"
                imageLauncher.launch(it)
            }
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED
        )
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                1800
            )

        binding.openCameraBtn.setOnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
                imageLauncherCamera.launch(it)
            }
        }

        binding.uploadImageBtn.setOnClickListener {
            val filename = UUID.randomUUID().toString()
            uploadImageToStorage(filename)
        }
        return root
    }

    private val imageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result?.data?.data?.let {
                    val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, it)
                    val image = InputImage.fromBitmap(bitmap, 0)
                    val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
                    labeler.process(image).addOnSuccessListener { labels ->
                        val list = mutableListOf<String>()
                        for (label in labels) {
                            val text = label.text
                            list.add(text)
                            Log.i("Is food?", "Label: $text")
                        }
                        if (list.contains("Food")) {
                            currentFile = it
                            binding.imageaddpost.setImageURI(it)
                        }else{
                            Toast.makeText(requireContext(), "Please select a food image", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            } else {
                Log.d("AddPostFragment", "Image not selected")
            }
        }
    private val imageLauncherCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)

                val bitmap = result?.data?.extras?.get("data");
                bitmap as Bitmap
                //binding.imageaddpost.setImageBitmap(bitmap as Bitmap)
                val bytes = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                val path = MediaStore.Images.Media.insertImage(
                    requireContext().contentResolver,
                    bitmap,
                    "Title",
                    null
                )
                currentFile = path?.let { Uri.parse(it) }

                val image = InputImage.fromBitmap(bitmap, 0)
                val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
                labeler.process(image).addOnSuccessListener { labels ->
                    val list = mutableListOf<String>()
                    for (label in labels) {
                        val text = label.text
                        list.add(text)
                        Log.i("AddPostFragmentlabeler", "Label: $text")
                    }
                    if(list.contains("Food")) {
                        currentFile = path?.let { Uri.parse(it) }
                        binding.imageaddpost.setImageURI(path?.let { Uri.parse(it) })
                    }else{
                        Toast.makeText(requireContext(), "Please take a food image", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Log.d("AddPostFragment", "Image not selected")
            }
        }

    private fun uploadImageToStorage(filename: String) {
        try {
            currentFile?.let {
                imageReferece.child("images/$filename").putFile(it).addOnSuccessListener {
                    Log.d("AddPostFragment", "File Location: $it")
                    imageReferece.child("images/$filename").downloadUrl.addOnSuccessListener {
                        url = it.toString()
                        Log.d("AddPostFragment", "File Location url: $url")

                        db.collection("Users")
                            .whereEqualTo(
                                "userEmail",
                                FirebaseAuth.getInstance().currentUser?.email.toString()
                            )
                            .get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {
                                    val userNamePosts = document.data["userName"].toString()
                                    val userImgProfile = document.data["userImg"].toString()
                                    getUserNameAndUpload(userNamePosts, userImgProfile)
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                            }
                        Toast.makeText(context, "Post added", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Log.d("AddPostFragment", "Failed to upload image to storage: ${it.message}")
                }
            }
        } catch (e: Exception) {
            Log.d("AddPostFragment", "Error uploading image to storage: ${e.message}")
        }
    }

    private fun getUserNameAndUpload(userNamePosts: String, userImgProfile: String) {
        val userEmailPosts = userEmail
        val userImgPosts = url
        val postDesc = binding.addPostDesc.text.toString()
        val titlePost = binding.addPostTitle.text.toString()
        val postId = UUID.randomUUID().toString()
        val post = Posts(
            postId,
            userEmailPosts,
            userNamePosts,
            userImgProfile,
            userImgPosts,
            postDesc,
            titlePost
        )
        db.collection("Posts").document(postId)
            .set(post)
            .addOnSuccessListener {
                Log.d(
                    ContentValues.TAG,
                    "DocumentSnapshot successfully written!"
                )
                findNavController().navigate(R.id.navigation_home)
            }
            .addOnFailureListener { e ->
                Log.w(
                    ContentValues.TAG,
                    "Error writing document",
                    e
                )
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}