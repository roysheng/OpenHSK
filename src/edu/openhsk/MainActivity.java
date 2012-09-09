package edu.openhsk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.Toast;
import edu.openhsk.utils.CharacterDAO;
import edu.openhsk.utils.CustomHanziParser;
import edu.openhsk.utils.DatabaseHelper;

public class MainActivity extends Activity {
    private static final String LOG_TAG = "MainActivity";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //parse hanzi data
        parseOrFail();
        
        //display list of characters (no menu in future versions?)
        //displayList();
        
        Button b1 = (Button) findViewById(R.id.button1);
        b1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, CharacterViewActivity.class);
				Random rand = new Random();
				i.putExtra("edu.openhsk.randomindex", rand.nextInt(151)+1);
				startActivity(i);
			}
		});
        b1.setOnLongClickListener(new OnLongClickListener() {
			@Override //XXX DEBUG ONLY
			public boolean onLongClick(View v) {
				Intent i = new Intent(MainActivity.this, CharacterViewActivity.class);
				i.putExtra("edu.openhsk.randomindex", 65);
				startActivity(i);
				return true;
			}
		});
        
        Button b2 = (Button) findViewById(R.id.button2);
        b2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, CharacterListActivity.class);
				startActivity(i);
			}
		});
        
        Button b3 = (Button) findViewById(R.id.button3);
        b3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(MainActivity.this, QuizActivity.class);
				startActivity(i);
			}
		});
    }

	private void parseOrFail() { //TODO: break out into asynctask
		try {
			DatabaseHelper dbh = new DatabaseHelper(this);
			CharacterDAO dao = new CharacterDAO(dbh);
			if (dao.getTableStatus() == true) {
				Log.d(LOG_TAG, "No data needed to be loaded");
				return;
			}
        	CustomHanziParser parser = new CustomHanziParser(dao);
        	InputStream inputStream = this.getAssets().open(getString(
        			R.string.filename_hsk1));
        	BufferedReader br = new BufferedReader(new InputStreamReader(
        			inputStream, "UTF8"));
			parser.parseHSK1CSV(br);
		} catch (IOException e) {
			Toast.makeText(this, "Fatal parser error", Toast.LENGTH_LONG);
			e.printStackTrace();
		}
	}
}