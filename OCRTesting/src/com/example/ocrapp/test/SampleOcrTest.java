package com.example.ocrapp.test;

import java.io.File;
import java.util.Locale;
import java.util.Formatter;

import android.os.Environment;
import android.test.ActivityInstrumentationTestCase2;

import com.example.ocrapp.*;
import com.example.ocrapp.properties.*;

public class SampleOcrTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	private MainActivity ocrActivity;
	private String workingDir;

	private static final double threshold = 100.0;
	private static final String testDir = "test_data";
	private static final String inputPath = testDir + File.separator + "input";
	private static final String expectedOutputPath = testDir + File.separator
			+ "expected_output";

	public SampleOcrTest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		workingDir = Environment.getExternalStorageDirectory().toString()
				+ "/OCRAppTesting/";
		ocrActivity = getActivity();
	}

	public void testPreconditions() {
		assertNotNull("OCR Activity is null", ocrActivity);
		assertNotNull("Working Directory path is null", workingDir);
	}

	// Test
	public void testOriginalInputFile() {
		String inputImage = "jane_eyre_book.jpg";
		String expectedOutputPath = "jane_eyre.txt";
		OcrProperties properties = TestUtil.setProperties(2, 12, false);

		testResults(inputImage, expectedOutputPath, properties);
	}

	// Test
	public void testInputFileEnlarged() {
		String inputImage = "jane_eyre_book_enlarged.jpg";
		String expectedOutputPath = "jane_eyre.txt";
		OcrProperties properties = TestUtil.setProperties(2, 12, false);

		testResults(inputImage, expectedOutputPath, properties);
	}

	// Test
	public void testInputFileBlackWhite() {
		String inputImage = "jane_eyre_book_blackwhite.jpg";
		String expectedOutputPath = "jane_eyre.txt";
		OcrProperties properties = TestUtil.setProperties(2, 12, false);

		testResults(inputImage, expectedOutputPath, properties);
	}

	// Test
	public void testInputFileSharpen() {
		String inputImage = "jane_eyre_book_sharpen.jpg";
		String expectedOutputPath = "jane_eyre.txt";
		OcrProperties properties = TestUtil.setProperties(2, 12, false);

		testResults(inputImage, expectedOutputPath, properties);
	}

	private void testResults(String inputImage, String expectedOutputPath,
			OcrProperties properties) {
		String imagePath = inputPath + File.separator + inputImage;
		TestUtil.setupTestDirs(ocrActivity, workingDir, imagePath, properties);
		String actualResult = OcrSetup.setupDesctopTestOCR(workingDir,
				imagePath, properties).getTextWithoutHeaders();
		String expectedOutput = TestUtil.loadExpectedOutpurFile(ocrActivity,
				expectedOutputPath);

		expectedOutput = TestUtil.getTextAsSingleLine(expectedOutput);
		actualResult = TestUtil.getTextAsSingleLine(actualResult);
		int distance = TestUtil.LevenshteinDistance(expectedOutput,
				actualResult);
		int overall = expectedOutput.length();
		double comparisonResult = 100.0 * (overall - distance) / overall;

		// output the details to console
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb, Locale.ENGLISH);
		formatter
				.format("Results for file :: %1$2s :::: Expected result :: %2$2s :::: Actual result :: %3$2s :::: Comparison score :: %4$2s",
						inputImage, expectedOutput, actualResult,
						comparisonResult);

		assertTrue(sb.toString(), comparisonResult > threshold);
	}

}
