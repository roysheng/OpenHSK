package edu.openhsk.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.openhsk.data.QuizHanzi;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class QuizHelper {
	private static final String LOG_TAG = "QuizHelper";
	private final Context context;

	public QuizHelper(Context context) {
		this.context = context;
	}

	public List<QuizHanzi> makeQuizList() {
		List<QuizHanzi> list = null;
		
		DatabaseHelper dbh = null;
		SQLiteDatabase db = null;
		try {
			dbh = new DatabaseHelper(context);
			db = dbh.getReadableDatabase();
			
			String[] columns = new String[] {"_id", "word", "pinyin", "definition", "soundfile"};
			Cursor cursor = db.query("t_hsk1", columns, null, null, null, null, "RANDOM() LIMIT 4");
			
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
	
	public void saveQuizAndAnswer() {
		
	}
	
	public void getSavedQuiz() {
		
	}

}
