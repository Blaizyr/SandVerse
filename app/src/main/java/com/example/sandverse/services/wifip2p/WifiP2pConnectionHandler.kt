package com.example.sandverse.services.wifip2p

import android.content.Context
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log.*
import com.example.sandverse.services.NavigatorHolder
import com.example.sandverse.services.Permission
import com.example.sandverse.services.PermissionManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class WifiP2pConnectionHandler(
    private val wifiP2pManager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel
    ): KoinComponent{
    private val permissionManager: PermissionManager by inject()
    private val navController: NavigatorHolder by inject()


    // Connect with a device
    fun connect(
        context: Context,
        config: WifiP2pConfig,
    ) {
        if (permissionManager.checkPermission(context, Permission.ACCESS_FINE_LOCATION)) {
            val channel = this.channel
            d("Connection (WifiVM)", "Trying to WiFi P2P connect (1/2)")
            wifiP2pManager.connect(channel, config, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    navController.navController?.navigate("room")
                    v("WifiP2pConnectionHandler", "Successfully Connected! (2/2)")
                }

                override fun onFailure(reason: Int) {
                    e("Connection (WifiVM)", "Connection failed, $reason. $context")
                }
            })
        }
    }

    // Cancel the connection
    fun disconnect() {
        wifiP2pManager.cancelConnect(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                d("Connection (WiFi ViewModel)", "Disconnection success!")
                TODO("navController - back to WifiDirectScreen, and closing all the room features")
            }

            override fun onFailure(errorCode: Int) {
                e("Connection (WiFi ViewModel)", "Disconnection failed, $errorCode")
            }
        })
    }
}
