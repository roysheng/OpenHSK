package edu.openhsk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import edu.openhsk.utils.CharacterDAO;
import edu.openhsk.utils.CustomHanziParser;
import edu.openhsk.utils.DatabaseHelper;

public class MainActivity extends Activity {
    private static final String LOG_TAG = "MainActivity";
	private DatabaseHelper dbh;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        dbh = new DatabaseHelper(this);
        CharacterDAO dao = new CharacterDAO(dbh);
		if (dao.getTableStatus() == true) {
			Log.d(LOG_TAG, "No data needed to be loaded");
			LinearLayout foreground = (LinearLayout) findViewById(R.id.main_menu_layout);
			foreground.setVisibility(View.VISIBLE);
		} else {
			new AsyncParser().execute((Object[])null);
		}
        
        //display list of characters (no menu in future versions?)
        //displayList();
        
        Button charButton = (Button) findViewById(R.id.button1);
        charButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, CharacterViewActivity.class);
				Random rand = new Random();
				i.putExtra("edu.openhsk.randomindex", rand.nextInt(151)+1);
				startActivity(i);
			}
		});
        charButton.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) { //XXX DEBUG ONLY
				Intent i = new Intent(MainActivity.this, CharacterViewActivity.class);
				i.putExtra("edu.openhsk.randomindex", 65);
				startActivity(i);
				return true;
			}
		});
        
        Button listButton = (Button) findViewById(R.id.button2);
        listButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//reset saved list position
				SharedPreferences prefs = getPreferences(MODE_WORLD_WRITEABLE);
				Editor edit = prefs.edit();
				edit.putInt("listPos", 0);
				edit.putInt("lengthFromTop", 0);
				edit.commit();
				
				Intent i = new Intent(MainActivity.this, CharacterListActivity.class);
				startActivity(i);
			}
		});
        
        Button quizButton = (Button) findViewById(R.id.button3);
        quizButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Editor editor = getSharedPreferences(QuizActivity.PREFS_NAME, MODE_WORLD_WRITEABLE).edit();
				editor.putBoolean(QuizActivity.IS_CACHED, false);
				editor.commit();
				
				Intent i = new Intent(MainActivity.this, QuizActivity.class);
				startActivity(i);
			}
		});
    }

	private void parseOrFail() {
		try {
			CharacterDAO dao = new CharacterDAO(dbh);
        	CustomHanziParser parser = new CustomHanziParser(dao);
        	InputStream inputStream = this.getAssets().open(getString(
        			R.string.filename_hsk1));
        	BufferedReader br = new BufferedReader(new InputStreamReader(
        			inputStream, "UTF8"));
			parser.parseHSK1CSV(br);
		} catch (IOException e) {
			Toast.makeText(this, "Fatal parser error", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
	
	private class AsyncParser extends AsyncTask<Object,Integer,Boolean> {
		private ProgressDialog dialog;
		
		public AsyncParser() {
			dialog = new ProgressDialog(MainActivity.this);
		}
		
		@Override
		protected void onPreExecute() {
			this.dialog.setMessage("Installing dictionaries, do not interrupt...");
			this.dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			this.dialog.show();
		}
		
		@Override
		protected Boolean doInBackground(Object... params) {
			parseOrFail();
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if (dialog.isShowing()) {
	            dialog.dismiss();
	        }
			
			LinearLayout foreground = (LinearLayout) findViewById(R.id.main_menu_layout);
			foreground.setVisibility(View.VISIBLE);
		}
		
	}
}