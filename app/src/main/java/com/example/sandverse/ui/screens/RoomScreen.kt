package com.example.sandverse.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.sandverse.R
import com.example.sandverse.viewmodels.WifiVM
import com.example.sandverse.data.RoomModel
import com.example.sandverse.services.wifip2p.DeviceListInfoHolder
import com.example.sandverse.ui.ButtonMain
import com.example.sandverse.viewmodels.MainVM
import com.example.sandverse.viewmodels.SyncDataVM
import org.koin.androidx.compose.koinViewModel


@Composable
fun RoomScreen(
    nav: NavHostController,
    wifiVM: WifiVM = koinViewModel(),
    mainVM: MainVM = koinViewModel(),
    syncDataVM: SyncDataVM = koinViewModel(),
) {
    ////---- Variables ----////
    val roomModel: RoomModel by mainVM.roomModel.collectAsState()
    val userData by mainVM.userModel.collectAsState()

    var isConnected by remember { mutableStateOf(false) }

    val context = LocalContext.current

    ////---- Functions Declaration ----////

    ////---- Content Composition ----////

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.anthracite))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Room Name: ${roomModel.roomname}", color = colorResource(id = R.color.cherry))
        Text(
            text = "MAC : ${DeviceListInfoHolder.actualConnectionAddress}",
            color = colorResource(id = R.color.cherry)
        )
        Text(
            text = "IP : ${DeviceListInfoHolder.actualConnectionIP}",
            color = colorResource(id = R.color.cherry)
        )
        /*
                if (isConnected) {
                    Text(
                        text = "Network Status: Host${roomData.room status}",
                        color = colorResource(id = R.color.)
                    )
                } else {
                    Text(
                        text = "Not Connected! ${roomData.room status}",
                        color = colorResource(id = R.color.)
                    )
                }

                Text(
                    text = "Number of Devices connected: ${roomData.roomDevicesCount}",
                    color = colorResource(id = R.color.)
                )

                ButtonMain(
                    onClick = {
                        isConnected = !isConnected
                    },
                    text = "on/off"
                )*/
        ButtonMain(
            onClick = {
                syncDataVM.startToListen()
                wifiVM.registerListenerService(
                    context,
                    port = syncDataVM.getListenerPort()
                )
            },
            text = "Start to listen"
        )
        ButtonMain(
            onClick = {
                syncDataVM.startReplication()
            },
            text = "Start to replicate"
        )

        ButtonMain(
            onClick = {
                wifiVM.cancelConnect()
            },
            text = "Disconnect"
        )

        TextField(value = userData.password, onValueChange = {})
    }
}

