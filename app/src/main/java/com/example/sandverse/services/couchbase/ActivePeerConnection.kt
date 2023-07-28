package com.example.sandverse.services.couchbase

import com.couchbase.lite.Message
import com.couchbase.lite.MessageEndpointConnection
import com.couchbase.lite.MessagingCloseCompletion
import com.couchbase.lite.MessagingCompletion
import com.couchbase.lite.ReplicatorConnection

@Suppress("unused")
class ActivePeerConnection : MessageEndpointConnection {
    private var replicatorConnection: ReplicatorConnection? = null

    fun disconnect() {
        replicatorConnection?.close(null)
        replicatorConnection = null
    }

    /* implementation of MessageEndpointConnection */
    override fun open(connection: ReplicatorConnection, completion: MessagingCompletion) {
        replicatorConnection = connection
        completion.complete(true, null)
    }

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