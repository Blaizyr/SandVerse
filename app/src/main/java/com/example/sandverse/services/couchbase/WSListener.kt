package com.example.sandverse.services.couchbase

import android.util.Log
import com.couchbase.lite.Database
import com.couchbase.lite.ListenerPasswordAuthenticator
import com.couchbase.lite.MessageEndpointListener
import com.couchbase.lite.MessageEndpointListenerConfigurationFactory
import com.couchbase.lite.ProtocolType
import com.couchbase.lite.URLEndpointListener
import com.couchbase.lite.URLEndpointListenerConfigurationFactory
import com.couchbase.lite.newConfig
import com.example.sandverse.DBManager.ExampleOfDBManager.dbManager
import com.example.sandverse.DBManager.ExampleOfDBManager.userDB

@Suppress("unused")
class WSListener {
    private var thisListener: URLEndpointListener? = null
    private var port: Int? = null

    fun p2pListenURL(db: Database? = userDB)  {
        val p2pListenerURL = URLEndpointListener(
            URLEndpointListenerConfigurationFactory.newConfig(
                collections = db?.collections,
//                networkInterface = "",
//                port = 5001,
                authenticator = ListenerPasswordAuthenticator { user, pwd ->
                    (user == "user") && (String(pwd) == "666")
                },
                enableDeltaSync = true,
                disableTls = true
            )
        )
        try {
            p2pListenerURL.start()

            port = p2pListenerURL.port
            Log.d("WSListener", "Port: $port")
            Log.d("WSListener", "Listener started successfully!! (2/2)")
        } catch (e: Exception) {
            Log.e("WSListener", "There's problem with URL Endpoint Listener", e)
        }
        thisListener = p2pListenerURL
    }
    fun getPort(): Int? {
        Log.d("WSListener", "gettin the listener port $port")
        return port
    }

    fun p2pListenMessages(db: Database? = userDB){
        val p2pListenerMessage = MessageEndpointListener(
            MessageEndpointListenerConfigurationFactory.newConfig(
                collections = db?.collections,
                protocolType = ProtocolType.MESSAGE_STREAM
            )
        )
        val connection = PassivePeerConnection()
        p2pListenerMessage.accept(connection)
        //thisListener = p2pListenerMessage
    }


    fun listenerStatusCheck(db: Database? = dbManager.createDb("")) {
        val listener = URLEndpointListener(
            URLEndpointListenerConfigurationFactory
                .newConfig(
                    collections = db!!.collections
                )
        )
        listener.start()
        val connectionCount = listener.status?.connectionCount
        val activeConnectionCount = listener.status?.activeConnectionCount
    }

    fun listenerStop() {
        val listener = thisListener
        thisListener = null
        val connection = PassivePeerConnection()
        listener?.stop()
    }

    companion object
}
