package com.example.sandverse.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sandverse.data.ColorModel
//import com.example.sandverse.DBManager
import com.example.sandverse.data.RoomModel
import com.example.sandverse.data.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import kotlin.random.Random

class MainVM: ViewModel(), KoinComponent{
        // -------- VARIABLES ---------- //
    private val replicationState: MutableLiveData<String> by lazy { MutableLiveData<String>("Not Started")}

    // COLOR
    private var _colorModel = MutableStateFlow(ColorModel(""))
    val colorModel: StateFlow<ColorModel> = _colorModel

    // USER
    private var _userModel = MutableStateFlow(UserModel("", ""))
    val userModel: StateFlow<UserModel> = _userModel

    // ROOM
    private var _roomModel = MutableStateFlow(RoomModel("", "", 0))
    val roomModel: StateFlow<RoomModel> = _roomModel


        // -------- Functions -------- //
    // LOGIN
    fun login(setUsername: (String) -> Unit, setPassword: (String) -> Unit) {
        val username = _userModel.value.username
        val password = _userModel.value.password

        setUsername(username)
        setPassword(password)
    }
    fun setUsername(username: String) {
        val currentData = _userModel.value
        _userModel.value = currentData.copy(username = username)
    }
    fun setPassword(password: String) {
        val currentData = _userModel.value
        _userModel.value = currentData.copy(password = password)
    }

    // ROOM
    fun setRoomName(roomName: String) {
        val currentData = _roomModel.value
        _roomModel.value = _roomModel.value.copy(roomname = roomName)
    }
    fun setRoomStatus(roomStatus: String) {
        val currentData = _roomModel.value
        _roomModel.value = _roomModel.value.copy(roomname = roomStatus)
    }

    fun setDeviceCount(deviceCount: Int) {
        val currentData = _roomModel.value
        _roomModel.value = currentData.copy(roomDevicesCount = deviceCount)
    }



    fun createRoom() {
        _roomModel.value = RoomModel("Nazwa", "Host", 1)
    }

    fun searchRooms() {
        _roomModel.value = RoomModel("Inny Pokój", "Gość", 3)
    }

    // COLOR
    fun changeColor() {
        _colorModel.value = _colorModel.value.copy(screenColor = generateRandomColor())
    }

    // Function to generate a random color represented as a string (HEX format)
    fun generateRandomColor(): String {
        // Generate random color components (0-255) for red, green, and blue
        val randRed = Random.nextInt(256)
        val randGreen = Random.nextInt(256)
        val randBlue = Random.nextInt(256)

        // Format RGB values into HEX format and return
        return String.format("#%02X%02X%02X", randRed, randGreen, randBlue)
    }
/*
    fun runIt(): MutableLiveData<String> {
        context.get()?.let { activity ->
            activity.viewModelScope.launch(Dispatchers.IO) {
                val mgr = DBManager.getInstance(activity)
                mgr.createDb("example")
                mgr.createCollection("example")
                val id = mgr.createDoc()
                mgr.retrieveDoc(id)
                mgr.updateDoc(id)
                mgr.queryDocs()
                mgr.replicate()
                    ?.onEach { change -> replicationState.postValue(change.status.activityLevel.toString()) }
                    ?.collect()
            }
        }
        return replicationState
    }
*/
}
