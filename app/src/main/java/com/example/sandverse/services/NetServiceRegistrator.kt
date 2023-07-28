package com.example.sandverse.services

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo
import android.util.Log
import android.widget.Toast
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.ServiceConfigurationError

class NetServiceRegistrator(
    private val serviceManager: NsdManager
    ) : KoinComponent {
     private val wifiP2pManager: WifiP2pManager by inject()
    private val permissionManager: PermissionManager by inject()
    private val channel: WifiP2pManager.Channel by inject()

    fun registerServiceLAN(port: Int) {
        // Create the NsdServiceInfo object, and populate it.
        val serviceInfo = NsdServiceInfo().apply {
            // The name is subject to change based on conflicts
            // with other services advertised on the same network.
            serviceName = "CouchBaseSync"
            serviceType = "_cblite._tcp"
            setPort(port)

        }
    }

    fun registerServiceP2P(context: Context?, port: Int) {
        val record: Map<String, String> = mapOf(
            "listen_port" to port.toString(), // serverSocket -> port
            "buddy_name" to "user${(Math.random() * 1000).toInt()}",
            "available" to "visible"
        )
        var serviceInfo: WifiP2pDnsSdServiceInfo? = null

        try {
             serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(
                 "P2pDataSync",
                 "_cblite._tcp",
                 record
             )
            Log.v("NetServiceRegistrator", "New ServiceInfo instance created successfully! (2/3)")
            Log.v("NetServiceRegistrator", "Record: $record")

        } catch (e: InstantiationException) {
            Log.e("NetServiceRegistrator", "ServiceInfo instantiation failed. $e", e)
        }

        permissionManager.checkPermission(context!!, Permission.ACCESS_FINE_LOCATION)

        try {
            wifiP2pManager.addLocalService(
                channel,
                serviceInfo,
                object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        Log.v("NetServiceRegistrator", "Service registered! $port (3/3)")
                        Log.v("NetServiceRegistrator", "$serviceInfo.")
                        Toast.makeText(context, "P2P service registered on $port port!", Toast.LENGTH_LONG).show()
                    }

                    override fun onFailure(p0: Int) {
                        Log.e("NetServiceRegistrator", "Failed to P2P service register. Error code: $p0")
                    }
                })
        } catch (e: ServiceConfigurationError) {
            Log.e("NetServiceRegistrator", "Service configuration error! $e", e)
        }

    }
}