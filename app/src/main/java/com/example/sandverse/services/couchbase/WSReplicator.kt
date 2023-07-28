package com.example.sandverse.services.couchbase

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.couchbase.lite.BasicAuthenticator
import com.couchbase.lite.CouchbaseLiteException
import com.couchbase.lite.ListenerToken
import com.couchbase.lite.Replicator
import com.couchbase.lite.ReplicatorConfigurationFactory
import com.couchbase.lite.ReplicatorType
import com.couchbase.lite.URLEndpoint
import com.couchbase.lite.newConfig
import com.example.sandverse.DBManager.ExampleOfDBManager.dbManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.lang.IllegalStateException
import java.net.URI

@Suppress("unused")
class WSReplicator(coroutineScope: CoroutineScope) : KoinComponent {
    private var token: ListenerToken? = null
    private val context: Context by inject()

    private var coll = dbManager.createCollection("coll")

    // Property to hold the Replicator instance
    internal var repl: Replicator? = null

    // Suspend function to initialize the Replicator
    suspend fun initializeReplicator(ip: String? = null, port: Int? = null): Replicator {
        // Get the URLEndpoint using a coroutine
        val endpoint = theEndpointListener(ip, port) ?: throw IllegalStateException("Failed to obtain URLEndpoint")

        // Return the initialized Replicator
        return Replicator(
            ReplicatorConfigurationFactory.newConfig(
                collections = mapOf(setOf(coll) to null),
                target = endpoint,
                type = ReplicatorType.PUSH_AND_PULL,
                continuous = true,
                heartbeat = 60,
                authenticator = BasicAuthenticator("valid.user", "valid.password.string".toCharArray()),
                acceptOnlySelfSignedServerCertificate = true
            )
        )
    }

    // The Replicator property will be initialized using a coroutine
    init {
        // Launch a coroutine to initialize the Replicator
        coroutineScope.launch {
//            repl = initializeReplicator()

            // Add a change listener
            token = repl?.addChangeListener { change ->
                val err: CouchbaseLiteException? = change.status.error
                if (err != null) {
                    Log.e("WSReplicator", "Error code :: ${err.code}", err)
                }
            }

            // Check the status
            repl?.status?.let {
                val progress = it.progress
                Log.i(
                    "WSReplicator",
                    "The Replicator is ${it.activityLevel} and has processed ${progress.completed} of ${progress.total} changes"
                )
            }
        }
    }

    // Other functions and logic for WSReplicator
    fun registerToken(): ListenerToken {
        return token!!
    }

    fun replicatorStop() {
        repl?.stop()
    }

    // Suspend function to obtain the URLEndpoint asynchronously
    private suspend fun theEndpointListener(ip: String? = null, port: Int? = null): URLEndpoint? {
        var uri: URLEndpoint? = null
        try {
              // Assign the value to the uri variable (assuming ipAddress and port are already set)
            uri = URLEndpoint(URI("ws://$ip:$port/db"))
            Log.v("WSReplicator", "Port: $port, IP: $ip. Uri: $uri")
            Toast.makeText(context, "URI: $uri", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Log.e("theEndpointListener", "Failed to set IP address", e)
        }
        return uri
    }
}
