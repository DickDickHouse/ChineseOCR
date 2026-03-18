# ChineseOCR

Android app prototype for Chinese OCR (Optical Character Recognition).

## Project recap (current status)

### What is implemented
- Capture image from camera and pick image from gallery
- Run OCR through `OCRManager` (Tesseract `chi_sim`)
- Show recognized text in the main screen
- Save OCR result as:
  - `.txt`
  - `.xlsx`
  - `.doc`

### Main code structure
- `app/src/main/java/com/example/chineseocr/MainActivity.kt`  
  UI flow: image input, OCR trigger, and save dialog
- `app/src/main/java/com/example/chineseocr/OCRManager.kt`  
  Tesseract initialization and text recognition
- `app/src/main/java/com/example/chineseocr/FileExportManager.kt`  
  Export helpers for TXT/XLSX/DOC

### Current gaps / risks
- Build setup is incomplete for Android currently:
  - `settings.gradle.kts` uses invalid Kotlin DSL syntax (`include ":app"` instead of `include(":app")`)
  - `app/build.gradle.kts` is configured as JVM Kotlin, not Android application module
- No automated tests (`app/src/test` and `app/src/androidTest` are not present)
- README had no project status details before this recap
