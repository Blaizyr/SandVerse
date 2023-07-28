package com.example.sandverse

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.couchbase.lite.BasicAuthenticator
import com.couchbase.lite.Collection
import com.couchbase.lite.CollectionConfiguration
import com.couchbase.lite.CouchbaseLite
import com.couchbase.lite.DataSource
import com.couchbase.lite.Database
import com.couchbase.lite.DatabaseConfiguration
import com.couchbase.lite.Expression
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.Query
import com.couchbase.lite.QueryBuilder
import com.couchbase.lite.Replicator
import com.couchbase.lite.ReplicatorChange
import com.couchbase.lite.ReplicatorConfigurationFactory
import com.couchbase.lite.ReplicatorType
import com.couchbase.lite.SelectResult
import com.couchbase.lite.URLEndpoint
import com.couchbase.lite.newConfig
import com.couchbase.lite.replicatorChangesFlow
import java.net.URI
import com.couchbase.lite.DatabaseConfigurationFactory
import org.koin.core.component.KoinComponent
import kotlinx.coroutines.flow.Flow as coFlow

//import com.couchbase.lite.Log

class DBManager : KoinComponent{
    private var database: Database? = null
    private var collection: Collection? = null
    private var replicator: Replicator? = null


    // One-off initialization
    private fun init(context: Context) {
        CouchbaseLite.init(context)
        Log.i("DBManager", "CBL Initialized")
    }


    // Create a database
    fun createDb(
        dbName: String,
        cfg: DatabaseConfiguration? = null,
    ): Database? {
        database = Database(
            dbName,
            cfg.newConfig()
            ) // TODO: Configuration --- a path --- crypto key;
        Log.i("DBManager", "Database created: $dbName. Path: commonPath")
        return database
    }


    // Create a new named collection (like a SQL table)
    // in the database's default scope.
    fun createCollection(collName: String): Collection {
        collection = database!!.createCollection(collName)
        Log.i("DBManager", "Collection $collName created: $collection")
        return collection as Collection
    }


    // Create a new document (i.e. a record)
    // and save it in a collection in the database.
    fun createDoc(): String {
        val mutableDocument = MutableDocument()
            .setFloat("version", 2.0f)
            .setString("language", "Java")
        collection?.save(mutableDocument)
        Log.i(TAG, "Mutable Document (ID: ${mutableDocument.id}) created")
        return mutableDocument.id
    }


    // Retrieve immutable document and log the database generated
    // document ID and some document properties
    fun retrieveDoc(docId: String) {
        collection?.getDocument(docId)
            ?.let {
                Log.i(TAG, "Document ID :: $docId")
                Log.i(TAG, "Learning :: ${it.getString("language")}")
            }
            ?: Log.i(TAG, "No such document :: $docId")
    }


    // Retrieve immutable document and update `language` property
    // document ID and some document properties
    fun updateDoc(docId: String) {
        collection?.getDocument(docId)?.let {
            collection?.save(
                it.toMutable().setString("language", "Kotlin")
            )
        }
    }


    // Create a query to fetch documents with language == Kotlin.
    fun queryDocs() {
        val coll = collection ?: return
        val query: Query = QueryBuilder.select(SelectResult.all())
            .from(DataSource.collection(coll))
            .where(Expression.property("language").equalTo(Expression.string("Kotlin")))
        query.execute().use { rs ->
            Log.i(TAG, "Number of rows :: ${rs.allResults().size}")
        }
    }


    //@Suppress("Since15")
    fun replicate(): coFlow<ReplicatorChange>? {
        val coll = collection ?: return null

        val collConfig = CollectionConfiguration()
            .setPullFilter { doc, _ -> "Java" == doc.getString("language") }

        val repl = Replicator(
            ReplicatorConfigurationFactory.newConfig(
                target = URLEndpoint(URI("ws://localhost:4984/getting-started-db")),
                collections = mapOf(setOf(coll) to collConfig),
                type = ReplicatorType.PUSH_AND_PULL,
                authenticator = BasicAuthenticator("sync-gateway", "password".toCharArray())
            )
        )

        // Listen to replicator change events.
        val changes = repl.replicatorChangesFlow()

        // Start replication.
        repl.start()
        replicator = repl

        return changes
    }
    companion object ExampleOfDBManager {
        // Creating DBManager instance --- creating db instance
        val dbManager = DBManager()
        val userDB = dbManager.createDb("userDB", DatabaseConfigurationFactory.newConfig())
        val defaultCollection = dbManager.createCollection("userCollection")
    }
}
