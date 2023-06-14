package com.example.sandverse.viewmodels

import androidx.lifecycle.ViewModel
import com.example.sandverse.model.PeersModel
import com.example.sandverse.model.RoomData
import com.example.sandverse.model.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {
    private val _userData = MutableStateFlow(UserData("", ""))
    val userData: StateFlow<UserData> = _userData

    private val _roomData = MutableStateFlow(RoomData("", "", 0))
    val roomData: StateFlow<RoomData> = _roomData

    private val _peersData = MutableStateFlow<List<PeersModel>>(emptyList())
    val peersData: StateFlow<List<PeersModel>> = _peersData

    fun login(setUsername: (String) -> Unit, setPassword: (String) -> Unit) {
        val username = _userData.value.username
        val password = _userData.value.password

        setUsername(username)
        setPassword(password)
    }

    fun setUsername(username: String) {
        val currentData = _userData.value
        _userData.value = currentData.copy(username = username)
    }

    fun setPassword(password: String) {
        val currentData = _userData.value
        _userData.value = currentData.copy(password = password)
    }

    fun setRoomName(roomName: String) {
        val currentData = _roomData.value
        _roomData.value = _roomData.value.copy(roomName)
    }

    fun setRoomStatus(roomStatus: String) {
        val currentData = _roomData.value
        _roomData.value = _roomData.value.copy(roomStatus)
    }

    fun setDeviceCount(deviceCount: Int) {
        val currentData = _roomData.value
        _roomData.value = currentData.copy(roomDevicesCount = deviceCount)
    }



    fun createRoom() {
        _roomData.value = RoomData("Nazwa", "Host", 1)
    }

    fun searchRooms() {
        _roomData.value = RoomData("Inny Pokój", "Gość", 3)
    }

}
