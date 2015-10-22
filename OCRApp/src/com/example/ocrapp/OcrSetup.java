package com.example.ocrapp;

import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import com.example.ocrapp.properties.OcrProperties;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.tesseract.android.TessBaseAPI;

public class OcrSetup {

	private static final String TAG = "OcrSetup";
	public static final String lang = "eng";

	private static TessBaseAPI baseApi;
	
	public static OcrResult setupAndroidOCR(Activity activity, String dataPath, String imagePath) {
		Util.setAssetManager(activity.getAssets());
		Util.setUpWorkingDirectory(dataPath);
		OcrProperties.setActivity(activity);
		OcrProperties ocrProperties = OcrProperties.getInstance();
		Bitmap bitmap = loadBitmapFromPath(imagePath);
		if (bitmap == null) {
			return null;
		}
		return OcrSetup.OCR(ocrProperties, dataPath, bitmap);
	}
	
	public static OcrResult setupDesctopTestOCR(String dataPath, String imagePath, OcrProperties properties) {
		Bitmap bitmap = loadBitmapFromPath(dataPath + imagePath);
		return OcrSetup.OCR(properties, dataPath, bitmap);
	}
	
	private static Bitmap loadBitmapFromPath(String imagePath) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

		try {
			ExifInterface exif = new ExifInterface(imagePath);
			int exifOrientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			Log.v(TAG, "Orient: " + exifOrientation);
			int rotate = 0;
			switch (exifOrientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			}
			Log.v(TAG, "Rotation: " + rotate);
			if (rotate != 0) {
				// Getting width & height of the given image.
				int w = bitmap.getWidth();
				int h = bitmap.getHeight();
				// Setting pre rotate
				Matrix mtx = new Matrix();
				mtx.preRotate(rotate);
				// Rotating Bitmap
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
			}
			// Convert to ARGB_8888, required by tess
			bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
			
			// enlarge bitmap???
			return bitmap;
		} catch (IOException e) {
			Log.e(TAG, "Couldn't correct orientation: " + e.toString());
			return null;
		}
	}

	public static OcrResult OCR(OcrProperties ocrProperties, String storageDir, Bitmap bitmap) {
		baseApi = new TessBaseAPI();
		baseApi.init(storageDir, lang, ocrProperties.getTesseractMode());
		baseApi.setPageSegMode(ocrProperties.getTesseractPageSegMode());
		baseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "");
		baseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "");

		OcrResult ocrResult = getOcrResult(bitmap);
		if (ocrResult != null)
			System.out.println("OCRed Text: " + ocrResult.getText());
			//Log.e(TAG, "OCRed Text: " + ocrResult.getText());
		else
			System.out.println("OCRed Text Error: --empty--");
			//Log.e(TAG, "OCRed Text Error: --empty--");
		
		bitmap.recycle();
		baseApi.clear();
		baseApi.end();

		return ocrResult;
	}

	private static OcrResult getOcrResult(Bitmap bitmap) {
		OcrResult ocrResult;
		String textResult;

		try {
			baseApi.setImage(ReadFile.readBitmap(bitmap));
			textResult = baseApi.getUTF8Text();

			// Check for failure to recognize text
			if (textResult == null || textResult.equals("")) {
				return null;
			}
			ocrResult = new OcrResult();
			ocrResult.setWordConfidences(baseApi.wordConfidences());
			ocrResult.setMeanConfidence(baseApi.meanConfidence());
			
			// Always get the word bounding boxes--we want it for annotating the
			// bitmap after the user
			// presses the shutter button, in addition to maybe wanting to draw
			// boxes/words during the
			// continuous mode recognition.
			ocrResult.setWordBoundingBoxes(baseApi.getWords().getBoxRects());
			ocrResult.setRegionBoundingBoxes(baseApi.getRegions().getBoxRects());
			ocrResult.setTextlineBoundingBoxes(baseApi.getTextlines().getBoxRects());
			
			// if (ViewfinderView.DRAW_CHARACTER_BOXES ||
			// ViewfinderView.DRAW_CHARACTER_TEXT) {
			// ocrResult.setCharacterBoundingBoxes(baseApi.getCharacters().getBoxRects());
			// }
		} catch (RuntimeException e) {
			Log.e(TAG,
					"Caught RuntimeException in request to Tesseract. Setting state to CONTINUOUS_STOPPED.");
			e.printStackTrace();
			try {
				baseApi.clear();
			} catch (NullPointerException e1) {
				// Continue
			}
			return null;
		}
		ocrResult.setBitmap(bitmap);
		ocrResult.setText(textResult);
		return ocrResult;
	}
}
