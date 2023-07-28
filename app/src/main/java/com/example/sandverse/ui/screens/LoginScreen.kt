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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.sandverse.viewmodels.MainVM
import com.example.sandverse.R
import com.example.sandverse.ui.ButtonMain
import org.koin.androidx.compose.koinViewModel


@Composable
fun LoginScreen(
    nav: NavHostController,
    mainVM: MainVM = koinViewModel(),
) {
    val userData by mainVM.userModel.collectAsState()

    val setUsername: (String) -> Unit = { username -> mainVM.setUsername(username) }
    val setPassword: (String) -> Unit = { password -> mainVM.setPassword(password) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.anthracite))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = userData.username,
            onValueChange = setUsername,
            label = { Text(stringResource(R.string.username)) },
            modifier = Modifier
                .padding(16.dp)
        )
        TextField(
            value = userData.password,
            onValueChange = setPassword,
            label = { Text(stringResource(R.string.password)) },
            modifier = Modifier
                .padding(16.dp),
            visualTransformation = PasswordVisualTransformationa()
        )

        ButtonMain(
            onClick = {
                mainVM.login(setUsername, setPassword)
                nav.navigate("wifiDirect")
            },
            text = stringResource(R.string.login) 
        )
    }
}

class PasswordVisualTransformationa : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val transformedText = text.text.replace(Regex("."), "\u2022")
        return TransformedText(AnnotatedString(transformedText), OffsetMapping.Identity)
    }
}