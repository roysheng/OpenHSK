package edu.openhsk.utils;

import edu.openhsk.MainActivity;
import edu.openhsk.data.Hanzi;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CharacterDAO {
	public static final String T_HSK1 = "t_hsk1";

	/** Helper class for initializing databases and creating cursors. */
	private DatabaseHelper dbh;

	/** The database connection. */
	private SQLiteDatabase db;

	public CharacterDAO(DatabaseHelper dbh) {
		this.dbh = dbh;
	}

	public CharacterDAO(DatabaseHelper dbh, boolean keepAlive) {
		this.dbh = dbh;
		if (keepAlive) {
			db = dbh.getWritableDatabase();
		}
	}

	public void insertCharacterIntoDB(Hanzi h) {
		db = dbh.getWritableDatabase();
		db.insertOrThrow(T_HSK1, null, h.toContentValues());
	}

	public Hanzi getCharacterById(int id) {
		db = dbh.getReadableDatabase();
		String[] fields = new String[] {"_id","word","pinyin","definition","islearned","strokes","soundfile"};
		Cursor cursor = db.query(T_HSK1, fields, "_id = ?", new String[] {"" + id}, null, null, null);
		if (cursor.moveToFirst()) {
			String word = cursor.getString(cursor.getColumnIndex("word"));
			String pinyin = cursor.getString(cursor.getColumnIndex("pinyin"));
			String def = cursor.getString(cursor.getColumnIndex("definition"));
			int islearned = cursor.getInt(cursor.getColumnIndex("islearned"));
			int strokes = cursor.getInt(cursor.getColumnIndex("strokes"));
			String soundfile = cursor.getString(cursor.getColumnIndex("soundfile"));
			return new Hanzi(word, pinyin, def, islearned == 1 ? true : false, strokes, soundfile);
		}
		return null;
	}

	/**
	 * Checks the status of the various database tables. If true, all 
	 * tables have been successfully populated. If false, tables have 
	 * not been populated or are only partially populated.
	 * @return whether HSK tables have been successfully initiated.
	 */
	public boolean getTableStatus() {
		SQLiteDatabase db2 = dbh.getReadableDatabase();
		Cursor cursor = db2.query(T_HSK1, new String[] {"_id"}, "", null, "", "", "");
		boolean status;
		if (cursor.getCount() > 0) {
			status =  true;
		} else {
			status = false;
		}
		cursor.close();
		db2.close();
		return status;
	}
	
	public String[] getCharacterData(int id) {
		db = dbh.getReadableDatabase();
		String[] columns = new String[] {"_id", "word", "pinyin", "definition"};
		Cursor cursor = db.query("t_hsk1", columns, "_id = ?", new String[] {"" + id}, null, null, null);
		String[] arr = null;
		if (cursor.moveToFirst()) {
			arr = new String[3];
			arr[0] = cursor.getString(cursor.getColumnIndex(columns[1]));
			arr[1] = cursor.getString(cursor.getColumnIndex(columns[2]));
			arr[2] = cursor.getString(cursor.getColumnIndex(columns[3]));
		}
		cursor.close();
		db.close();
		return arr;
	}

	public void closeDB() {
		if (db != null) {
			db.close();
		}
	}
}
