package com.example.sandverse.ui


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp

@Composable
fun CloudList(
    modalVisible: Boolean,
    onClose: () -> Unit,
    contentPipe: @Composable () -> Unit = { }, // Use this if you only want to pass content from a higher-level composable to CloudWindow.
    content: List<Any>? = null
) {
    val placeholderContent = remember {
        mutableStateListOf(
            "Loading...",
            "Loading...",
            "Loading..."
        )
    }
    val lazyColumnContent = @Composable {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 10.dp)
        ) {
            items(content ?: placeholderContent) { item ->
                Text(text = item.toString())
            }
        }
    }

    val determinedContent = when (content) {
        null -> contentPipe
        else -> lazyColumnContent
    }

    CloudWindow(
        modalVisible = modalVisible,
        onClose = onClose,
        content = determinedContent
    )

}
