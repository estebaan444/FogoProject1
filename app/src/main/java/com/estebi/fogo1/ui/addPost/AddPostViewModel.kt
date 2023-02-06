package com.estebi.fogo1.ui.addPost

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddPostViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is addPost Fragment"
    }
    val text: LiveData<String> = _text
}