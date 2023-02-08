package com.unipi.mpsp21043.emarketadmin.ui.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unipi.mpsp21043.emarketadmin.database.FirestoreHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            if (FirestoreHelper().getCurrentUserID() != "") {
                _isLoading.value = false
            }else
                _isLoading.value = false
        }
    }
}
