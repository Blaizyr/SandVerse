package com.example.sandverse.ui.screens

import android.util.Log.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.couchbase.lite.Database
import com.example.sandverse.DBManager
import com.example.sandverse.R
import com.example.sandverse.data.PeersUIState
import com.example.sandverse.viewmodels.WifiVM
import com.example.sandverse.ui.ButtonMain
import com.example.sandverse.ui.CloudListClickable
import com.example.sandverse.ui.CloudWindow
import com.example.sandverse.viewmodels.MainVM
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
    var dbName by remember { mutableStateOf("color") }
    var collName by remember { mutableStateOf("color") }


    val updatesCount by wifiVM.updatesCount.collectAsState()
    d("WiFiDirectScreen", "Updates count: $updatesCount")

    // Create a mutableState based on peersData
    val peersDataState = wifiVM.peersData.collectAsState()
    val peersData = peersDataState.value.list
    // Log the collected peers data
    d("WiFiDirectScreen", "Collected peers data: $peersData")
    println("Peers data = ${peersDataState.value.list}")
    println("Peers data = $peersData")



    val context = LocalContext.current



    fun navTo(screen: String) {
        CoroutineScope(Dispatchers.Main).launch { nav.navigate(screen) }
    }

    fun createRoom() {
        CoroutineScope(Dispatchers.IO).launch {
            database = DBManager.dbManager.createDb("db-$dbName")
            DBManager.dbManager.createCollection(collName)
            v("WifiDirectScreen", "Set DB name: $dbName")
            v("WifiDirectScreen", "Set collection name: $collName")


            delay(350)

            syncDataVM.startToListen(database)
            delay(500)

            wifiVM.registerServiceListener(
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
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .background(colorResource(id = R.color.light_gold))
                .absoluteOffset(y = 0.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Number of devices updates: $updatesCount",
                color = colorResource(id = R.color.cherryText),
                modifier = Modifier
                    .align(Alignment.CenterStart)
            )
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
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

    }


    CloudListClickable(
        modalVisible = isVisibleSearcher,
        onClose = { isVisibleSearcher = false },
        content = peersData,
        itemContent = { item: PeersUIState ->
            item.deviceName.plus(" " + item.deviceAddress)
        },
        onItemClick = { item: PeersUIState ->
            val deviceAddress = item.onClickSelect()
            wifiVM.connect(
                context,
                deviceAddress,
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        WiFiDirectScreen(
            nav = NavHostController(LocalContext.current)
        )
    }
}
