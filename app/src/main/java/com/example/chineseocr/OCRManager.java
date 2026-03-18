package com.example.chineseocr;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

public class OCRManager {
    private static final String TAG = "OCRManager";
    private static final String LANGUAGE_SIMPLIFIED = "chi_sim";
    private static final String LANGUAGE_TRADITIONAL = "chi_tra";
    private static final String LANGUAGE_COMBINED = LANGUAGE_SIMPLIFIED + "+" + LANGUAGE_TRADITIONAL;

    private final TessBaseAPI tessBaseAPI;
    private final String dataPath;
    private String activeLanguage = LANGUAGE_SIMPLIFIED;

    public OCRManager(Context context) {
        dataPath = context.getExternalFilesDir(null) + "/tesseract/";
        tessBaseAPI = new TessBaseAPI();
        initializeTesseract();
    }

    private void initializeTesseract() {
        activeLanguage = resolveLanguageModel();
        tessBaseAPI.init(dataPath, activeLanguage);
        Log.d(TAG, "Tesseract initialized with data path: " + dataPath + ", language: " + activeLanguage);
    }

    private String resolveLanguageModel() {
        File tessDataDir = new File(dataPath, "tessdata");
        boolean hasSimplified = new File(tessDataDir, LANGUAGE_SIMPLIFIED + ".traineddata").exists();
        boolean hasTraditional = new File(tessDataDir, LANGUAGE_TRADITIONAL + ".traineddata").exists();

        if (hasSimplified && hasTraditional) {
            return LANGUAGE_COMBINED;
        }
        if (hasTraditional) {
            return LANGUAGE_TRADITIONAL;
        }
        if (hasSimplified) {
            return LANGUAGE_SIMPLIFIED;
        }

        Log.w(TAG, "No Chinese traineddata found in " + tessDataDir + ". Falling back to " + LANGUAGE_COMBINED);
        return LANGUAGE_COMBINED;
    }

    public String recognizeText(Bitmap bitmap) {
        tessBaseAPI.setImage(bitmap);
        String recognizedText = tessBaseAPI.getUTF8Text();
        Log.d(TAG, "Recognized text (" + activeLanguage + "): " + recognizedText);
        return recognizedText;
    }

    public void cleanup() {
        tessBaseAPI.end();
        Log.d(TAG, "Tesseract cleaned up.");
    }
}
