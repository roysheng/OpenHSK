package edu.openhsk.utils;

import java.io.IOException;
import java.util.HashMap;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.Log;

public class SoundManager {
	protected static final String LOG_TAG = "SoundManager";
	private AssetManager assetManager;
	private String filePath = "hsk1_sounds/";
	private SoundPool sp;
	private HashMap<Integer, Integer> soundMap = new HashMap<Integer, Integer>();

	public SoundManager(AssetManager assetManager) {
		this.assetManager = assetManager;
		sp = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
		sp.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				Log.d(LOG_TAG, "Sound loaded");
			}
		});
		
		AssetFileDescriptor afd;
		try {
			afd = assetManager.openFd(filePath  + "chi-2d9d12c4.ogg");
			int soundID = sp.load(afd, 1);
			soundMap.put(1, soundID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void playSoundFileByName(String fileName) {
		if ("chi-2d9d12c4.ogg".equals(fileName) == false) {
			Log.d(LOG_TAG, "Wrong file: " + filePath + fileName);
			return;
		}
		try {
			if (sp.play(soundMap.get(1), 1f, 1f, 1, 0, 1f) == 0) {
				throw new Exception();
			}
			Log.d(LOG_TAG, "Played file: " + filePath + fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
