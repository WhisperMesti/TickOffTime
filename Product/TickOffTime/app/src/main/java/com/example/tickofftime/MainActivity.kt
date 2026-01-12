package com.example.tickofftime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.tickofftime.ui.theme.TickOffTimeTheme

//center of the whole app
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TickOffTimeTheme {
                    TickOffTimeApp()
                }
            }
        }
    }

//main call
@Composable
fun TickOffTimeApp() {
    NavigationHost(navController = rememberNavController())
}

@Preview
@Composable
fun LightThemePreview() {
    TickOffTimeTheme(darkTheme = false) {
        TickOffTimeApp()
    }
}

@Preview
@Composable
fun DarkThemePreview() {
    TickOffTimeTheme(darkTheme = true) {
        TickOffTimeApp()
    }
}