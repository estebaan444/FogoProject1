package com.estebi.fogo1.ui.user

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.estebi.fogo1.LoginActivity
import com.estebi.fogo1.SignInActivity
import com.estebi.fogo1.databinding.FragmentUserBinding
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


        user = FirebaseAuth.getInstance().currentUser!!;
        binding.textUser.text = user.email

        binding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Log.w(ContentValues.TAG, FirebaseAuth.getInstance().signOut().toString())
            val intent = Intent (activity, LoginActivity::class.java)
            activity?.startActivity(intent)
            activity?.finish()
        }

        binding.deleteAccount.setOnClickListener {
            FirebaseAuth.getInstance().currentUser?.delete()
            Toast.makeText(context, "The account has been deleted",
                Toast.LENGTH_SHORT).show()
            val intent = Intent (activity, SignInActivity::class.java)
            activity?.startActivity(intent)
            activity?.finish()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}