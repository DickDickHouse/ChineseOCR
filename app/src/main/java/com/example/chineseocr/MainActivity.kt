package com.example.chineseocr

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
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
                        currentBitmap = bitmap
                        imageView.setImageBitmap(bitmap)
                    }
                }
                GALLERY_REQUEST -> {
                    val imageUri = data?.data
                    if (imageUri != null) {
                        try {
                            currentBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                            imageView.setImageBitmap(currentBitmap)
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
                val documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                
                when (which) {
                    0 -> {
                        val filePath = File(documentsDir, "$fileName.txt").absolutePath
                        fileExportManager.saveAsTxt(text, filePath)
                        Toast.makeText(this, "Saved as: $filePath", Toast.LENGTH_LONG).show()
                    }
                    1 -> {
                        val filePath = File(documentsDir, "$fileName.xlsx").absolutePath
                        fileExportManager.saveAsXlsx(listOf(text), filePath)
                        Toast.makeText(this, "Saved as: $filePath", Toast.LENGTH_LONG).show()
                    }
                    2 -> {
                        val filePath = File(documentsDir, "$fileName.doc").absolutePath
                        fileExportManager.saveAsDoc(text, filePath)
                        Toast.makeText(this, "Saved as: $filePath", Toast.LENGTH_LONG).show()
                    }
                }
            }
            .show()
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