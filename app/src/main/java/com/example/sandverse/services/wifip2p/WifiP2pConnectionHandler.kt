package com.example.sandverse.services.wifip2p

import android.util.Log
import androidx.navigation.NavController

interface  WifiP2pConnectionListener {
    fun onConnectionSuccess()
    fun setNavController(navController: NavController)
}

class WifiP2pConnectionHandler : WifiP2pConnectionListener {
    private var navController: NavController? = null

    override fun setNavController(navController: NavController) {
        this.navController = navController
        Log.v("WifiP2pConnectionHandler", "Nav Controller is set")
    }

    override fun onConnectionSuccess() {
        navController?.let { setNavController(it) }
        navController?.navigate("room")
        Log.v("WifiP2pConnectionHandler", "Successfully Connected! (2/2)")
    }
}
