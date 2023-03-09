package com.estebi.fogo1.ui.user

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.estebi.fogo1.LoginActivity
import com.estebi.fogo1.R
import com.estebi.fogo1.databinding.FragmentUserBinding
import com.estebi.fogo1.models.Posts
import com.estebi.fogo1.repository.posts.PostsRepository
import com.estebi.fogo1.ui.user.adapter.MyPostsAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null

    private val binding get() = _binding!!

    private lateinit var user: FirebaseUser

    private val db = Firebase.firestore

    private val myPostsAdapter = MyPostsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val root: View = binding.root
        (activity as AppCompatActivity).supportActionBar?.hide()

        user = Firebase.auth.currentUser!!;

        db.collection("Users").document(FirebaseAuth.getInstance().currentUser?.email.toString())
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    binding.userNameProfile.text = document.data?.get("userName").toString()
                    val userImg = document.data?.get("userImg").toString()
                    Picasso.get().load(userImg).into(binding.profileImage)
                    binding.textUser.text = user.email
                }
            }
            .addOnFailureListener {
                binding.userNameProfile.text = ""
                binding.textUser.text = ""
                binding.profileImage.setImageResource(R.drawable.account)
            }

        binding.configButton.setOnClickListener {
            bottomSheetDialog()
        }
        val myPostsRv = binding.myPostsRV
        myPostsRv.layoutManager = GridLayoutManager(requireContext(), 2)
        myPostsRv.setHasFixedSize(true)
        myPostsRv.adapter = myPostsAdapter
        myPostsAdapter.setItemListener(object : MyPostsAdapter.onItemClickListener {
            override fun onItemClick(posts: Posts) {
                val userEmailKey = posts.userEmailPosts
                //val userName = user.userName
                Toast.makeText(requireContext(), userEmailKey, Toast.LENGTH_SHORT).show()
            }
        })
        observeHomePosts()

        return root
    }

    private fun observeHomePosts() {
        PostsRepository.getMyPostsList().observe(requireActivity()) { postsList ->
            myPostsAdapter.setListData(postsList)
            myPostsAdapter.notifyDataSetChanged()
        }
    }

    private fun bottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_layout)
        val logoutLayout = bottomSheetDialog.findViewById<LinearLayout>(R.id.layoutLogout)
        val deleteLayout = bottomSheetDialog.findViewById<LinearLayout>(R.id.layoutDelete)
        val prefs = requireContext().getSharedPreferences(
            getString(R.string.prefs_file),
            Context.MODE_PRIVATE
        ).edit()
        logoutLayout?.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            bottomSheetDialog.dismiss()
            Intent(requireContext(), LoginActivity::class.java).also {
                startActivity(it)
            }
            prefs.clear()
            prefs.apply()
            activity?.finish()

        }
        deleteLayout?.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            with(builder)
            {
                setMessage("Want to delete?")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        Intent(requireContext(), LoginActivity::class.java).also {
                            startActivity(it)
                        }
                        activity?.finish()

                        db.collection("Users").document(user.email.toString()).delete()

                        db.collection("Posts")
                            .whereEqualTo("userEmailPosts", user.email.toString()).get()
                            .addOnSuccessListener {
                                for (document in it) {
                                    db.collection("Posts").document(document.id).delete()
                                }
                            }.addOnFailureListener { exception ->
                                binding.userNameProfile.text = "Error al cargar"
                                binding.textUser.text = "Error al cargar"
                                binding.profileImage.setImageResource(R.drawable.account)
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

            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}