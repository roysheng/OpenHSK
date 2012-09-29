package edu.openhsk;

import edu.openhsk.R;
import edu.openhsk.adapters.CharacterListViewBinder;
import edu.openhsk.utils.DatabaseHelper;
import edu.openhsk.utils.ListHelper;
import edu.openhsk.utils.SoundManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class CharacterListActivity extends Activity {
	public static final String LENGTH_FROM_TOP = "lengthFromTop";
	public static final String LIST_POS = "listPos";
	private static final String LOG_TAG = "CharacterListActivity";
	private static final String[] queryColumns = new String[] {"_id", "word", "pinyin", "definition", "islearned"};
	public static final String PREFS_NAME = "edu.openhsk.list.prefs";
	
	private DatabaseHelper dbhelp;
	private SQLiteDatabase db;
	private ListView listView;
	private CharacterListViewBinder viewBinder;
	private SimpleCursorAdapter adapter;
	private Cursor cursor;
	private SoundManager soundManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.charlist);
		
		dbhelp = new DatabaseHelper(this);
		db = dbhelp.getReadableDatabase();
		
		listView = (ListView) findViewById(R.id.charListView);
		cursor = db.query("t_hsk1", queryColumns, "", null, "", "", "");
		refreshList(cursor);
		
		soundManager = new SoundManager(getAssets());
		listView.setOnItemClickListener(new PlaySoundButton());
		listView.setFastScrollEnabled(true);
		
		startManagingCursor(cursor);
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
		dbhelp = new DatabaseHelper(this);
		db = dbhelp.getReadableDatabase();
		
		listView = (ListView) findViewById(R.id.charListView);
		cursor = db.query("t_hsk1", queryColumns, "", null, "", "", "");
		refreshList(cursor);
		
		startManagingCursor(cursor);
		
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
	
	@Override
	protected void onStop() {
		db.close();
		super.onStop();
	}
	
	private class PlaySoundButton implements OnItemClickListener {
		public void onItemClick(AdapterView<?> av, View view, int pos, long id) {
			TextView tv = (TextView) view.findViewById(R.id.charListView);
			String word = tv.getText().toString();
			
			//get sound filepath, play
			String fileName = new ListHelper(CharacterListActivity.this).getFileNameByWord(word);
			new AsyncSoundPlayer().execute(fileName);
			startManagingCursor(cursor);
		}
	}
	
	private class AsyncSoundPlayer extends AsyncTask<Object,Integer,Boolean> {
		@Override
		protected Boolean doInBackground(Object... params) {
			String fileName = (String) params[0];
			Log.d(LOG_TAG, "Playing soundfile " + fileName);
			soundManager.playSoundFile(fileName);
			return true;
		}
	}
}
