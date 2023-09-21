package com.example.sandverse.ui.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.sandverse.data.ColorModel
import com.example.sandverse.viewmodels.MainVM
import com.example.sandverse.viewmodels.SyncDataVM
import com.example.sandverse.viewmodels.WifiVM
import org.koin.androidx.compose.koinViewModel

@Composable
@Suppress("UNUSED_PARAMETER")
fun ColorSyncScreen(
    mainVM: MainVM = koinViewModel(),
    wifiVM: WifiVM = koinViewModel(),
    syncDataVM: SyncDataVM = koinViewModel(),
) {
    val colorModel: ColorModel by mainVM.colorModel.collectAsState()

    // Function to change the color
    fun changeColor() {
        mainVM.changeColor()
    }

    // Background color based on the current color model
    val backgroundColor = if (colorModel.screenColor.isNotEmpty()) {
        Color(android.graphics.Color.parseColor(colorModel.screenColor))
    } else {
        // Default (white) Color, if colorModel.screenColor is empty
        Color.White
    }

    // Composable to display the screen with the current color
    Box(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize()
            .clickable { changeColor() }    // Add an onClick handler to change the color when the screen is clicked

    ) {
        // UI content here

        // For example, display a text to indicate the current color
        Text(
            text = "Current Color: ${colorModel.screenColor}",
            color = Color.Gray,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
