@file:Suppress("DEPRECATION")

package com.example.sandverse.services.wifip2p

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.util.Log.*
import com.example.sandverse.R
import com.example.sandverse.services.Permission
import com.example.sandverse.services.PermissionManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class WifiDirectBroadcastReceiver(
    private val wifiP2pManager: WifiP2pManager,
) :
    BroadcastReceiver(),
    KoinComponent
{
    internal val permissionManager: PermissionManager by inject()
    internal val channel: WifiP2pManager.Channel by inject()

    internal var peersDataListener: OnPeersDataReceivedListener? = null

    fun setPeersDataListener(listener: OnPeersDataReceivedListener) {
        peersDataListener = listener
    }
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                when (intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)) {

                    WifiP2pManager.WIFI_P2P_STATE_ENABLED -> {
                        if (permissionManager.checkPermission(context,
                                Permission.ACCESS_FINE_LOCATION
                            )) {
                            d("Receiver", context.getString(R.string.wifi_enabled))
                        }
                    }

                    else -> {
                        d("Receiver", context.getString(R.string.wifi_not_enabled))
                    }

                }
            }

            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                if (permissionManager.checkPermission(context, Permission.ACCESS_FINE_LOCATION)) {
                    try {
                        wifiP2pManager.requestPeers(channel) { peers: WifiP2pDeviceList ->
                            if (peers.deviceList.isNotEmpty()) {
                                // TODO: !!!!! Implement ready methods from WifiP2P API for peer management!!

                                peersDataListener?.onPeersDataReceived(peers.deviceList)
                                d("BroadcastReceiver", context.getString(R.string.devices_found))
                                for (peer in peers.deviceList) {
                                    d("BroadcastReceiver", "Device: ${peer.deviceName} ${peer.deviceAddress}")
                                }

                                d("BroadcastReceiver", "onPeersDataReceived executed")



//                                peersHolder.peersList by wifiVM.peersModel.collectAsState()
//                                val setPeersData: (String) -> Unit = { peersData -> setPeersData(PeersData) }

//                                devices.addAll(peers.deviceList.map { it.deviceName.toString() })
//                                deviceAddress.addAll(peers.deviceList.map { it.deviceAddress.toString() })
//                                deviceName.addAll(peers.deviceList.map { it.deviceName.toString() })

                            } else {
                                d("BroadcastReceiver", context.getString(R.string.devices_not_found))
                            }
                        }
                    } catch (e: SecurityException) {
                        e("BroadcastReceiver", "SecurityException: ${e.message}")
                    }
                }
            }

            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                d("BroadcastReceiver", "Connection Changed")

                val wifiP2pInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO, wifiP2pManager::class.java) as WifiP2pInfo?
                } else {
                    intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO) as WifiP2pInfo?
                }
                wifiP2pInfo?.let {
                    if (it.groupFormed && it.isGroupOwner) {
                        // Device is the group owner (connection established)
                        // You can perform necessary actions after the connection is established
                        d("BroadcastReceiver", "You're the group owner")

                    } else if (it.groupFormed) {
                        // Device is a part of the group, but not the owner (connection established)
                        // You can perform necessary actions after the connection is established
                        d("BroadcastReceiver", "You're group member")

                    } else {
                        // Device is disconnected from the group (connection terminated)
                        // You can perform necessary actions after the connection is terminated
                        d("BroadcastReceiver", "You aren't member of any group")
                    }
                }

            }

            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                // Respond to this device's wifi state changing
            }
        }
    }

}



interface OnPeersDataReceivedListener {
    fun onPeersDataReceived(peers: Collection<WifiP2pDevice>)
}
