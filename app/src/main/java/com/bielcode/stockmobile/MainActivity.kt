package com.bielcode.stockmobile

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.emptyPreferences
import com.bielcode.stockmobile.data.preferences.Account
import com.bielcode.stockmobile.ui.screens.navigation.AuthNavigation
import com.bielcode.stockmobile.ui.screens.navigation.UserNavigation
import com.bielcode.stockmobile.ui.theme.StockMobileTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory


class MainActivity : ComponentActivity() {
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel.checkUserRole().observe(this) { preferences ->
            Log.d("Preferences", "$preferences")
            if (preferences == Account(
                    null,
                    null,
                    null,
                    null
                )
                ||
                preferences == Account(
                    "",
                    "",
                    "",
                    ""
                )
            ) {
                setContent {
                    StockMobileTheme {
                        Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
                            AuthNavigation()
                        }
                    }
                }
            } else {
                setContent {
                    StockMobileTheme {
                        Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
                            UserNavigation(role = preferences.role.toString())
                        }
                    }
                }
            }
        }
    }
}
