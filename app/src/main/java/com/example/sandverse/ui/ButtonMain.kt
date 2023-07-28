package com.example.sandverse.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.sandverse.R

@Composable
fun ButtonMain(
    onClick: () -> Unit,
    text: String
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.shiny_gold),
            contentColor = colorResource(id = R.color.cherryText)
        ),
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = text)
    }
}



