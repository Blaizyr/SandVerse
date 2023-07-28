package com.example.sandverse.services.couchbase

import com.couchbase.lite.Collection
import com.couchbase.lite.CouchbaseLiteException
import com.couchbase.lite.Message
import com.couchbase.lite.MessageEndpointConnection
import com.couchbase.lite.MessageEndpointListener
import com.couchbase.lite.MessageEndpointListenerConfigurationFactory
import com.couchbase.lite.MessagingCloseCompletion
import com.couchbase.lite.MessagingCompletion
import com.couchbase.lite.ProtocolType
import com.couchbase.lite.ReplicatorConnection
import com.couchbase.lite.newConfig
import java.lang.Exception

@Suppress("unused")
class PassivePeerConnection : MessageEndpointConnection {
    private var listener: MessageEndpointListener? = null
    private var replicatorConnection: ReplicatorConnection? = null

    @Throws(CouchbaseLiteException::class)
    fun startListener(collections: Set<Collection>) {
        listener = MessageEndpointListener(
            MessageEndpointListenerConfigurationFactory.newConfig(
                collections,
                ProtocolType.MESSAGE_STREAM
            )
        )
    }


    fun stopListener() {
        listener?.closeAll()
    }

    fun accept() {
        val connection = PassivePeerConnection() /* implements MessageEndpointConnection */
        listener?.accept(connection)
    }

    fun disconnect() {
        replicatorConnection?.close(null)
    }

    /* implementation of MessageEndpointConnection */
    override fun open(connection: ReplicatorConnection, completion: MessagingCompletion) {
        replicatorConnection = connection
        completion.complete(true, null)
    }

    /* implementation of MessageEndpointConnection */
    override fun close(error: Exception?, completion: MessagingCloseCompletion) {
        /* disconnect with communications framework */
        /* ... */
        /* call completion handler */
        completion.complete()
    }

    /* implementation of MessageEndpointConnection */
    override fun send(message: Message, completion: MessagingCompletion) {
        /* send the data to the other peer */
        /* ... */
        /* call the completion handler once the message is sent */
        completion.complete(true, null)
    }

    fun receive(message: Message) {
        replicatorConnection?.receive(message)
    }

}