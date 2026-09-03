package com.example.clip2pdf

import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ShareToPdfActivity : Activity() {
    private var sharedText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedText = savedInstanceState?.getString(KEY_SHARED_TEXT) ?: readSharedText(intent)

        val text = sharedText?.trimEnd()
        if (text.isNullOrBlank()) {
            Toast.makeText(this, R.string.no_text_error, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (savedInstanceState == null) {
            openCreateDocument(defaultPdfFilename())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_SHARED_TEXT, sharedText)
        super.onSaveInstanceState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != REQUEST_CREATE_DOCUMENT) return

        val uri = data?.data
        val text = sharedText
        if (resultCode != RESULT_OK || uri == null || text.isNullOrBlank()) {
            finish()
            return
        }

        savePdf(uri, text)
        finish()
    }

    private fun openCreateDocument(filename: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, filename)
        }
        try {
            startActivityForResult(intent, REQUEST_CREATE_DOCUMENT)
        } catch (_: Exception) {
            Toast.makeText(this, R.string.save_error, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun savePdf(uri: Uri, text: String) {
        try {
            contentResolver.openOutputStream(uri, "wt")?.use { outputStream ->
                PdfWriter().write(text, outputStream)
            } ?: throw IllegalStateException("No output stream for $uri")

            Toast.makeText(this, R.string.saved_message, Toast.LENGTH_SHORT).show()
            openPdf(uri)
        } catch (_: Exception) {
            Toast.makeText(this, R.string.save_error, Toast.LENGTH_LONG).show()
        }
    }

    private fun openPdf(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            clipData = ClipData.newUri(contentResolver, "Saved PDF", uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            startActivity(intent)
        } catch (_: Exception) {
            // Saving succeeded; skip opening if no installed app can view PDFs.
        }
    }

    private fun readSharedText(intent: Intent): String? {
        return when (intent.action) {
            Intent.ACTION_SEND -> intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()
            Intent.ACTION_PROCESS_TEXT -> {
                intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
                    ?: intent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()
            }
            else -> null
        }
    }

    private fun defaultPdfFilename(): String {
        val timestamp = SimpleDateFormat("yyyy-MM-dd-HHmm", Locale.US).format(Date())
        return "clip-$timestamp.pdf"
    }

    companion object {
        private const val KEY_SHARED_TEXT = "sharedText"
        private const val REQUEST_CREATE_DOCUMENT = 1001
    }
}
