package com.example.sandverse.ui.screens

import android.net.wifi.p2p.WifiP2pManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.sandverse.R
import com.example.sandverse.ui.ButtonMain
import com.example.sandverse.ui.CloudList
import com.example.sandverse.viewmodels.MainViewModel


@Composable
fun WiFiDirectScreen(
    navHostController: NavHostController,
    viewModel: MainViewModel = viewModel(),
    wifiP2pManager: WifiP2pManager,
    channel : WifiP2pManager.Channel?) {

    val context = LocalContext.current
    var isDiscovering by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.antracyt))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ButtonMain(
            onClick = {
                viewModel.createRoom()
                navHostController.navigate("room")
            },
            text = "Utwórz pokój"
        )

        ButtonMain(
            onClick = {
                viewModel.searchRooms()
                isVisible = true
                isDiscovering = true
                wifiP2pManager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        Toast.makeText(context, "Sukces!", Toast.LENGTH_LONG).show()
                    }

                    override fun onFailure(p0: Int) {
                        Toast.makeText(context, "Błąd", Toast.LENGTH_LONG).show()
                    }

                })
                //navHostController.navigate("room")
            },
            text = "Szukaj pokoi"
        )
    }
    CloudList(
        modalVisible = isVisible,
        onClose = { isVisible = false }
    )
}
