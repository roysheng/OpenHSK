package edu.openhsk.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	protected static final String DBNAME = "openhskdb";
	protected static final int DBVERSION = 2;

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DBNAME, null, DBVERSION);
	}

	public DatabaseHelper(Context context) {
		this(context, DBNAME, null, DBVERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE t_hsk1(" +
				"_id integer primary key," +
				"word text," +
				"pinyin text," +
				"definition text," +
				"searchkey text," +
				"islearned integer," +
				"strokes integer," +
				"soundfile text);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
		if (newVer == 2) {
			db.execSQL("ALTER TABLE t_hsk1 ADD COLUMN(soundfile text);");
		}
	}

}
