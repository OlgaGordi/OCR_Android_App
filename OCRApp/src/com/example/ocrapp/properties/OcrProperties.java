package com.example.ocrapp.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import android.app.Activity;
import android.util.Log;

public class OcrProperties {

	private final static String TAG = "OcrProperties";

	private static OcrProperties ocrProperties;
	private static Activity activity;

	private int tesseractMode = 2;
	private int tesseractPageSegMode = 12;
	private boolean removeHeadersFooters = true;

	public static final String PropertiesName = "ocr.properties";

	public static OcrProperties getInstance() {
		if (ocrProperties == null) {
			if (activity == null) {
				Log.e(TAG, "Activity is not set.");
				return null;
			}
			loadOcrProperties();
		}
		return ocrProperties;
	}

	public static OcrProperties getDefaultInstance() {
		ocrProperties = new OcrProperties();
		return ocrProperties;
	}

	public int getTesseractMode() {
		return tesseractMode;
	}

	public void setTesseractMode(int mode) {
		tesseractMode = mode;
	}

	public int getTesseractPageSegMode() {
		return tesseractPageSegMode;
	}

	public void setTesseractPageSegMode(int pageSegMode) {
		tesseractPageSegMode = pageSegMode;
	}

	public boolean getRemoveHeadersFooters() {
		return removeHeadersFooters;
	}

	public void setRemoveHeadersFooters(boolean removeHF) {
		removeHeadersFooters = removeHF;
	}

	public static void setActivity(Activity a) {
		activity = a;
	}

	private static void loadOcrProperties() {
		ocrProperties = new OcrProperties();
		try {
			FileInputStream fileIn = activity.openFileInput(PropertiesName);
			Properties config = new Properties();
			config.load(fileIn);

			ocrProperties.tesseractMode = Integer.parseInt(config
					.getProperty("tesseract_mode"));
			ocrProperties.tesseractPageSegMode = Integer.parseInt(config
					.getProperty("tesseract_page_seg_mode"));
			ocrProperties.removeHeadersFooters = Boolean.parseBoolean(config
					.getProperty("remove_headers_footers"));
		} catch (IOException e) {
			Log.e(TAG, "Could not load OCR properties file.");
			e.printStackTrace();
		} catch (Exception e) {
			Log.e(TAG, "Error processing OCR properties.");
			e.printStackTrace();
		}
	}

	private OcrProperties() {
	}

}
