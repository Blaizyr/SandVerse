package com.example.sandverse.services

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class NavigatorHolder {
    var navController: NavHostController? = null
}
@Composable
fun NavHolder(navHolder: NavigatorHolder) {
    val navController = rememberNavController()
    navHolder.navController = navController
}
