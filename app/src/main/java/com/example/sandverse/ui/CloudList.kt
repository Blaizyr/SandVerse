package com.example.sandverse.ui


import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember

@Composable
fun CloudList(
    modalVisible: Boolean,
    onClose: () -> Unit,
    content: List<Any>? = null
) {
    val placeholderContent = remember {
        mutableStateListOf(
            "Loading...",
            "Loading...",
            "Loading...",
            "Loading...",
            "Loading...",
            "Loading...",
            "Loading...",
            "Loading...",
            "Loading...",
            "Loading..."
        )
    }
    val listContent = content ?: placeholderContent
    val lazyColumnContent = @Composable {
        LazyColumn {
            items(listContent) { item ->
                Text(text = item.toString())
            }
        }
    }
    CloudWindow(modalVisible = modalVisible, onClose = onClose, content = lazyColumnContent)
}
