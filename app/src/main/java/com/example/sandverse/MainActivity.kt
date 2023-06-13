package com.example.sandverse

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.sandverse.ui.screens.LoginScreen
import com.example.sandverse.ui.screens.RoomScreen
import com.example.sandverse.ui.screens.WiFiDirectScreen

class MainActivity : ComponentActivity() {

    private val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }

    private var channel: WifiP2pManager.Channel? = null
    private var receiver: BroadcastReceiver? = null
    private lateinit var wifiP2pManager: WifiP2pManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") { LoginScreen(navController) }
                    composable("wifiDirect") {
                        WiFiDirectScreen(navController, wifiP2pManager = wifiP2pManager, channel = channel)
                    }
                    composable("room") { RoomScreen(navController) }
                }
            }
        }

        wifiP2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = wifiP2pManager.initialize(this, mainLooper, null)
        channel?.also { channel ->
            receiver = WifiDirectBroadcastReceiver(wifiP2pManager, channel, this)
        }
    }

    override fun onResume() {
        super.onResume()
        receiver?.also { receiver ->
            registerReceiver(receiver, intentFilter)
        }
    }

    override fun onPause() {
        super.onPause()
        receiver?.also { receiver ->
            unregisterReceiver(receiver)
        }
    }

}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        LoginScreen(navHostController = NavHostController(LocalContext.current))
    }
}