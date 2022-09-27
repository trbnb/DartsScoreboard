package de.trbnb.darts.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.internal.composableLambdaN
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.fragment
import de.trbnb.darts.ui.main.MainScreen
import de.trbnb.darts.ui.main.newplayer.NewPlayerDialogTemplate
import de.trbnb.darts.ui.match.MatchScreen

sealed interface Screen {
    val route: String

    object Main : Screen {
        override val route: String = "main"
    }

    object Match : Screen {
        override val route: String = "match"
    }
}

val LocalNavController = staticCompositionLocalOf<NavController> {
    throw IllegalStateException("NavController not set yet")
}

@Composable
fun NavHost() {
    val controller = rememberNavController()
    CompositionLocalProvider(
        LocalNavController provides controller
    ) {
        NavHost(controller, Screen.Main.route) {
            composable(Screen.Main.route) {
                MainScreen()
            }

            composable(Screen.Match.route) {
                MatchScreen()
            }
        }
    }
}