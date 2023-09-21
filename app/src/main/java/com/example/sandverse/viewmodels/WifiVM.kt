package com.example.sandverse.viewmodels

import android.content.Context
import android.content.IntentFilter
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log.*
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sandverse.data.PeersUIState
import com.example.sandverse.data.UIState
import com.example.sandverse.services.NavigatorHolder
import com.example.sandverse.services.NetServiceRegistrator
import com.example.sandverse.services.Permission
import com.example.sandverse.services.PermissionManager
import com.example.sandverse.services.wifip2p.OnPeersDataReceivedListener
import com.example.sandverse.services.wifip2p.WifiDirectBroadcastReceiver
import com.example.sandverse.services.wifip2p.WifiP2pConnectionHandler
import com.example.sandverse.ui.screens.WiFiDirectScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.lang.Math.random

class WifiVM(
    internal val wifiP2pManager: WifiP2pManager,
    internal val channel: WifiP2pManager.Channel,
    internal val receiver: WifiDirectBroadcastReceiver,
) :
    ViewModel(),
    KoinComponent,
    OnPeersDataReceivedListener {

    private val _updatesCount: MutableStateFlow<Int> = MutableStateFlow(0)
    val updatesCount: StateFlow<Int> = _updatesCount.asStateFlow()
    //--------- PEERS ---------//
    private val _peersData = MutableStateFlow(UIState(listOf(PeersUIState( "", "", "", "", 1 ))))
    val peersData: StateFlow<UIState> = _peersData.asStateFlow()

    // -------- KOIN INJECTED -------- //
    private val netServiceRegistrator: NetServiceRegistrator by inject()
    internal val permissionManager: PermissionManager by inject()
    private val connectionHandler: WifiP2pConnectionHandler by inject()


    private val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }


    init {
        _peersData
            .onEach { updatedUIState ->
                // To check if the reference changes (if a new instance is created)
                val isSameInstance = updatedUIState === _peersData.value

                // To check if the content of the list is the same
                val isContentSame = updatedUIState.list == _peersData.value.list

                d("WifiVM", "isSameInstance: $isSameInstance, isContentSame: $isContentSame")
            }
            .launchIn(viewModelScope)
    }




    override fun onPeersDataReceived(peers: Collection<WifiP2pDevice>) {
        d("WifiVM", "onPeersDataReceived func induced")

        val updatedPeersList: List<PeersUIState> = peers.map { device ->
            d("WifiVM", "Device: ${device.deviceName} ${device.deviceAddress}")
            PeersUIState(
                device.deviceAddress,
                device.deviceName,
                device.primaryDeviceType,
                device.secondaryDeviceType ?: "Unknown",
                device.status
            )
        }

        _peersData.update {
            it.copy(list = updatedPeersList)

        }.also { _updatesCount.update { it + 1 } }
        // Log the updated peers data
        d("WifiVM", "Updated peers data: $updatedPeersList")
    }

    ///// -------- RECEIVER -------- /////
    fun registerReceiver(context: Context) {
        try {
            context.registerReceiver(receiver, intentFilter)
            receiver.setPeersDataListener(this)
            d("WifiVM", "Receiver registered!")
        } catch (e: Exception) {
            e("WifiVM", "Receiver registration problem", e)
        }
    }

    fun unregisterReceiver(context: Context) {
        context.unregisterReceiver(receiver)
    }

    ////// ------- CONNECTION -------- ///////
    fun discoverPeers(context: Context) {
        if (permissionManager.checkPermission(context, Permission.ACCESS_COARSE_LOCATION)) {
            val channel = this.channel
            wifiP2pManager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    d("WifiVM", "Peer discovery begins. $context")
                }

                override fun onFailure(p0: Int) {
                    e("WifiVM", "Peer discovery failure. ID: $p0. $context")
                }
            })
        }
    }

    // Select a device
    /*fun selectDevice(index: Int) {
        val device = peersData.value.getOrNull(index)
        _selectedDevice.value = device
    }*/
    /*
        private fun selectDevice(address: String) {
    //        val selectedDevice = DeviceListInfoHolder.deviceAddress[index]
    //        DeviceListInfoHolder.actualConnectionAddress = selectedDevice

            val selectedDevice = peersData.value.getOrNull(address).
            selectedDevice?.let {
                val config = WifiP2pConfig().apply {
                    deviceAddress = selectedDevice.toString()
                    wps.setup = WpsInfo.PBC
                }
                d("WifiVM", "Device $selectedDevice selected")
            }
        }
    */

    fun connect(ctx: Context, address: String) {
        val config = WifiP2pConfig().apply {
            deviceAddress = address
            wps.setup = WpsInfo.PBC
        }
        d("WifiVM", "Device $address selected")
        connectionHandler.connect(config = config, context = ctx)
    }

    fun disconnect() {
        connectionHandler.disconnect()
    }

    ////// ------- Service Registration -------- ///////
    // Register service on P2P connection
    fun registerServiceListener(context: Context?, port: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            d("WifiVM", "Service registration started (1/3)")
            netServiceRegistrator.registerServiceP2P(context, port)
        }
    }
}