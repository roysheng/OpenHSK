package edu.openhsk.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	protected static final String DBNAME = "openhskdb";
	protected static final int DBVERSION = 3;
	
	/** Table for the HSK1 characters and metadata. */
	public static final String T_HSK1 = "t_hsk1";
	/** Table for cached quizzes. */
	public static final String T_CACHE = "t_cache";
	
	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DBNAME, null, DBVERSION);
	}
	
	public DatabaseHelper(Context context) {
		this(context, "", null, 0);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		//remember to update these statements to 
		//contain all database upgrades
		db.execSQL("CREATE TABLE t_hsk1(" +
				"_id integer primary key," +
				"word text," +
				"pinyin text," +
				"definition text," +
				"searchkey text," +
				"islearned integer," +
				"strokes integer," +
				"soundfile text);");
		db.execSQL("CREATE TABLE t_cache(" +
				"_id integer primary key," +
				"word_id integer);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		if (oldVer != newVer) {
			for (int ver = oldVer; ver < newVer; ver++) {
				if (ver == 1) { //version 1->2
					db.execSQL("ALTER TABLE t_hsk1 ADD COLUMN(soundfile text);");
				} else if (ver == 2) { //version 2->3
					db.execSQL("CREATE TABLE t_cache(" +
						"_id integer primary key," +
						"word_id integer);");
				}
			}
		}
	}

}
