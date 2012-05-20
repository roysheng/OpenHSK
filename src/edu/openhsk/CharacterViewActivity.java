package edu.openhsk;

import java.util.List;

import edu.openhsk.R;
import edu.openhsk.utils.CharacterDAO;
import edu.openhsk.utils.DatabaseHelper;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class CharacterViewActivity extends Activity {
	private static final String LOG_TAG = "CharacterViewActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.singlechar);
		
		Intent intent = getIntent(); //get char id from DB
		int id = intent.getIntExtra("edu.openhsk.randomindex", 1);
        
		//fetch data from DB
		CharacterDAO dao = new CharacterDAO(new DatabaseHelper(this));
		String[] charData = dao.getCharacterData(id);
		if (charData != null && charData.length > 0) { //display in GUI
			TextView tv = (TextView) findViewById(R.id.charView);
			tv.setText(charData[0]);
			
			TextView tv2 = (TextView) findViewById(R.id.pinyinView);
			tv2.setText(charData[1]);
			
			TextView tv1 = (TextView) findViewById(R.id.descrView);
			tv1.setText(charData[2]);
		} else {
			String errorMsg = "No hanzi found for id: " + id;
			Log.e(LOG_TAG, errorMsg);
			Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
		}
	}

}
