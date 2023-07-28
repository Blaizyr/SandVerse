package com.example.sandverse.services.wifip2p

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.example.sandverse.R
import com.example.sandverse.services.Permission
import com.example.sandverse.services.PermissionManager
import com.example.sandverse.services.wifip2p.DeviceListInfoHolder.deviceAddress
import com.example.sandverse.services.wifip2p.DeviceListInfoHolder.deviceName
import com.example.sandverse.services.wifip2p.DeviceListInfoHolder.devices
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WifiDirectBroadcastReceiver(
    private val wifiP2pManager: WifiP2pManager,
) : BroadcastReceiver(), KoinComponent {
    private val permissionManager: PermissionManager by inject()
    private val channel: WifiP2pManager.Channel by inject()

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                when (intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)) {

                    WifiP2pManager.WIFI_P2P_STATE_ENABLED -> {
                        if (permissionManager.checkPermission(context,
                                Permission.ACCESS_FINE_LOCATION
                            )) {
                            Log.d("Receiver", context.getString(R.string.wifi_enabled))
                        }
                    }

                    else -> {
                        Log.d("Receiver", context.getString(R.string.wifi_not_enabled))
                    }

                }
            }

            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                if (permissionManager.checkPermission(context, Permission.ACCESS_FINE_LOCATION)) {
                    try {
                        wifiP2pManager.requestPeers(channel) { peers: WifiP2pDeviceList ->
                            devices.clear()
                            if (peers.deviceList.isNotEmpty()) {
                                // TODO: !!!!! Implement ready methods from WifiP2P API for peer management!!
                                devices.addAll(peers.deviceList.map { it.deviceName.toString() })
                                deviceAddress.addAll(peers.deviceList.map { it.deviceAddress.toString() })
                                deviceName.addAll(peers.deviceList.map { it.deviceName.toString() })
                                Log.d(
                                    "BroadcastReceiver",
                                    context.getString(R.string.devices_found) + " "
                                            + deviceName.toString() + " "
                                            + deviceAddress.toString()
                                )
                            } else {
                                Log.d("BroadcastReceiver", context.getString(R.string.devices_not_found))
                            }
                        }
                    } catch (e: SecurityException) {
                        Log.e("BroadcastReceiver", "SecurityException: ${e.message}")
                    }
                }
            }

            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                // Respond to new connection or disconnections
            }

            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                // Respond to this device's wifi state changing
            }
        }
    }
}


object DeviceListInfoHolder {
    val devices = mutableStateListOf<String>()
    val deviceAddress = mutableStateListOf<String>()
    val deviceName = mutableStateListOf<String>()
    var actualConnectionAddress: String? = null
    var actualConnectionIP: String? = null
}
