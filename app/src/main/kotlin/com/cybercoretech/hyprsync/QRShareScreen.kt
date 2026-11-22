// QRShareScreen.kt – shows a QR code for a contact
package com.cybercoretech.hyprsync

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Simple QR-share screen.
 * It receives a `contactId` (string), loads the Contact from the repository,
 * encodes the Contact as JSON, creates a QR code bitmap with ZXing, and shows it.
 * A "Save / Share" button is provided – implementation left as a stub.
 */
@Composable
fun QRShareScreen(contactId: String) {
    val contact = remember(contactId) { ContactRepository.getContactById(contactId.toLongOrNull() ?: 0L) }

    // Encode contact to JSON – using kotlinx-serialization.
    val json = remember(contact) { Json.encodeToString(contact) }

    // Generate QR bitmap once.
    val qrBitmap = remember(json) { generateQrBitmap(json) }

    Column {
        Text(text = "QR Share for ${contact.name}")
        Spacer(modifier = Modifier.height(16.dp))
        if (qrBitmap != null) {
            Image(
                bitmap = qrBitmap.asImageBitmap(),
                contentDescription = "QR code for contact",
                modifier = Modifier.size(240.dp),
            )
        } else {
            Text("Failed to generate QR code")
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            // TODO: implement Android share Intent to export the bitmap or JSON file.
        }) {
            Text("Save / Share")
        }
    }
}

/** Generates a QR code bitmap from the given text. Returns null on failure. */
private fun generateQrBitmap(
    text: String,
    size: Int = 512,
): Bitmap? =
    try {
        val bitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels =
            IntArray(width * height) { i ->
                if (bitMatrix.get(i % width, i / width)) android.graphics.Color.BLACK else android.graphics.Color.WHITE
            }
        Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    } catch (e: WriterException) {
        Log.w("QRShareScreen", "QR encoding failed", e)
        null
    } catch (e: IllegalArgumentException) {
        Log.w("QRShareScreen", "QR encoding failed", e)
        null
    }
