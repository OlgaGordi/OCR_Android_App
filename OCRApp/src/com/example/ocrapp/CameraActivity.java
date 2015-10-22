package com.example.ocrapp;

import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

// class modified from:
// http://android-er.blogspot.in/2010/12/camera-preview-on-surfaceview.html
public class CameraActivity extends Activity implements SurfaceHolder.Callback {

	private static final String TAG = "OcrActivity";
	
	Camera camera;
	SurfaceView surfaceView;
	SurfaceHolder surfaceHolder;
	boolean previewing = false;
	
	protected static final String TEMP_IMG = "temp.jpg";
	protected static final int IMG_QUALITY = 100;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);

		Button buttonTakePhoto = (Button) findViewById(R.id.tryAgain);

		getWindow().setFormat(PixelFormat.UNKNOWN);
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		//surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		if (!previewing) {
			camera = Camera.open();
			if (camera != null) {
				try {
					camera.setPreviewDisplay(surfaceHolder);
					camera.startPreview();
					previewing = true;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		buttonTakePhoto.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (camera != null && previewing) {
					Bitmap bitmap = surfaceView.getDrawingCache();
					camera.stopPreview();
					camera.release();
					camera = null;

					previewing = false;

					try {
						FileOutputStream fileout = openFileOutput(
								TEMP_IMG, MODE_PRIVATE);
						bitmap.compress(Bitmap.CompressFormat.JPEG, IMG_QUALITY,
								fileout);
					} catch (Exception e) {
						Log.e(TAG,
								"Caught RuntimeException in request to Tesseract. Setting state to CONTINUOUS_STOPPED.");
						e.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}
}
