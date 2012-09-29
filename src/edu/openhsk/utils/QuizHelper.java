package edu.openhsk.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.openhsk.data.QuizHanzi;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static edu.openhsk.utils.DatabaseHelper.T_CACHE;
import static edu.openhsk.utils.DatabaseHelper.T_HSK1;

public class QuizHelper {
	private static final String LOG_TAG = "QuizHelper";
	private final Context context;
	
	public QuizHelper(Context context) {
		this.context = context;
	}
	
	public List<QuizHanzi> makeQuizList(boolean quizIsCached) {
		List<QuizHanzi> list = null;
		
		DatabaseHelper dbh = null;
		SQLiteDatabase db = null;
		try {
			dbh = new DatabaseHelper(context);
			db = dbh.getReadableDatabase();
			
			String[] columns = new String[] {"_id", "word", "pinyin", "definition", "soundfile"};
			Cursor cursor;
			if (quizIsCached) {
//				cursor = db.rawQuery("SELECT * FROM wordsTable WHERE _id IN (SELECT word_id FROM savedQuizTable)", null);
				cursor = db.query(T_HSK1, columns, "_id IN (SELECT word_id FROM " + T_CACHE + ")", null, null, null, null);
			} else {
				cursor = db.query(T_HSK1, columns, null, null, null, null, "RANDOM() LIMIT 4");
			}
			
			if (cursor != null && cursor.getCount() == 4) {
				if (cursor.moveToFirst()) {
	    			//store words in list
					list = new ArrayList<QuizHanzi>(4);
	    			do {
	    				int wordId = cursor.getInt(cursor.getColumnIndex("_id"));
	    				String wordStr = cursor.getString(cursor.getColumnIndex("word"));
	    				String pinyinStr = cursor.getString(cursor.getColumnIndex("pinyin"));
	    				String defStr = cursor.getString(cursor.getColumnIndex("definition"));
	    				String soundStr = cursor.getString(cursor.getColumnIndex("soundfile"));
	    				list.add(new QuizHanzi(wordId, wordStr, pinyinStr, defStr, soundStr));
	    			} while (cursor.moveToNext());
	    		} else {
	    			Log.e(LOG_TAG, "Error moving cursor to first row");
	    		}
			} else {
	    		Log.e(LOG_TAG, "Error, wrong amount of words in cursor:" + cursor.getCount());
	    	}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null && db.isOpen()) {
				db.close();
			}
			if (dbh != null) {
				dbh.close();
			}
		}
		
		return list;
	}
	
	public int chooseCorrectAnswer(List<QuizHanzi> quizWordList) {
		//randomizes index and answer
		int randInt = -1;
		randInt = new Random(System.currentTimeMillis()).nextInt(4);
		int idOfAnswer = quizWordList.get(randInt).getId();
		Log.d(LOG_TAG, "Index of answer: " + randInt + 
				" Id of answer: " + idOfAnswer);
		return idOfAnswer;
	}
	
	private void saveQuiz(int[] hanziIds) {
		DatabaseHelper dbh = null;
		SQLiteDatabase db = null;
		try {
			dbh = new DatabaseHelper(context);
			db = dbh.getReadableDatabase();
			
			Cursor cursor = db.query(T_CACHE, new String[] {"_id"}, null, null, null, null, null);
			int count = cursor.getCount();
			if (count > 0) {
				db.delete(T_CACHE, null, null);
			}
			
			ContentValues values = new ContentValues(1);
			for (int i = 0; i < hanziIds.length; i++) {
				values.put("word_id", hanziIds[i]);
				db.insert(T_CACHE, null, values);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) db.close();
			if (dbh != null) dbh.close();
		}
	}
	
	public void invalidateCache() {
		DatabaseHelper dbh = null;
		SQLiteDatabase db = null;
		try {
			dbh = new DatabaseHelper(context);
			db = dbh.getReadableDatabase();
			
			Cursor cursor = db.query(T_CACHE, new String[] {"_id"}, 
					null, null, null, null, null);
			int count = cursor.getCount();
			if (count > 0) {
				db.delete(T_CACHE, null, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) db.close();
			if (dbh != null) dbh.close();
		}
	}

	public void cacheQuiz(List<QuizHanzi> quizWordList) {
		int[] hanziIds = { 
			quizWordList.get(0).getId(),
			quizWordList.get(1).getId(), 
			quizWordList.get(2).getId(),
			quizWordList.get(3).getId(), 
		};
		saveQuiz(hanziIds);
	}

}
