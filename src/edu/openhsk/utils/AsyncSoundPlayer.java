package edu.openhsk.utils;

import android.os.AsyncTask;
import android.util.Log;

public class AsyncSoundPlayer extends AsyncTask<Object,Integer,Boolean> {
	private static final String LOG_TAG = "AsyncSoundPlayer";

	@Override
	protected Boolean doInBackground(Object... params) {
		String fileName = (String) params[0];
		SoundManager soundManager = (SoundManager) params[1];
		Log.d(LOG_TAG, "Playing soundfile " + fileName);
		soundManager.playSoundFile(fileName);
		return true;
	}
}