package com.example.sandverse.services

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest
import android.util.Log
import com.example.sandverse.services.wifip2p.DeviceListInfoHolder
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Suppress("unused")
class NetServicesDiscoverer(
    private val nsdManager: NsdManager,
) : KoinComponent {
    private val permissionManager: PermissionManager by inject()
    private lateinit var discoveryListener: NsdManager.DiscoveryListener
    private val wifiP2pManager: WifiP2pManager by inject()
    private val channel: WifiP2pManager.Channel by inject()

    private val buddies = mutableMapOf<String, String>()


    internal suspend fun discoverService(context: Context? = null): WifiP2pDevice {
        return withContext(Dispatchers.Default) {
            val callbackDeferred = CompletableDeferred<WifiP2pDevice?>()
            val txtListener = DnsSdTxtRecordListener { fullDomain, record, device ->
                Log.d("NSDiscoverer", "Net Service record available - $record")
                record["buddyname"]?.also {
                    buddies[device.deviceAddress] = it
                }
            }

            val servListener = DnsSdServiceResponseListener { instanceName, registrationType, resourceType ->
                // Update the device name with the human-friendly version from
                // the DnsTxtRecord, assuming one arrived.
                resourceType.deviceName = buddies[resourceType.deviceAddress] ?: resourceType.deviceName
                callbackDeferred.complete(resourceType)
                Log.d("NSDiscoverer", "onBonjourServiceAvailable $instanceName")
            }

            wifiP2pManager.setDnsSdResponseListeners(channel, servListener, txtListener)

            val serviceRequest = WifiP2pDnsSdServiceRequest.newInstance()

            wifiP2pManager.addServiceRequest(
                channel,
                serviceRequest,
                object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        Log.d("NSDiscoverer", "Service request success!")
                    }

                    override fun onFailure(e: Int) {
                        callbackDeferred.completeExceptionally(Exception("Service request failed. Error N.$e"))
                        Log.e("NSDiscoverer", "Service request failed. Error N.$e")
                    }
                }
            )

            permissionManager.checkPermission(context!!, Permission.ACCESS_FINE_LOCATION)

            wifiP2pManager.discoverServices(
                channel,
                object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        Log.d("NSDiscoverer", "Service discover success!")
                    }

                    override fun onFailure(e: Int) {
                        callbackDeferred.completeExceptionally(Exception("Service discover failed. Error N.$e"))
                        Log.e("NSDiscoverer", "Service discover failed. Error N.$e")
                    }
                }
            )

            val callbackService = callbackDeferred.await()
            if (callbackService != null) {
                callbackService
            } else {
                throw NullPointerException("Service response does not contain WifiP2pDevice.")
            }
        }
    }


            //  ------------- RESOLVER -------------  //
    /*
           private val resolveListener = object : NsdManager.ResolveListener {

               override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                   // Called when the resolve fails. Use the error code to debug.
                   Log.e("NetServiceDiscoverer", "Resolve failed: $errorCode")
               }

               override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                   Log.v("NetServiceDiscoverer", "Resolve Succeeded. $serviceInfo")

                   if (serviceInfo.serviceName == mServiceName) {
                       Log.d("NetServiceDiscoverer", "Same IP.")
                       return
                   }
                   mService = serviceInfo
                   val port: Int = serviceInfo.port
                   val host: InetAddress = serviceInfo.host
               }
           }


        private fun discoverService() {
     Callback includes:
             * fullDomain: full domain name: e.g "printer._ipp._tcp.local."
             * record: TXT record dta as a map of key/value pairs.
             * device: The device running the advertised service.

            val txtListener = WifiP2pManager.DnsSdTxtRecordListener { fullDomain, record, device ->
                Log.d("NSDiscoverer", "DnsSdTxtRecord available -$record")
                record["buddyname"]?.also {
                    buddies[device.deviceAddress] = it
                }
            }

    *//*val serviceListener = DnsSdServiceResponseListener { instanceName, registrationType ->

        }*//*

        wifiP2pManager.setDnsSdResponseListeners(channel, null, txtListener)


        val serviceRequest = WifiP2pDnsSdServiceRequest.newInstance()
        wifiP2pManager.addServiceRequest(
            channel,
            serviceRequest,
            object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    TODO("Not yet implemented")
                }

                override fun onFailure(p0: Int) {
                    TODO("Not yet implemented")
                }
            }
        )
    }
    */


             //  -------- SERVICES DISCOVERY BY LAN -----------  //
    suspend fun discoverServices(serviceType: String): String {
        return suspendCoroutine { continuation ->
            nsdManager.discoverServices(
                serviceType,
                NsdManager.PROTOCOL_DNS_SD,
                object : NsdManager.DiscoveryListener {

                    override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                        Log.e(
                            "NetServiceDiscoverer",
                            "Failed to START discover. Error code: $errorCode"
                        )
                        continuation.resume("Error $errorCode")
                    }

                    override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                        Log.e(
                            "NetServiceDiscoverer",
                            "Failed to STOP discover. Error code: $errorCode"
                        )
                    }

                    override fun onDiscoveryStarted(serviceType: String) {
                        Log.v("NetServiceDiscoverer", "Discovery started.")
                    }

                    override fun onDiscoveryStopped(serviceType: String) {
                        Log.v("NetServiceDiscoverer", "Discovery stopped.")
                    }

                    override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                        // Found Service
                        val ipAddress = serviceInfo.host.hostAddress
                        DeviceListInfoHolder.actualConnectionIP = ipAddress
                        Log.v("NetServiceDiscoverer", "Service found. $ipAddress")
                        nsdManager.stopServiceDiscovery(this)
                        continuation.resume("$ipAddress")
                    }

                    override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                        Log.v("NetServiceDiscoverer", "Service lost.")
                    }
                })
        }
    }


    companion object NSDManager
}

