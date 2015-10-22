package com.example.ocrapp.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.example.ocrapp.MainActivity;
import com.example.ocrapp.Util;
import com.example.ocrapp.properties.OcrProperties;

public class TestUtil {

	private static final String newLine = "/n";
	private static final String testDir = "test_data";
	private static final String inputPath = testDir + File.separator + "input";
	private static final String expectedOutputPath = testDir + File.separator
			+ "expected_output";

	public static void setupTestDirs(MainActivity ocrActivity, String dataPath,
			String imagePath, OcrProperties properties) {
		Util.setAssetManager(ocrActivity.getAssets());
		Util.setUpWorkingDirectory(dataPath);
		Util.makeDirectory(dataPath + testDir);
		Util.makeDirectory(dataPath + inputPath);
		Util.copyFile(dataPath, imagePath);
	}

	/**
	 * 
	 * @param tessMode
	 *            0 - Run Tesseract only - fastest. 1 - Run Cube only - better
	 *            accuracy, but slower. 2 - Run both and combine results - best
	 *            accuracy. 3 - Default OCR engine mode.
	 * 
	 * @param tessPageSegMode
	 *            0 - Orientation and script detection (OSD) only. 1 - Automatic
	 *            page segmentation with OSD. 2 - Automatic page segmentation,
	 *            but no OSD, or OCR 3 - Fully automatic page segmentation, but
	 *            no OSD. (Default) 4 - Assume a single column of text of
	 *            variable sizes. 5 - Assume a single uniform block of
	 *            vertically aligned text. 6 - Assume a single uniform block of
	 *            text. 7 - Treat the image as a single text line. 8 - Treat the
	 *            image as a single word. 9 - Treat the image as a single word
	 *            in a circle. 10 - Treat the image as a single character. 11 -
	 *            Find as much text as possible in no particular order. 12 -
	 *            Sparse text with orientation and script detection.
	 * 
	 * @return OcrProperties
	 */
	public static OcrProperties setProperties(int tessMode,
			int tessPageSegMode, boolean removeHeadersFooters) {
		OcrProperties props = OcrProperties.getDefaultInstance();
		props.setTesseractMode(tessMode);
		props.setTesseractPageSegMode(tessPageSegMode);
		props.setRemoveHeadersFooters(removeHeadersFooters);
		return props;
	}

	// method from :
	// http://howtodoinjava.com/2013/10/06/how-to-read-data-from-inputstream-into-string-in-java/
	public static String loadExpectedOutpurFile(MainActivity ocrActivity,
			String fileName) {
		String filePath = expectedOutputPath + File.separator + fileName;
		try {
			InputStream in = ocrActivity.getAssets().open(filePath);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			StringBuilder out = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				out.append(line);
				out.append(" ");
			}
			reader.close();
			return out.toString();
		} catch (IOException e) {
			System.out.println("IOException in loadExpectedOutpurFile : "
					+ e.getMessage());
			return null;
		}
	}

	public static String getTextAsSingleLine(String input) {
		return input.replace(newLine, " ").replaceAll("\\s+", " ").trim();
	}

	// method from :
	// http://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Java
	public static int LevenshteinDistance(String s0, String s1) {
		int len0 = s0.length() + 1;
		int len1 = s1.length() + 1;

		// the array of distances
		int[] cost = new int[len0];
		int[] newcost = new int[len0];

		// initial cost of skipping prefix in String s0
		for (int i = 0; i < len0; i++)
			cost[i] = i;

		// dynamically computing the array of distances

		// transformation cost for each letter in s1
		for (int j = 1; j < len1; j++) {
			// initial cost of skipping prefix in String s1
			newcost[0] = j;

			// transformation cost for each letter in s0
			for (int i = 1; i < len0; i++) {
				// matching current letters in both strings
				int match = (s0.charAt(i - 1) == s1.charAt(j - 1)) ? 0 : 1;

				// computing cost for each transformation
				int cost_replace = cost[i - 1] + match;
				int cost_insert = cost[i] + 1;
				int cost_delete = newcost[i - 1] + 1;

				// keep minimum cost
				newcost[i] = Math.min(Math.min(cost_insert, cost_delete),
						cost_replace);
			}

			// swap cost/newcost arrays
			int[] swap = cost;
			cost = newcost;
			newcost = swap;
		}

		// the distance is the cost for transforming all letters in both strings
		return cost[len0 - 1];
	}

}
