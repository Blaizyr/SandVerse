package com.example.sandverse.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.sandverse.R
import com.example.sandverse.data.RoomData
import com.example.sandverse.ui.ButtonMain
import com.example.sandverse.viewmodels.MainViewModel

@Composable
fun RoomScreen(navHostController: NavHostController, viewModel: MainViewModel = viewModel()) {
    val roomData: RoomData by viewModel.roomData.collectAsState()
    var isConnected by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.antracyt))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Nazwa pokoju: ${roomData.roomname}", color = colorResource(id = R.color.wiśniowy))

        if (isConnected) {
            Text(text = "Status w sieci: Host${roomData.roomstatus}", color = colorResource(id = R.color.wiśniowy))
        } else {
            Text(text = "Brak połączenia! ${roomData.roomstatus}", color = colorResource(id = R.color.wiśniowy))
        }

        Text(text = "Ilość urządzeń: ${roomData.roomDevicesCount}", color = colorResource(id = R.color.wiśniowy))

        ButtonMain(
            onClick = { isConnected =! isConnected },
            text = "on/off"
        )
    }
}
