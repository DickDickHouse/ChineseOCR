package com.example.chineseocr;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        List<String> languageCandidates = resolveLanguageCandidates();
        for (String language : languageCandidates) {
            if (tessBaseAPI.init(dataPath, language)) {
                activeLanguage = language;
                Log.d(TAG, "Tesseract initialized with data path: " + dataPath + ", language: " + activeLanguage);
                return;
            }
            Log.w(TAG, "Failed to initialize Tesseract with language: " + language);
        }

        throw new IllegalStateException(
                "Unable to initialize Tesseract. Expected traineddata under "
                        + new File(dataPath, "tessdata").getAbsolutePath()
        );
    }

    private List<String> resolveLanguageCandidates() {
        File tessDataDir = new File(dataPath, "tessdata");
        boolean hasSimplified = new File(tessDataDir, LANGUAGE_SIMPLIFIED + ".traineddata").exists();
        boolean hasTraditional = new File(tessDataDir, LANGUAGE_TRADITIONAL + ".traineddata").exists();
        List<String> candidates = new ArrayList<>();

        if (hasSimplified && hasTraditional) {
            candidates.add(LANGUAGE_COMBINED);
            candidates.add(LANGUAGE_SIMPLIFIED);
            candidates.add(LANGUAGE_TRADITIONAL);
            return candidates;
        }
        if (hasTraditional) {
            candidates.add(LANGUAGE_TRADITIONAL);
            return candidates;
        }
        if (hasSimplified) {
            candidates.add(LANGUAGE_SIMPLIFIED);
            return candidates;
        }

        Log.w(TAG, "No Chinese traineddata found in " + tessDataDir + ". Initialization may fail; trying known Chinese language fallbacks.");
        candidates.add(LANGUAGE_SIMPLIFIED);
        candidates.add(LANGUAGE_TRADITIONAL);
        candidates.add(LANGUAGE_COMBINED);
        return candidates;
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
