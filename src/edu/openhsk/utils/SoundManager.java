package edu.openhsk.utils;

import java.io.IOException;
import java.util.HashMap;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.Log;

public class SoundManager {
	protected static final String LOG_TAG = "SoundManager";
	private static final String FILEPATH = "hsk1_sounds/";

	//SoundPool playback constants
	private static final int STREAM_ERROR = 0;
	private static final float RATE = 1f;
	private static final int LOOP = 0;
	private static final int PRIORITY = 1;
	private static final float RIGHT_VOLUME = 1f;
	private static final float LEFT_VOLUME = 1f;

	private SoundPool soundPool;
	
	/** This hashmap maps soundfiles to soundId's. */
	private HashMap<String, Integer> soundMap;

	public SoundManager(AssetManager assetManager, DatabaseHelper dbh) {
		soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				Log.d(LOG_TAG, "Sounds loaded");
			}
		});
		
		soundMap = new HashMap<String, Integer>();
		
		AssetFileDescriptor afd;
		try { //TODO släng ut allt och byt till lazy loading
			if (soundMap.isEmpty()) {
				SQLiteDatabase db = dbh.getReadableDatabase();
				Cursor cursor = db.query("t_hsk1", new String[] {"_id", "soundfile"}, null, null, null, null, null);
				if (cursor.moveToFirst()) {
					int count = cursor.getCount();
					if (count <= 0) {
						throw new IOException("No content in database");
					}
					while (!cursor.isAfterLast()) {
						String filename = cursor.getString(cursor.getColumnIndex("soundfile"));
						afd = assetManager.openFd(FILEPATH  + filename);
						int soundId = soundPool.load(afd, PRIORITY);
						if (soundId != 0) {
							soundMap.put(filename, soundId);
						} else {
							Log.e(LOG_TAG, "Error loading file " + filename + " into soundpool");
						}
						cursor.moveToNext();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void playSoundFileByName(String filename) {
//		if ("chi-2d9d12c4.ogg".equals(fileName) == false) {
//			Log.e(LOG_TAG, "Wrong file: " + FILEPATH + fileName);
//			return;
//		}
		try {
			Integer soundID = soundMap.get(filename);
			if (soundID != 0) {
				if (soundPool.play(soundID, LEFT_VOLUME, RIGHT_VOLUME, PRIORITY, LOOP, RATE) == STREAM_ERROR) {
					throw new Exception("Playback error for file " + filename + " with soundId " + soundID);
				}
			}
			Log.d(LOG_TAG, "Played file: " + FILEPATH + filename);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
