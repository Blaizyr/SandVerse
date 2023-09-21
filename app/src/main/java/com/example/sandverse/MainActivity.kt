package com.example.sandverse

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.couchbase.lite.CouchbaseLite
import com.example.sandverse.services.NavHolder
import com.example.sandverse.services.NavigatorHolder
import com.example.sandverse.ui.screens.ColorSyncScreen
import com.example.sandverse.viewmodels.WifiVM
import com.example.sandverse.ui.screens.LoginScreen
import com.example.sandverse.ui.screens.RoomScreen
import com.example.sandverse.ui.screens.WiFiDirectScreen
import com.example.sandverse.viewmodels.MainVM
import org.koin.android.ext.android.inject

open class MainActivity : ComponentActivity() {

    private val wifiVM: WifiVM by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navigatorHolder: NavigatorHolder by inject()
                NavHolder(navigatorHolder)

                NavHost(navController = navigatorHolder.navController!!, startDestination = "login") {
                    composable("login") {
                        LoginScreen(
                            nav = navigatorHolder.navController!!,
                        )
                    }
                    composable("wifiDirect") {
                        WiFiDirectScreen(
                            nav = navigatorHolder.navController!!,
                        )
                    }
                    composable("room") {
                        RoomScreen(
                            nav = navigatorHolder.navController!!,
                        )
                    }
                    composable("color") {
                        ColorSyncScreen(
                        )
                    }

                }
            }
        }

        try {
            CouchbaseLite.init(applicationContext)
            Log.v("MainActivity", "Couchbase Lite successfully initialized.")
        } catch (e: ExceptionInInitializerError) {
            Log.e("MainActivity", "Couchbase Lite cannot be initialized/", e)
        }
    }

    override fun onResume() {
        super.onResume()
        wifiVM.registerReceiver(applicationContext)
    }

    override fun onPause() {
        super.onPause()
        wifiVM.unregisterReceiver(applicationContext)
    }

    override fun onDestroy() {
        super.onDestroy()
        wifiVM.disconnect()
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        LoginScreen(
            nav = NavHostController(LocalContext.current),
            mainVM = MainVM()
        )
    }
}
