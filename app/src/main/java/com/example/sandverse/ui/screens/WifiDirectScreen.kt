package com.example.sandverse.ui.screens

//import android.widget.Toast
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.couchbase.lite.Database
import com.example.sandverse.DBManager
import com.example.sandverse.services.wifip2p.DeviceListInfoHolder
import com.example.sandverse.R
import com.example.sandverse.viewmodels.WifiVM
import com.example.sandverse.services.wifip2p.WifiP2pConnectionHandler
import com.example.sandverse.ui.ButtonMain
import com.example.sandverse.ui.CloudListClickable
import com.example.sandverse.ui.CloudWindow
import com.example.sandverse.viewmodels.SyncDataVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun WiFiDirectScreen(
    nav: NavController,
    wifiVM: WifiVM = koinViewModel(),
    syncDataVM: SyncDataVM = koinViewModel(),
) {
    var isVisibleSearcher by remember { mutableStateOf(false) }
    var isVisibleCreator by remember { mutableStateOf(false) }
    var database: Database?
    var dbName by remember { mutableStateOf("") }
    var collName by remember { mutableStateOf("") }

    val context = LocalContext.current

    // TODO: Handler do zależności koina
    val connectionHandler = WifiP2pConnectionHandler()
    wifiVM.setConnectionListener(connectionHandler, nav)

    try {
        wifiVM.initializeWifiP2p(context)// Wifi p2p manager -> property of wifiViewModel
        Log.d("WifiDirectScreen","WifiP2pManager is initialized!! (as a property of wifi ViewModel)")
        wifiVM.registerReceiver(context)
    } catch (e: ExceptionInInitializerError) {
        Log.e("WifiDirectScreen", "WifiP2pManager cannot be initialized. ${e.message}", e)
    }

    fun navTo(screen: String) {
        CoroutineScope(Dispatchers.Main).launch { nav.navigate(screen) }
    }

    fun createRoom() {
        CoroutineScope(Dispatchers.IO).launch {
            database = DBManager.dbManager.createDb("db-$dbName")
            DBManager.dbManager.createCollection(collName)
            Log.v("WifiDirectScreen", "Set DB name: $dbName")
            Log.v("WifiDirectScreen", "Set collection name: $collName")


            delay(350)

            syncDataVM.startToListen(database)
            delay(500)

            wifiVM.registerListenerService(
                context,
                port = syncDataVM.getListenerPort()
            )
            delay(1000)

            navTo(dbName)
            dbName = ""
            collName = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.anthracite))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ButtonMain(
            onClick = {
                isVisibleCreator = true
            },
            text = stringResource(R.string.create_room)
        )

        ButtonMain(
            onClick = {
                //viewModel.searchRooms()
                isVisibleSearcher = true
                wifiVM.discoverPeers(context)
            },
            text = stringResource(R.string.search_room)
        )
    }


    CloudListClickable(
        modalVisible = isVisibleSearcher,
        onClose = { isVisibleSearcher = false },
        content = DeviceListInfoHolder.devices,
        onItemClickIndex = { index ->
            wifiVM.connectWith(
                context,
                wifiVM.selectDevice(index),
            )
        }
    )
    CloudWindow(modalVisible = isVisibleCreator, onClose = {
        isVisibleCreator = false
    }) {
        Text(text = "Room name:")
        TextField(value = dbName, onValueChange = { dbName = it })
        Text(text = "Collection name:")
        TextField(value = collName, onValueChange = { collName = it } )
        ButtonMain(
            onClick = {
                createRoom()
            },
            text = "Create"
        )
    }
}

