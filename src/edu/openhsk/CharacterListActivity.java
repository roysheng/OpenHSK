package edu.openhsk;

import edu.openhsk.R;
import edu.openhsk.adapters.CharacterListViewBinder;
import edu.openhsk.utils.DatabaseHelper;
import edu.openhsk.utils.SoundManager;
import android.app.Activity;
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
import android.widget.Toast;

public class CharacterListActivity extends Activity {
	private static final String LOG_TAG = "CharacterListActivity";
	private static final String[] queryColumns = new String[] {"_id", "word", "pinyin", "definition", "islearned"};

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
		
		soundManager = new SoundManager(getAssets(), dbhelp);
		listView.setOnItemClickListener(new PlaySoundButton());
		
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
		
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		db.close();
		super.onStop();
	}
	
	private class PlaySoundButton implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> av, View view, int pos,
				long id) {
			//query db for sound filepath
			TextView tv = (TextView) view.findViewById(R.id.charListView);
			String string = tv.getText().toString();
			Cursor cursor = db.query("t_hsk1", new String[] {"_id","soundfile"}, "word = ?", new String[] {string}, null, null, null);
			
			//play sound from file
			if (cursor.moveToFirst()) {
				String fileName = cursor.getString(1);
				Toast.makeText(CharacterListActivity.this, fileName, Toast.LENGTH_SHORT).show();
				Log.d(LOG_TAG, "id: " + pos + " String: " + string + " file: " + fileName);
				AsyncTask asyncPlayer = new AsyncSoundPlayer(fileName);
				asyncPlayer.execute(null);
			}
		}
	}
	
	private class AsyncSoundPlayer extends AsyncTask<Object,Integer,Boolean> {
		private final String fileName;
		
		public AsyncSoundPlayer(String fileName) {
			this.fileName = fileName;
		}
		
		@Override
		protected void onPreExecute() {
			//super.onPreExecute();
			Log.d(LOG_TAG,"Pre playback");
		}
		
		@Override
		protected Boolean doInBackground(Object... params) {
			soundManager.playSoundFileByName(fileName);
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			//super.onPostExecute(result);
			Log.d(LOG_TAG,"Post playback");
		}
		
	}
}
