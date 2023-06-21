package com.example.sandverse.ui


import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun CloudListClickable(
    modalVisible: Boolean,
    onClose: () -> Unit,
    onItemClickIndex: ((Int) -> Unit)? = null,
    content: List<String>? = null
) {
    val context = LocalContext.current

    val lazyColumnContent = @Composable {
        var count by remember { mutableIntStateOf(0) }

        LazyColumn {
            itemsIndexed(content ?: listOf("Empty")){ index, item ->
                Text(
                    text = item,
                    modifier = Modifier.clickable {
                        count++
                        Toast.makeText(context, "Klik $count", Toast.LENGTH_SHORT).show()
                        onItemClickIndex?.invoke(index)
                    }
                )
            }
        }
    }

    CloudList(
        modalVisible = modalVisible,
        onClose = onClose,
        contentPipe = lazyColumnContent
    )
}

