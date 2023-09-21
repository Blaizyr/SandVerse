package com.example.sandverse.ui.screens
/*

import androidx.navigation.NavHostController
import com.example.sandverse.data.UserModel
import com.example.sandverse.viewmodels.MainVM
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.DisplayName
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.MockedConstruction
import org.mockito.MockedConstruction.Context
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()


    @Test
    @DisplayName("Should call the login method in the MainVM when the login button is clicked")
    fun loginScreenWhenLoginButtonIsClickedThenCallMainVMLogin() {
        val navController = mock(NavHostController::class.java)
        val mainVM = mock(MainVM::class.java)
        val username = "testUser"
        val password = "testPassword"

        composeTestRule.setContent {
            LoginScreen(navController, mainVM)
        }

        composeTestRule.onNodeWithText("Login").performClick()

        verify(mainVM).login(eq(username), eq(password))
        verify(navController).navigate("wifiDirect")
    }

    @Test
    @DisplayName("Should update the password when the password TextField value changes")
    fun loginScreenWhenPasswordTextFieldValueChanges() {
        val navController = mock(NavHostController::class.java)
        val mainVM = mock(MainVM::class.java)
        val userModel = mock(StateFlow::class.java) as StateFlow<UserModel>
        val context = mock(Context::class.java)

        val loginScreen = LoginScreen(navController, mainVM)

        `when`(mainVM.userModel).thenReturn(userModel)

        composeTestRule.setContent {
            loginScreen
        }

        composeTestRule.onNodeWithText("Password").performTextInput("newPassword")

        verify(mainVM).setPassword("newPassword")
    }

    @Test
    @DisplayName("Should update the username when the username TextField value changes")
    fun loginScreenWhenUsernameTextFieldValueChanges() {
        val navController = mock(NavHostController::class.java)
        val mainVM = mock(MainVM::class.java)
        val userModel = mock(StateFlow::class.java) as StateFlow<UserModel>
        val context = mock(Context::class.java)

        val loginScreen = LoginScreen(navController, mainVM)

        `when`(mainVM.userModel).thenReturn(userModel)

        composeTestRule.setContent {
            loginScreen
        }

        composeTestRule.onNodeWithText("Username").performTextInput("john.doe")

        verify(mainVM).setUsername("john.doe")
    }

    @Test
    @DisplayName("Should navigate to wifiDirect when the login button is clicked")
    fun loginScreenWhenLoginButtonIsClicked() {
        val navController = mock(NavHostController::class.java)
        val mainVM = mock(MainVM::class.java)
        val userModel = mock(StateFlow::class.java) as StateFlow<UserModel>
        val context = mock(Context::class.java)

        val loginScreen = LoginScreen(navController, mainVM)

        `when`(mainVM.userModel).thenReturn(userModel)
        `when`(context.getString(R.string.username)).thenReturn("Username")
        `when`(context.getString(R.string.password)).thenReturn("Password")
        `when`(context.getString(R.string.login)).thenReturn("Login")

        composeTestRule.setContent {
            loginScreen
        }

        composeTestRule.onNodeWithText("Login").performClick()

        verify(mainVM).login(any(), any())
        verify(navController).navigate("wifiDirect")
    }

}

private fun MockedConstruction.Context.getString(username: Any): Any {
    return mock(String::class.java).toString()

}
*/
