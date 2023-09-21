package com.example.sandverse.ui


import android.util.Log.*
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun <T> CloudListClickable(
    modalVisible: Boolean,
    onClose: () -> Unit,
//    onItemClickIndex: ((Int) -> Unit)? = null,
//    content: List<String>? = null
    onItemClick: ((T) -> Unit),
    content: List<T>? = null,
    itemContent: (T) -> String
) {
    val context = LocalContext.current

    val isContentEmpty = content?.isEmpty() ?: true

    if (isContentEmpty) {
        d("CloudListClickable1", "Content is empty!!!")
    } else {
        d("CloudListClickable1", "Content is not empty!!! ${content!!.size}, ${content[content.size-1]}")
    }

    val lazyColumnContent = @Composable {
        var count by remember { mutableStateOf(0) }

        LazyColumn(
            contentPadding = PaddingValues(vertical = 10.dp)
        ) {
            itemsIndexed(content ?: listOf("Empty")) { _ , item ->
                d("CloudListClickable2", "$item. ${content?.get(0)}. ${content?.size}")

                // TODO: Unchecked cast: Any? to T !!!!!!!!!!!!!!!!!!
                val displayText = itemContent(item as T)
                d("CloudListClickable3", "$displayText, $item")

                Text(
                    text = displayText.toString(),
                    modifier = Modifier.clickable {
                        onItemClick.invoke(item)
                        count++
                        Toast.makeText(context, "Click $count", Toast.LENGTH_SHORT).show()
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

