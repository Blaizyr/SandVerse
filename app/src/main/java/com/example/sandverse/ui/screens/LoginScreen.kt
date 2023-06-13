package com.example.sandverse.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.sandverse.viewmodels.MainViewModel
import com.example.sandverse.R
import com.example.sandverse.ui.ButtonMain


@Composable
fun LoginScreen(navHostController: NavHostController, viewModel: MainViewModel = viewModel()) {
    val userData by viewModel.userData.collectAsState()

    val setUsername: (String) -> Unit = { username -> viewModel.setUsername(username) }
    val setPassword: (String) -> Unit = { password -> viewModel.setPassword(password) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.antracyt))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = userData.username,
            onValueChange = setUsername,
            label = { Text("Username") },
            modifier = Modifier
                .padding(16.dp)
        )
        TextField(
            value = userData.password,
            onValueChange = setPassword,
            label = { Text("Password") },
            modifier = Modifier
                .padding(16.dp),
            visualTransformation = PasswordVisualTransformationa()
        )

        ButtonMain(
            onClick = {
                viewModel.login(setUsername, setPassword)
                navHostController.navigate("wifiDirect")
            },
            text = "Login"
        )
    }
}

class PasswordVisualTransformationa : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val transformedText = text.text.replace(Regex("."), "\u2022")
        return TransformedText(AnnotatedString(transformedText), OffsetMapping.Identity)
    }
}