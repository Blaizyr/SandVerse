package com.example.sandverse.viewmodels

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.sandverse.services.NetServiceRegistrator
import com.example.sandverse.services.Permission
import com.example.sandverse.services.PermissionManager
import com.example.sandverse.services.wifip2p.DeviceListInfoHolder
import com.example.sandverse.services.wifip2p.WifiP2pConnectionListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WifiVM(
    private val wifiP2pManager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel,
    private val receiver: BroadcastReceiver
) : ViewModel(), KoinComponent {

    private val deviceListInfoHolder: DeviceListInfoHolder by inject()
    private val netServiceRegistrator: NetServiceRegistrator by inject()
    private val permissionManager: PermissionManager by inject()
    private var connectionListener: WifiP2pConnectionListener? = null


    private val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }

    // Initialize WiFi P2P
    fun initializeWifiP2p(context: Context) : WifiP2pManager.Channel {
        return channel
    }

    // Register the receiver
    fun registerReceiver(context: Context) {
        try {
            context.registerReceiver(receiver, intentFilter)
            Log.d("WifiVM", "Receiver registered!")
        } catch (e: Exception) {
            Log.e("WifiVM", "Receiver registration problem", e)
        }
    }

    // Unregister the receiver
    fun unregisterReceiver(context: Context) {
        context.unregisterReceiver(receiver)
    }

    // Discover peers
    fun discoverPeers(context: Context) {
        if (permissionManager.checkPermission(context, Permission.ACCESS_COARSE_LOCATION)) {
            val channel = this.channel
            wifiP2pManager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.d("WifiVM", "Peer discovery begins. $context")
                }

                override fun onFailure(p0: Int) {
                    Log.e("WifiVM", "Peer discovery failure. ID: $p0. $context")
                }
            })
        }
    }

    // Select a device
    fun selectDevice(index: Int): WifiP2pConfig {
        val selectedDevice = DeviceListInfoHolder.deviceAddress[index]
        DeviceListInfoHolder.actualConnectionAddress = selectedDevice
        val config = WifiP2pConfig().apply {
            deviceAddress = selectedDevice
            wps.setup = WpsInfo.PBC
        }
        Log.d("WifiVM", "Device $selectedDevice selected")
        return config
    }
//
    fun setConnectionListener(listener: WifiP2pConnectionListener, navController: NavController) {
        connectionListener = listener
        connectionListener?.setNavController(navController)
        Log.d("WifiVM", "Connection Listener is set")
    }

    fun registerListenerService(context: Context?, port: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("WifiVM", "Service registration started (1/3)")
            netServiceRegistrator.registerServiceP2P(context, port)
        }
    }

    // Connect with a device
    fun connectWith(
        context: Context,
        config: WifiP2pConfig,
    ) {
        if (permissionManager.checkPermission(context, Permission.ACCESS_FINE_LOCATION)) {
            val channel = this.channel
            Log.d("Connection (WifiVM)", "Trying to WiFi P2P connect (1/2)")
            wifiP2pManager.connect(channel, config, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    connectionListener?.onConnectionSuccess()
                }

                override fun onFailure(reason: Int) {
                    Log.e("Connection (WifiVM)", "Connection failed, $reason. $context")
                }
            })
        }
    }

    // Cancel the connection
    fun cancelConnect() {
        wifiP2pManager.cancelConnect(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                Log.d("Connection (WiFi ViewModel)", "Disconnection success!")
            }

            override fun onFailure(errorCode: Int) {
                Log.e("Connection (WiFi ViewModel)", "Disconnection failed, $errorCode")
            }
        })
    }
}