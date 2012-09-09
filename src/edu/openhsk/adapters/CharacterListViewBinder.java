package edu.openhsk.adapters;

import android.database.Cursor;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class CharacterListViewBinder implements SimpleCursorAdapter.ViewBinder {

	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
		//columnIndex == 0 is the mandatory _id for the SQLite table.
		if (columnIndex == 1) { //word
			TextView word = (TextView) view;
			word.setText(cursor.getString(cursor.getColumnIndex("word")));
		} else if (columnIndex == 2) { //pinyin
			TextView pinyin = (TextView) view;
			pinyin.setText(cursor.getString(cursor.getColumnIndex("pinyin")));
		} else if (columnIndex == 3) { //def
			TextView def = (TextView) view;
			def.setText(cursor.getString(cursor.getColumnIndex("definition")));
		} else if (columnIndex == 4) { //islearned
//			CheckBox isLearnedView = (CheckBox) view;
//			int isLearned = cursor.getInt(cursor.getColumnIndex("islearned"));
//			isLearnedView.setSelected(isLearned == 1 ? true : false);
		}
		return false;
	}

}
