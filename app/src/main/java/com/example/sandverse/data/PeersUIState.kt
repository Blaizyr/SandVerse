package com.example.sandverse.data

data class PeersUIState(
    val deviceAddress: String = "",
    val deviceName: String = "",
    val primaryDeviceType: String = "",
    val secondaryDeviceType: String? = null,
    val status: Int = 1,
) {
    fun onClickSelect(): String {
        return deviceAddress
    }
}
