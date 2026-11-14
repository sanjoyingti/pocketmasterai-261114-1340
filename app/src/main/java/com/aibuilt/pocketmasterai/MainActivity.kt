package com.aibuilt.pocketmasterai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.aibuilt.pocketmasterai.theme.PedalboardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PedalboardTheme(darkTheme = isSystemInDarkTheme()) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    PocketMasterApp()
                }
            }
        }
    }
}

@Composable
fun PocketMasterApp() {
    MaterialTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            // Root app scaffold; screens live in the navigation graph.
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PedalboardTheme {
        PocketMasterApp()
    }
}
