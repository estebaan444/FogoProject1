package com.estebi.fogo1.ui.user

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.estebi.fogo1.LoginActivity
import com.estebi.fogo1.R
import com.estebi.fogo1.SignInActivity
import com.estebi.fogo1.databinding.FragmentUserBinding
import com.estebi.fogo1.repository.auth.AuthRepository.Companion.db
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class UserFragment : Fragment() {

    private var _binding: FragmentUserBinding? = null

    private val binding get() = _binding!!

    private lateinit var user: FirebaseUser


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val root: View = binding.root
        (activity as AppCompatActivity).supportActionBar?.hide()

        user = FirebaseAuth.getInstance().currentUser!!;
        binding.textUser.text = user.email
        val docRef = db.collection("Users").document(FirebaseAuth.getInstance().currentUser?.email.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    //Los datos los cojemos desde aqui:
                    Log.d(TAG, document.data?.get("userName").toString())
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        binding.logout.setOnClickListener {
            val prefs = requireContext().getSharedPreferences(
                getString(R.string.prefs_file),
                Context.MODE_PRIVATE
            ).edit()
            prefs.clear()
            prefs.apply()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            activity?.finish()
            activity?.startActivity(intent)
        }

        binding.deleteAccount.setOnClickListener {
            db.collection("Users")
                .document(FirebaseAuth.getInstance().currentUser?.email.toString())
                .delete()
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
            FirebaseAuth.getInstance().currentUser?.delete()
            Toast.makeText(
                context, "The account has been deleted",
                Toast.LENGTH_SHORT
            ).show()
            val prefs = requireContext().getSharedPreferences(
                getString(R.string.prefs_file),
                Context.MODE_PRIVATE
            ).edit()
            prefs.clear()
            prefs.apply()

            val intent = Intent(activity, SignInActivity::class.java)
            activity?.finish()
            activity?.startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}