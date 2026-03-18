# ChineseOCR
Android OCR app for capturing or selecting an image, extracting Chinese text (simplified + traditional), and exporting the result.

The app now restores the last selected image and recognized text when reopened, and exported files are written into the app's Documents/ChineseOCR directory.
When exporting to XLSX, table-like OCR output (pipe/box outlines, tab-separated values, or spaced columns) is split into multiple rows/columns to better fit Excel tables.

To build a debug APK into the repository root directory, run:

`./gradlew :app:exportDebugApkToRoot`

The generated file will be:

`./ChineseOCR-debug.apk`
