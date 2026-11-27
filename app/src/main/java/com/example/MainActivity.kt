package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.FastBiteDatabase
import com.example.data.local.SettingsStore
import com.example.data.repository.FastBiteRepository
import com.example.ui.AppScreen
import com.example.ui.FastBiteViewModel
import com.example.ui.components.FloatingToastHost
import com.example.ui.screens.CustomerLoginScreen
import com.example.ui.screens.CustomerPortalScreen
import com.example.ui.screens.LandingScreen
import com.example.ui.screens.StaffLoginScreen
import com.example.ui.screens.StaffPortalScreen
import com.example.ui.theme.FastBiteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = FastBiteDatabase.getInstance(applicationContext)
        val settingsStore = SettingsStore(applicationContext)
        val repository = FastBiteRepository(db.fastBiteDao(), settingsStore)

        setContent {
            FastBiteTheme {
                val viewModel: FastBiteViewModel = viewModel(
                    factory = FastBiteViewModel.Factory(repository)
                )
                val state by viewModel.uiState.collectAsStateWithLifecycle()

                Box(modifier = Modifier.fillMaxSize()) {
                    when (state.ui.currentScreen) {
                        AppScreen.LANDING -> LandingScreen(
                            state = state,
                            onCustomerClick = { viewModel.openCustomerPortalEntry() },
                            onStaffClick = { viewModel.openStaffPortalEntry() }
                        )
                        AppScreen.CUSTOMER_LOGIN -> CustomerLoginScreen(
                            onBack = { viewModel.navigateTo(AppScreen.LANDING) },
                            onSignIn = { name, contact, method ->
                                viewModel.signInCustomer(name, contact, method)
                            }
                        )
                        AppScreen.STAFF_LOGIN -> StaffLoginScreen(
                            onBack = { viewModel.navigateTo(AppScreen.LANDING) },
                            onSignIn = { email, password ->
                                viewModel.signInStaff(email, password)
                            }
                        )
                        AppScreen.CUSTOMER_APP -> CustomerPortalScreen(
                            state = state,
                            viewModel = viewModel,
                            onSwitchToLanding = { viewModel.navigateTo(AppScreen.LANDING) }
                        )
                        AppScreen.STAFF_APP -> StaffPortalScreen(
                            state = state,
                            viewModel = viewModel,
                            onSwitchToLanding = { viewModel.navigateTo(AppScreen.LANDING) }
                        )
                    }

                    FloatingToastHost(
                        toast = state.ui.toastBanner,
                        onDismiss = { viewModel.dismissToast() },
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .statusBarsPadding()
                    )
                }
            }
        }
    }
}
