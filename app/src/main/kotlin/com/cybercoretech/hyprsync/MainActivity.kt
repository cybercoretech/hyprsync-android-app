// MainActivity.kt – entry point with Compose navigation
package com.cybercoretech.hyprsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
// Import the UI screens
import com.cybercoretech.hyprsync.ui.ContactListScreen
import com.cybercoretech.hyprsync.ui.QRShareScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { HyprSyncApp() }
    }
}

@Composable
fun HyprSyncApp() {
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("HyprSync") })
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "contact_list",
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            composable("contact_list") { ContactListScreen(onContactClick = { id ->
                navController.navigate("contact_detail/" + id)
            }) }
            // Placeholder for detail screen – future implementation
            composable("contact_detail/{contactId}") { backStackEntry ->
                // For now just show QR share screen for demo
                val id = backStackEntry.arguments?.getString("contactId") ?: ""
                QRShareScreen(contactId = id)
            }
        }
    }
}

package com.cybercoretech.hyprsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { HyprSyncApp() }
    }
}

@Composable
fun HyprSyncApp() {
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("HyprSync") })
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "contact_list",
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            composable("contact_list") { ContactListScreen(onContactClick = { id ->
                navController.navigate("contact_detail/" + id)
            }) }
            // Placeholder for detail screen – future implementation
            composable("contact_detail/{contactId}") { backStackEntry ->
                // For now just show QR share screen for demo
                val id = backStackEntry.arguments?.getString("contactId") ?: ""
                QRShareScreen(contactId = id)
            }
        }
    }
}
