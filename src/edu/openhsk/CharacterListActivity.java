package edu.openhsk;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import edu.openhsk.adapters.CharacterListViewBinder;
import edu.openhsk.utils.AsyncSoundPlayer;
import edu.openhsk.utils.CharacterDAO;
import edu.openhsk.utils.DatabaseHelper;
import edu.openhsk.utils.SoundManager;

public class CharacterListActivity extends Activity {
	public static final String LENGTH_FROM_TOP = "lengthFromTop";
	public static final String LIST_POS = "listPos";
	private static final String LOG_TAG = "CharacterListActivity";
	private static final String[] queryColumns = new String[] {"_id", "word", "pinyin", "definition", "islearned"};
	public static final String PREFS_NAME = "edu.openhsk.list.prefs";
	
	private DatabaseHelper dbh;
	private ListView listView;
	private CharacterListViewBinder viewBinder;
	private SimpleCursorAdapter adapter;
	private Cursor cursor;
	private SoundManager soundManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.charlist);
		
		dbh = new DatabaseHelper(this);
		SQLiteDatabase db = dbh.getReadableDatabase();
		
		listView = (ListView) findViewById(R.id.charListView);
		cursor = db.query("t_hsk1", queryColumns, "", null, "", "", "");
		refreshList(cursor);
		
		soundManager = new SoundManager(getAssets());
		listView.setOnItemClickListener(new PlaySoundClickListener());
		listView.setFastScrollEnabled(true);
		
		startManagingCursor(cursor);
		db.close(); //bugfix?
	}

	private void refreshList(Cursor cursor) {
		if (cursor.getCount() <= 0) {
			listView.setVisibility(View.GONE);
			new TextView(this).setText("No characters found...");
		} else {
			String[] from = new String[] {"word", "pinyin", "definition", "islearned"};
			int[] to = new int[] {R.id.charListView, R.id.pinyinListView, R.id.defListView};
			adapter = new SimpleCursorAdapter(this, R.layout.list_item, cursor, from, to);
			adapter.setViewBinder(viewBinder);
			listView.setAdapter(adapter);
		}
	}
	
	@Override
	protected void onResume() {
		dbh = new DatabaseHelper(this);
		SQLiteDatabase db = dbh.getReadableDatabase();
		
		listView = (ListView) findViewById(R.id.charListView);
		cursor = db.query("t_hsk1", queryColumns, "", null, "", "", "");
		refreshList(cursor);
		
		startManagingCursor(cursor);
		db.close();
		
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_WORLD_WRITEABLE);
		int listPos = prefs.getInt(LIST_POS, 0);
		int lengthFromTop = prefs.getInt(LENGTH_FROM_TOP, 0);
		listView.setSelectionFromTop(listPos, lengthFromTop);
		if (listPos != 0) {
			Log.d(LOG_TAG, "Restoring list position to index " + listPos);
		}
		
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		if (listView != null) {
			int position = listView.getFirstVisiblePosition();
			View v = listView.getChildAt(0);
			int lengthFromTop = (v == null) ? 0 : v.getTop();
			SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_WORLD_WRITEABLE);
			Editor edit = prefs.edit();
			edit.putInt(LIST_POS, position);
			edit.putInt(LENGTH_FROM_TOP, lengthFromTop);
			edit.commit();
		}
		
		super.onPause();
	}
	
	private class PlaySoundClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> av, View view, int pos, long id) {
			TextView tv = (TextView) view.findViewById(R.id.charListView);
			String word = tv.getText().toString();
			
			//get sound filepath, play
			String fileName = new CharacterDAO(dbh)
				.getFileNameByWord(word);
			new AsyncSoundPlayer().execute(fileName, soundManager);
		}
	}
}
