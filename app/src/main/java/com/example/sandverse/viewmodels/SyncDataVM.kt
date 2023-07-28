package com.example.sandverse.viewmodels

import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import android.util.Log
import androidx.lifecycle.ViewModel
import com.couchbase.lite.CouchbaseLiteException
import com.couchbase.lite.Database
import com.example.sandverse.services.NetServicesDiscoverer
import com.example.sandverse.services.couchbase.WSListener
import com.example.sandverse.services.couchbase.WSReplicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Suppress("SpellCheckingInspection")
class SyncDataVM : ViewModel(), KoinComponent {
    private val nsDiscoverer: NetServicesDiscoverer by inject()
    private val wsReplicator: WSReplicator by inject()
    private val wsListener: WSListener by inject()
    private var currentPort: Int? = 5001
    private var currentIP: String? = null


    fun startReplication(context: Context? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            replicate(context)
        }
    }

    private suspend fun replicate(context: Context? = null) {
        // TODO: setIP oraz setPort zrealizować PRZED INSTANCJALIZACJĄ WSReplicatora  i przekazać mu je jako argumenty.
        try {
            Log.d("SyncDataVM", "Trying to get IP address of service Host...")
            getIP(context)
        } catch (e: Exception) {
            Log.e("SyncDataVM", "$e error with service Host IP address getting.")
        } finally {
            try {
                if (currentIP != null && currentPort != null) {
                    initRepl()
                    startRepl()
                    Log.d("SyncDataVM", "SyncDataVM started the replicator $currentIP,$currentPort")
                } else {
                    Log.e("SyncDataVM", "IP address or Port is null. $currentIP,$currentPort")
                }
            } catch (e: CouchbaseLiteException) {
                e.printStackTrace()
                Log.e(
                    "SyncDataVM",
                    "SyncDataVM cannot start the replicator. $currentIP,$currentPort",
                    e
                )
            } finally {
                delay(2000)
                Log.d("SyncDataVM", "Udało się?, zweryfikuj adres: $currentIP")
            }
        }
    }

    private suspend fun getIP(context: Context?): String? {
        val device: WifiP2pDevice = nsDiscoverer.discoverService(context)
        return device.deviceAddress
    }


    private suspend fun initRepl() {
        wsReplicator.initializeReplicator(currentIP!!, currentPort!!)
    }

    private suspend fun startRepl() {
        wsReplicator.repl?.start()
    }

    fun startToListen(db: Database? = null) {
        try {
            // TODO: Wprowadzić zmiany, tak aby info o porcie szło bezpośrednio między jednym a drugim VModelem
            Log.v("SyncDataVM", "Listener starting... (1/2)")
            CoroutineScope(Dispatchers.IO).launch {
                wsListener.p2pListenURL(db)
                delay(200)
                currentPort = getListenerPort()
                delay(100)
                Log.v("SyncDataVM", "Experiment with timing. PORT: $currentPort")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("SyncDataVM", "SyncDataVM cannot start listening to the URL")
        }
    }


    fun getListenerPort(): Int {
        return try {
            wsListener.getPort() ?: 5001
            Log.d("SyncDataVM", "Port set")
        } catch (e: Exception) {
            Log.e("SyncDataVM", "Error retrieving listener port", e)
            -1
        }
    }

    fun onStopListen() {
        wsListener.listenerStop()
    }

    fun onStopReplicate() {
        wsReplicator.replicatorStop()
    }

    override fun onCleared() {
        super.onCleared()
        onStopListen()
        onStopReplicate()
    }

    /**
     * Extension function to suspend execution and wait for the result of the deferred [String] value.
     * This function is intended to be used inside a suspend function.
     */
    suspend fun String?.await(): String? = suspendCancellableCoroutine { continuation ->
        if (this != null) {
            continuation.resume(this) {}
        } else {
            continuation.resume(null) {}
        }
    }
}
