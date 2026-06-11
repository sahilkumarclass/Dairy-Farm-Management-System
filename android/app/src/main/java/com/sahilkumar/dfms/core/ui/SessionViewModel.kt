package com.sahilkumar.dfms.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sahilkumar.dfms.core.data.SessionManager
import com.sahilkumar.dfms.core.data.SessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionManager: SessionManager,
) : ViewModel() {

    val state: StateFlow<SessionState> = sessionManager.state

    fun logout() {
        viewModelScope.launch { sessionManager.clear() }
    }
}
