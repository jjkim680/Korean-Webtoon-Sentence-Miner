package com.example.koreanwebtoonsentenceminer

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VerificationScreen(
    viewModel: DictionaryViewModel,
    modifier: Modifier = Modifier
) {
    // Compose listens to the Flow and trigger a UI refresh when it changes
    val results by viewModel.searchResults.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        
        Button(onClick = { 
            Log.d("TracerBullet", "Attempting to insert '사과'...")
            viewModel.insertDummyCard() 
        }) {
            Text("Insert '사과'")
        }

        Text(
            text = "Found ${results.size} matches in Database:", 
            modifier = Modifier.padding(top = 16.dp)
        )

        // Display the results
        results.forEach { translation ->
            Text("- ${translation.koreanWord} (Hash: ${translation.idHash.take(6)}...)")
        }
    }
}