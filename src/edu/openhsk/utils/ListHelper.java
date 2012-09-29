package edu.openhsk.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ListHelper {
	private static final String LOG_TAG = "ListHelper";
	private final Context context;

	public ListHelper(Context context) {
		this.context = context;
	}

	public String getFileNameByWord(String word) {
		String fileName = null;
		if (word == null || word.equals("")) {
			return fileName;
		}
		
		DatabaseHelper dbh = null;
		SQLiteDatabase db = null;
		try {
			dbh = new DatabaseHelper(context);
			db = dbh.getReadableDatabase();
			Cursor cursor = db.query("t_hsk1", new String[] {"_id","soundfile"}, "word = ?", new String[] {word}, null, null, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					fileName = cursor.getString(cursor.getColumnIndex("soundfile"));
				} else {
					Log.e(LOG_TAG, "Error moving cursor to first row");
				}
				cursor.close();
			} else {
				Log.e(LOG_TAG, "Cursor returned null for query word: " + word);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) db.close();
			if (dbh != null) dbh.close();
		}
		
		return fileName;
	}
	
}
