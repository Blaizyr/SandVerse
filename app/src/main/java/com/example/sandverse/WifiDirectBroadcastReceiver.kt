package com.example.sandverse

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import com.example.sandverse.DeviceListHolder.devices


class WifiDirectBroadcastReceiver(
    private val wifiP2pManager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel,
    private val activity: MainActivity
) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {

            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                when (intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)) {
                    WifiP2pManager.WIFI_P2P_STATE_ENABLED -> {
                        Toast.makeText(activity, "Wi-Fi P2P is enabled!", Toast.LENGTH_LONG).show()
                    }

                    else -> {
                        Toast.makeText(activity, "Wi-Fi P2P isn't enabled", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }

            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                wifiP2pManager.requestPeers(channel) { peers: WifiP2pDeviceList ->
                    devices.clear()
                    if (peers.deviceList.isNotEmpty()) {
                        devices.addAll(peers.deviceList.map { it.deviceName.toString() })
                        Toast.makeText(
                            activity,
                            "Znaleziono ${devices.size} urządzeń",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(activity, "Nie znaleziono urządzeń", Toast.LENGTH_LONG)
                            .show()
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


object DeviceListHolder {
    val devices = mutableStateListOf<String>()
}

