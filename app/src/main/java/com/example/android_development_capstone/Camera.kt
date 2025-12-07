package com.example.android_development_capstone

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import android.util.Base64
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val OPENAI_API_KEY = "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"

@Composable
fun Camera(nav: NavHostController, modifier: Modifier = Modifier) {
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            // After photo is captured, send to OpenAI (async) then navigate
            coroutineScope.launch {
                isLoading = true
                errorMessage = null
                val response = try {
                    identifyPlant(bitmap)
                } catch (e: Exception) {
                    errorMessage = "Failed to identify plant: ${e.message}"
                    null
                } finally {
                    isLoading = false
                }

                response?.let { result ->
                    nav.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("plantResponse", result)
                    nav.navigate("cameraresponse")
                }
            }
        } else {
            errorMessage = "No photo captured."
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.tertiary,
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavBar(nav) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "Identify a Plant or Tree!",
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(fontSize = 48.sp),
                    color = Color(0xFF2E7D32),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 26.dp),

                )

                PlantPicture()

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = { takePicture.launch(null) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32),
                        contentColor = Color.White
                    )
                ) {
                    Text(if (isLoading) "Identifying..." else "Take plant photo")
                }

                errorMessage?.let { err ->
                    Text(
                        text = err,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }
    }
}


private suspend fun identifyPlant(bitmap: Bitmap): String = withContext(Dispatchers.IO) {
    // Encode bitmap to Base64 (PNG)
    val pngStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, pngStream)
    val imageBytes = pngStream.toByteArray()
    val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

    val payload = """
        {
          "model": "gpt-4o-mini",
          "messages": [
            {
              "role": "system",
              "content": "You are a botanist. Identify plants from images. Respond with common name, scientific name, and a one-line care tip."
            },
            {
              "role": "user",
              "content": [
                { "type": "text", "text": "Identify this plant from the photo. If unsure, give the closest guess." },
                { "type": "image_url", "image_url": { "url": "data:image/png;base64,$base64Image" } }
              ]
            }
          ],
          "max_tokens": 200
        }
    """.trimIndent()

    val url = URL("https://api.openai.com/v1/chat/completions")
    val conn = (url.openConnection() as HttpURLConnection).apply {
        requestMethod = "POST"
        setRequestProperty("Authorization", "Bearer $OPENAI_API_KEY")
        setRequestProperty("Content-Type", "application/json")
        doOutput = true
        outputStream.use { os ->
            os.write(payload.toByteArray(Charsets.UTF_8))
        }
    }

    val responseCode = conn.responseCode
    val stream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
    val responseText = stream.bufferedReader().use(BufferedReader::readText)

    if (responseCode !in 200..299) {
        throw IllegalStateException("OpenAI request failed ($responseCode): $responseText")
    }

    // Parse minimal JSON for the assistant's message content
    val content = try {
        val json = JSONObject(responseText)
        json.getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content")
    } catch (e: Exception) {
        throw IllegalStateException("Failed to parse OpenAI response: ${e.message}")
    }

    return@withContext content
}