package com.example.ocrapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;

public class Util {
	
	private static final String TAG = "Util";

	private static final String[] CUBE_SUPPORTED_LANGUAGES = {
			"ara", // Arabic
			"eng", // English
			"hin" // Hindi
	};

//	private static final String[] CUBE_REQUIRED_LANGUAGES = {
//			"ara" // Arabic
//	};

	private static final String[] CUBE_DATA_FILES = {
			".cube.bigrams",
			".cube.fold",
			".cube.lm",
			".cube.nn",
			".cube.params",
			".cube.size", // This file is not available for Hindi
			".cube.word-freq",
			".tesseract_cube.nn",
			".traineddata"
	};

	private static String languageCode = "eng";
	private static AssetManager assetManager;
	private static String workDir = "tessdata";

	public static void changeLanguageCode(String lang) {
		languageCode = lang;
	}
	
	public static void setAssetManager(AssetManager assetManager) {
		Util.assetManager = assetManager;
	}
	
	public static boolean setUpWorkingDirectory(String destinationDirBase) {
		boolean installSuccess = false;
		boolean isCubeSupported = false;
		for (String s : CUBE_SUPPORTED_LANGUAGES) {
			if (s.equals(languageCode)) {
				isCubeSupported = true;
			}
		}

		if (!makeDirectory(destinationDirBase + workDir)) {
			return false;
		}
		
		copyPropertiesFile(destinationDirBase, "ocr.properties");
		// copy properties file
		/*File propertiesFile = new File(destinationDirBase + "ocr.properties");
		if (!propertiesFile.exists()) {
			copyPropertiesFile(destinationDirBase, "ocr.properties");
		}*/
		
		File tessdataDir = new File(destinationDirBase + workDir);
		if (isCubeSupported) {
			boolean isAFileMissing = false;
			File dataFile;
			for (String s : CUBE_DATA_FILES) {
				dataFile = new File(tessdataDir.toString() + File.separator
						+ languageCode + s);
				if (!dataFile.exists()) {
					if (!copyFile(destinationDirBase, workDir + File.separator + languageCode + s)) {
						isAFileMissing = true;
					}
				}
			}
			installSuccess = !isAFileMissing;
		}
		else {
			File tesseractTestFile = new File(tessdataDir, languageCode
					+ ".traineddata");
			if (!tesseractTestFile.exists()) {
				if (copyFile(destinationDirBase, workDir + File.separator + languageCode + ".traineddata")) {
					installSuccess = true;
				}
			}
		}
		
		File osdFile = new File(tessdataDir + "/osd.traineddata");
		boolean osdInstallSuccess = true;
		if (!osdFile.exists()) {
			if (!copyFile(destinationDirBase, workDir + "/osd.traineddata")) {
				osdInstallSuccess = false;
			}
		}
		
		return installSuccess && osdInstallSuccess;
	}
	
	public static boolean copyFile(String dir, String fileName) {
		if (assetManager == null) {
			Log.e(TAG, "Asset Manager is not set");
			return false;
		}
		
		try {
    		InputStream in = assetManager.open(fileName);
    		OutputStream out = new FileOutputStream(dir + fileName);
    		// Transfer bytes from in to out
    		byte[] buf = new byte[1024];
    		int len;
    		while ((len = in.read(buf)) > 0) {
    			out.write(buf, 0, len);
    		}
    		in.close();
    		out.close();
    		Log.v(TAG, "Copied " + fileName);
    		return true;
		} catch (IOException e) {
			Log.e(TAG, "Was unable to copy " + fileName + " " + e.toString());
			return false;
		}
	}
	
	public static boolean copyPropertiesFile(String dir, String fileName) {
		if (assetManager == null) {
			Log.e(TAG, "Asset Manager is not set");
			return false;
		}
		
		try {
    		InputStream in = assetManager.open("properties/" + fileName);
    		OutputStream out = new FileOutputStream(dir + fileName);
    		// Transfer bytes from in to out
    		byte[] buf = new byte[1024];
    		int len;
    		while ((len = in.read(buf)) > 0) {
    			out.write(buf, 0, len);
    		}
    		in.close();
    		out.close();
    		Log.v(TAG, "Copied " + fileName);
    		return true;
		} catch (IOException e) {
			Log.e(TAG, "Was unable to copy " + fileName + " " + e.toString());
			return false;
		}
	}
	
	public static boolean makeDirectory(String dirName) {
		File dir = new File(dirName);
		if (!dir.exists() && !dir.mkdirs()) {
			Log.e(TAG, "Couldn't make directory " + dir);
			return false;
		}
		return true;
	}
	
}
