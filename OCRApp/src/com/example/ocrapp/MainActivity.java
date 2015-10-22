package com.example.ocrapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	protected Button takePictureButton;
	protected String imgPath;
	protected String dataPath;
	protected boolean taken;
	protected static final String PHOTO_TAKEN = "photo_taken";
	protected static final String TEMP_IMG = "temp.jpg";
	protected static final int IMG_QUALITY = 100;

	protected Uri imageUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*
		 * Code for external storage - SD card
		 */
		String state = null;
		try {
			state = Environment.getExternalStorageState();
		} catch (RuntimeException e) {
			Log.e(TAG, "Is the SD card visible?", e);
			// showErrorMessage("Error",
			// "Required external storage (such as an SD card) is unavailable.");
		}
		dataPath = Environment.getExternalStorageDirectory().toString()
				+ "/OCRApp/";

		// dataPath = getFilesDir() + File.separator;

		imgPath = dataPath + TEMP_IMG;
		// startCameraActivity();
		
		takePictureButton = (Button) findViewById(R.id.tryAgain);
		takePictureButton.setOnClickListener(new ButtonClickHandler());
	}

	public class ButtonClickHandler implements View.OnClickListener {
		public void onClick(View view) {
			Log.v(TAG, "Starting Camera app");
			startCameraActivity();
		}
	}

	// Simple android photo capture:
	// http://labs.makemachine.net/2010/03/simple-android-photo-capture/
	protected void startCameraActivity() {
		File file = new File(imgPath);
	    Uri outputFileUri = Uri.fromFile(file);
	    
	    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
	    
	    startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "resultCode: " + resultCode);
		if (resultCode == -1) {
			onPhotoTaken();
			/*try {
				Bitmap bitmap = (Bitmap) data.getExtras().get("data");
				
				

				FileOutputStream fileout;
				fileout = openFileOutput(TEMP_IMG, MODE_PRIVATE);
				bitmap.compress(Bitmap.CompressFormat.JPEG, IMG_QUALITY,
						fileout);
				fileout.flush();
				fileout.close();
				onPhotoTaken();
			} catch (IOException e) {
				Log.e(TAG,
						"Error closing FileOutputStream");
				e.printStackTrace();
			}*/
		} else {
			Log.v(TAG, "User cancelled");
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(MainActivity.PHOTO_TAKEN, taken);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.i(TAG, "onRestoreInstanceState()");
		if (savedInstanceState.getBoolean(MainActivity.PHOTO_TAKEN)) {
			onPhotoTaken();
		}
	}

	protected void onPhotoTaken() {
		OcrResult result = OcrSetup.setupAndroidOCR(this, dataPath, imgPath);
		EditText resultField = (EditText) findViewById(R.id.editText);
		if (result == null) {
			resultField.setText("Error: Result is empty.");
		} else {
			resultField.setText(getTextAsSingleLine(result.getText()));
		}
		
		/*BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 1;
		Bitmap bitmap = BitmapFactory.decodeFile(imgPath, options);
		ImageView imageview = (ImageView) findViewById(R.id.imageView1);
        imageview.setImageBitmap(bitmap);*/
        
        // delete the image file
        File file = new File(imgPath);
        boolean deleted = file.delete();
	}
	
	private static String getTextAsSingleLine(String input) {
		return input.replace("/n", " ").replaceAll("\\s+", " ").trim();
	}
	
}
