package com.example.shortlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.shortlist.ui.screens.ProjectsScreen
import com.example.shortlist.ui.screens.SplashScreen
import com.example.shortlist.ui.theme.ShortListTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.dark(
                android.graphics.Color.TRANSPARENT
            )
        )

        setContent {
            ShortListTheme {
                var showSplash by rememberSaveable {
                    mutableStateOf(true)
                }

                if (showSplash) {
                    SplashScreen(
                        onSplashFinished = {
                            showSplash = false
                        }
                    )
                } else {
                    ProjectsScreen(
                        onProjectClick = { projectId ->
                            // Connect the selected project's shot list next.
                        }
                    )
                }
            }
        }
    }
}