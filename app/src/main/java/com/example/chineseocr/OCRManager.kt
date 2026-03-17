import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

public class OCRManager {
    private static final String TAG = "OCRManager";
    private TessBaseAPI tessBaseAPI;
    private String dataPath;

    public OCRManager(Context context) {
        dataPath = context.getExternalFilesDir(null) + "/tesseract/";
        tessBaseAPI = new TessBaseAPI();
        initializeTesseract();
    }

    private void initializeTesseract() {
        tessBaseAPI.init(dataPath, "chi_sim"); // Chinese Simplified
        Log.d(TAG, "Tesseract initialized with data path: " + dataPath);
    }

    public String recognizeText(Bitmap bitmap) {
        tessBaseAPI.setImage(bitmap);
        String recognizedText = tessBaseAPI.getUTF8Text();
        Log.d(TAG, "Recognized text: " + recognizedText);
        return recognizedText;
    }

    public void cleanup() {
        tessBaseAPI.end();
        Log.d(TAG, "Tesseract cleaned up.");
    }
}