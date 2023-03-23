package com.estebi.fogo1.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.estebi.fogo1.databinding.FragmentSearchBinding
import com.estebi.fogo1.models.User
import com.estebi.fogo1.repository.user.SearchUserRepository.Companion.getUserNameList
import com.estebi.fogo1.repository.user.SearchUserRepository.Companion.getUserNameListFiltered
import com.estebi.fogo1.ui.goToUser.GoToUserActivity
import com.estebi.fogo1.ui.search.SearchViewModel.Companion.getUserEmailKey

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    private val binding get() = _binding!!
    private val userAdapter = UserAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root
        (activity as AppCompatActivity).supportActionBar?.hide()

        //save search data in variable to use in observeUser
        val searchView = binding.searchUserBar.text
        binding.searchButton.setOnClickListener {
            observeUser(searchView.toString())
        }

        val userRecyclerView = binding.recyclerUser
        userRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        userRecyclerView.setHasFixedSize(true)
        userRecyclerView.adapter = userAdapter
        userAdapter.setItemListener(object : UserAdapter.onItemClickListener {
            override fun onItemClick(user: User) {
                var userEmailKey = user.userEmail
                Intent(requireContext(), GoToUserActivity::class.java).apply {
                    startActivity(this)
                }
                getUserEmailKey = userEmailKey
            }
        })
        observeUser(searchView.toString())
        return root
    }

    private fun observeUser(searchData: String) {
        if (searchData == "") {
            getUserNameList().observe(requireActivity()) { userList ->
                userAdapter.setListData(userList)
                userAdapter.notifyDataSetChanged()
            }
        }else{
            getUserNameListFiltered(searchData).observe(requireActivity()) { userList ->
                val filteredList = userList.filter { it.userName.contains(searchData) }
                userAdapter.filterList(filteredList)
                userAdapter.notifyDataSetChanged()
            }
        }
    }
}