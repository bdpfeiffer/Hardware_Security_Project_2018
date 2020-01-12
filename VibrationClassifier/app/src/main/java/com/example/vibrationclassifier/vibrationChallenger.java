package com.example.vibrationclassifier;

import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class VibrationChallenger implements Runnable {
	private static final String TAG = "VibrationChallenger";
	private MainActivity parent;
	private File dir;
	private Vibrator vibrator;
	private int progressMax, progress;
	private ProgressBar progressBar;

	VibrationChallenger(MainActivity parent, File dir, Vibrator vibrator) {
		this.parent = parent;
		this.dir = dir;
		this.vibrator = vibrator;
	}

	public void run() {

		File file;
		String[] directory;
		//identify patterns .csv's in system directory
		try {
			file = new File(dir + "/patterns");
			file.mkdirs();
			directory = file.list();
		} catch (Exception e) {
			Log.e(TAG, "FILE read ERROR");
			return; //error out
		}

		progressMax = directory.length;
		progress = 0;
		//run on each file in the directory
		for (String filePattern : directory) {

			//setup pattern
			//confirm is csv
			if (!filePattern.contains(".csv")) {
				Log.d(TAG, filePattern + " is non csv ignoring");
				continue;
			} else {
				Log.d(TAG, filePattern + " reading start");
			}

			//prepare a scanner on the target file
			File pattern = new File(dir + "/patterns/" + filePattern);
			Scanner patternScan;
			try {
				patternScan = new Scanner(pattern).useDelimiter(",");
			} catch (FileNotFoundException e) {
				Log.e(TAG, "file disappeared skipping");
				continue;
			}

			Log.d(TAG, "data to read: " + patternScan.hasNext("\\d+,\\d+"));
			//read pattern data
			long[] times = new long[1000];
			int[] amps = new int[1000];
			try {
				int i = 0;
				while (patternScan.hasNextLong()) {
					times[i] = patternScan.nextLong();
					amps[i] = patternScan.nextInt();
					i++;
				}
			} catch (Exception e) {
				Log.e(TAG, "ERROR: " + e.toString());
			}
			long duration = 0;
			StringBuilder temp = new StringBuilder();
			for (long i : times) {
				duration += i;
				temp.append(i).append(", ");
			}
			Log.d(TAG, "Timings: " + temp);
			temp = new StringBuilder();
			for (int i : amps) {
				temp.append(i).append(", ");
			}
			Log.d(TAG, "Amplitudes: " + temp);

			//begin recording
			parent.startRecording();

			if (Build.VERSION.SDK_INT >= 26) {
				vibrator.vibrate(VibrationEffect.createWaveform(times, amps, -1));
			} else {
				vibrator.vibrate(times, -1);
			}
			try {
				Thread.sleep(duration);
			} catch (InterruptedException e) {
				Log.d(TAG, e.getMessage());
			}

			//stop recording
			progress++;
			parent.stopRecording(filePattern, ((double)progress) / progressMax );
			Log.d(TAG, "vibration finished");

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Log.d(TAG, e.getMessage());
			}
		}
	}
}

