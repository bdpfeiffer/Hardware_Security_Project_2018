package com.example.vibrationclassifier;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;


import android.hardware.SensorManager;
import android.widget.ProgressBar;


public class MainActivity extends AppCompatActivity implements SensorEventListener{
	private static final String TAG = "MainActivity";
	private static final int WRITE_EXTERNAL_REQUEST_CODE = 8;
	private SensorManager sensorManager;
	private PowerManager powerManager;
	private File dir;                                       // directory of the system
	private FileWriter writer;
	private Sensor accelerometer, gyroscope;
	private PowerManager.WakeLock wakeLock;
	private ProgressBar progressBar;
	private int progress = 0;

	private float data[][];
	private long dataTime[];
	private int index;


	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == WRITE_EXTERNAL_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			Test();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Check whether this app has write external storage permission or not.

		if (Build.VERSION.SDK_INT >= 23) {
			int writeExternalStoragePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
			// If do not grant write external storage permission.
			if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
				// Request user to grant write external storage permission.
				requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_REQUEST_CODE);
			} else {
				Test();
			}
		}


	}

	protected void Test() {

		//gets the file location for the system
		dir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DOCUMENTS), "VCdata");
		dir.mkdirs();
		Log.d(TAG, dir.getPath() + " is writeable: " + dir.canWrite());
		if (!dir.exists()) {

			return;
		}

		//context must be done in the main activity, add the sm to the constructor
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

		wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, getClass().getName());

		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);


		VibrationChallenger challenger = new VibrationChallenger(this, dir, (Vibrator) getSystemService(Context.VIBRATOR_SERVICE));
		Thread vibrationThread = new Thread(challenger);

		vibrationThread.start();

		Log.d(TAG, "completed startup");
	}

	protected void onResume(){
		super.onResume();
		wakeLock.acquire();
	}

	protected void onPause(){
		super.onPause();
		stopRecording("Interrupted", 0 );
		wakeLock.release();
	}

	@Override
	public void onAccuracyChanged(Sensor s, int Accuracy) {
		Log.d(TAG, "accuracy changed to: " + Accuracy + " on: " + s.getName());
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			if ( index < 10000) {
				dataTime[index] = event.timestamp;
				data[index][0] = event.values[0];
				data[index][1] = event.values[1];
				data[index][2] = event.values[2];
				index++;
			}
		} else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
			if ( index < 10000) {
				dataTime[index] = event.timestamp;
				data[index][3] = event.values[0];
				data[index][4] = event.values[1];
				data[index][5] = event.values[2];
				index++;
			}
		}
	}

	public void startRecording(){
		data = new float[10000][6];
		dataTime = new long[10000];
		index = 0;
		sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
	}

	public void stopRecording( String savePath, double progress ){
		sensorManager.unregisterListener(this);
		this.progress = (int) progress * 100;
		progressBar.setProgress(this.progress);
		try {
			File out = new File(dir + "/responses");
			Log.d(TAG, "out location exists: " + out.exists());
			Log.d(TAG, "made dir: " + out.mkdirs());
			Log.d(TAG, "pattern name: " + savePath);
			Log.d(TAG, "full path: " + out.getPath() + "/resp_" + savePath);
			writer = new FileWriter(out.getPath() + "/resp_" + savePath, false);
		} catch (java.io.IOException e) {
			Log.e(TAG, e.getMessage());
		}

		try {
			for (int j = 0; j < index; j++) {
				writer.append(Long.toString(dataTime[j])).append(",")
						.append(Float.toString(data[j][0])).append(",")
						.append(Float.toString(data[j][1])).append(",")
						.append(Float.toString(data[j][2])).append(",")
						.append(Float.toString(data[j][3])).append(",")
						.append(Float.toString(data[j][4])).append(",")
						.append(Float.toString(data[j][5])).append("\n");
			}
			writer.flush();
			writer.close();
		} catch (java.io.IOException e) {
			Log.e(TAG, e.getMessage());
		}
		Log.d(TAG, "recording finished");

	}

}
