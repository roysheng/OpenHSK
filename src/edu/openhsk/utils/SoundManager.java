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
	private final AssetManager assetManager;

	public SoundManager(AssetManager assetManager) {
		this.assetManager = assetManager;
		soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				if (status == 0 && sampleId != 0) {
					if (soundPool.play(sampleId, LEFT_VOLUME, 
							RIGHT_VOLUME, PRIORITY, LOOP, 
								RATE) == STREAM_ERROR) {
						Log.e(LOG_TAG, "Playback error for file with " +
								"soundId " + sampleId);
					} else {
						Log.d(LOG_TAG, "Played soundfile ??? with id " + sampleId);
					}
				}
			}
		});
		
		soundMap = new HashMap<String, Integer>();
	}
	
	public void playSoundFile(String filename) {
		try {
			int soundID = 0;
			try {
				soundID = soundMap.get(filename);
				if (soundID == 0) {
					throw new Exception("Error loading soundfile " + filename);
				}
				if (soundPool.play(soundID, LEFT_VOLUME, RIGHT_VOLUME, 
						PRIORITY, LOOP, RATE) == STREAM_ERROR) {
					throw new Exception("Playback error for file " + 
						filename + " with soundId " + soundID);
				}
				Log.d(LOG_TAG, "Played file: " + FILEPATH + filename);
			} catch (NullPointerException e) {
				soundID = loadSound(filename);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int loadSound(String filename) {
		AssetFileDescriptor afd;
		try {
			afd = assetManager.openFd(FILEPATH  + filename);
			int soundId = soundPool.load(afd, PRIORITY);
			if (soundId != 0) {
				soundMap.put(filename, soundId);
				return soundId;
			} else {
				Log.e(LOG_TAG, "Error loading file " + filename + 
						" into soundpool");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
