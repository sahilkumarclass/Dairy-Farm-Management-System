package com.sahilkumar.dfms

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.sahilkumar.dfms.core.data.SessionState
import com.sahilkumar.dfms.core.ui.SessionViewModel
import com.sahilkumar.dfms.core.ui.components.LoadingState
import com.sahilkumar.dfms.core.ui.theme.DairySmartTheme
import com.sahilkumar.dfms.feature.login.LoginScreen
import com.sahilkumar.dfms.nav.MainShell
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DairySmartTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val sessionViewModel: SessionViewModel = hiltViewModel()
                    val state by sessionViewModel.state.collectAsStateWithLifecycle()
                    when (val s = state) {
                        SessionState.Loading -> LoadingState()
                        SessionState.LoggedOut -> LoginScreen()
                        is SessionState.LoggedIn -> MainShell(
                            session = s.session,
                            onLogout = { sessionViewModel.logout() },
                        )
                    }
                }
            }
        }
    }
}
