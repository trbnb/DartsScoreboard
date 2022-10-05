package de.trbnb.darts.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import de.trbnb.darts.ui.navigation.NavHost
import de.trbnb.darts.ui.theme.DartsTheme

@AndroidEntryPoint
class Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DartsTheme {
                NavHost()
            }
        }
    }
}