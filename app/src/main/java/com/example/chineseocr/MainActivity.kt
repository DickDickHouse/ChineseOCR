package com.example.chineseocr

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var textView: TextView
    private lateinit var ocrManager: OCRManager
    private lateinit var fileExportManager: FileExportManager
    
    private var currentBitmap: Bitmap? = null
    private val CAMERA_REQUEST = 100
    private val GALLERY_REQUEST = 101
    private val PERMISSION_REQUEST = 200

    companion object {
        private const val TAG = "MainActivity"
        private const val PREFS_NAME = "chinese_ocr_session"
        private const val KEY_LAST_TEXT = "last_text"
        private const val KEY_LAST_IMAGE_PATH = "last_image_path"
        private const val SESSION_IMAGE_FILE = "last_selected_image.png"
        private const val OUTPUT_DIRECTORY_NAME = "ChineseOCR"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.image_view)
        textView = findViewById(R.id.text_view)
        val cameraButton: Button = findViewById(R.id.button_camera)
        val galleryButton: Button = findViewById(R.id.button_gallery)
        val ocrButton: Button = findViewById(R.id.button_ocr)
        val saveButton: Button = findViewById(R.id.button_save)

        ocrManager = OCRManager(this)
        fileExportManager = FileExportManager()

        cameraButton.setOnClickListener { checkPermissionAndOpenCamera() }
        galleryButton.setOnClickListener { openGallery() }
        ocrButton.setOnClickListener { performOCR() }
        saveButton.setOnClickListener { showSaveDialog() }

        restoreSession(savedInstanceState)
    }

    private fun checkPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST)
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST -> {
                    val bitmap = data?.extras?.get("data") as? Bitmap
                    if (bitmap != null) {
                        updateCurrentBitmap(bitmap)
                    }
                }
                GALLERY_REQUEST -> {
                    val imageUri = data?.data
                    if (imageUri != null) {
                        try {
                            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                            updateCurrentBitmap(bitmap)
                        } catch (e: IOException) {
                            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun performOCR() {
        if (currentBitmap != null) {
            val text = ocrManager.recognizeText(currentBitmap!!)
            textView.text = text
            persistRecognizedText(text)
            Toast.makeText(this, "OCR completed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showSaveDialog() {
        val text = textView.text.toString()
        if (text.isEmpty()) {
            Toast.makeText(this, "No text to save", Toast.LENGTH_SHORT).show()
            return
        }

        val formats = arrayOf("TXT", "XLSX", "DOC")
        AlertDialog.Builder(this)
            .setTitle("Select file format")
            .setItems(formats) { _, which ->
                val fileName = "OCR_${System.currentTimeMillis()}"
                val documentsDir = getOutputDirectory()
                
                when (which) {
                    0 -> {
                        val filePath = File(documentsDir, "$fileName.txt").absolutePath
                        showSaveResult(fileExportManager.saveAsTxt(text, filePath), filePath)
                    }
                    1 -> {
                        val filePath = File(documentsDir, "$fileName.xlsx").absolutePath
                        showSaveResult(fileExportManager.saveAsXlsx(listOf(text), filePath), filePath)
                    }
                    2 -> {
                        val filePath = File(documentsDir, "$fileName.doc").absolutePath
                        showSaveResult(fileExportManager.saveAsDoc(text, filePath), filePath)
                    }
                }
            }
            .show()
    }

    private fun restoreSession(savedInstanceState: Bundle?) {
        val sessionPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val restoredText = savedInstanceState?.getString(KEY_LAST_TEXT)
            ?: sessionPrefs.getString(KEY_LAST_TEXT, null)

        if (!restoredText.isNullOrEmpty()) {
            textView.text = restoredText
        }

        val imagePath = savedInstanceState?.getString(KEY_LAST_IMAGE_PATH)
            ?: sessionPrefs.getString(KEY_LAST_IMAGE_PATH, null)

        if (!imagePath.isNullOrEmpty()) {
            val restoredBitmap = BitmapFactory.decodeFile(imagePath)
            if (restoredBitmap != null) {
                currentBitmap = restoredBitmap
                imageView.setImageBitmap(restoredBitmap)
            }
        }
    }

    private fun updateCurrentBitmap(bitmap: Bitmap) {
        currentBitmap = bitmap
        imageView.setImageBitmap(bitmap)
        persistCurrentBitmap(bitmap)
    }

    private fun persistCurrentBitmap(bitmap: Bitmap) {
        val imageFile = File(filesDir, SESSION_IMAGE_FILE)
        try {
            FileOutputStream(imageFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.flush()
            }
            getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString(KEY_LAST_IMAGE_PATH, imageFile.absolutePath)
                .apply()
        } catch (exception: IOException) {
            Log.w(TAG, "Unable to persist selected image for session restore", exception)
        }
    }

    private fun persistRecognizedText(text: String) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .edit()
            .putString(KEY_LAST_TEXT, text)
            .apply()
    }

    private fun getOutputDirectory(): File {
        val baseDirectory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: filesDir
        return File(baseDirectory, OUTPUT_DIRECTORY_NAME).apply {
            mkdirs()
        }
    }

    private fun showSaveResult(saved: Boolean, filePath: String) {
        val message = if (saved) {
            "Saved as: $filePath"
        } else {
            "Unable to save file"
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_LAST_TEXT, textView.text.toString())
        outState.putString(
            KEY_LAST_IMAGE_PATH,
            getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(KEY_LAST_IMAGE_PATH, null)
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ocrManager.cleanup()
    }
}
