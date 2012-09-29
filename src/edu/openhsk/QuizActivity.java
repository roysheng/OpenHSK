package edu.openhsk;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import edu.openhsk.data.QuizHanzi;
import edu.openhsk.utils.QuizHelper;

public class QuizActivity extends Activity {
	public static final String PREFS_NAME = "edu.openhsk.quiz.prefs";
	public static final String IS_CACHED = "isCached";
	private static final String ID_OF_ANSWER = "idOfAnswer";
	private static final String LOG_TAG = "QuizActivity";

	private final static int idArray[] = new int[] {R.id.defView0, R.id.defView1, R.id.defView2, R.id.defView3};
	private TextView quizWordView;
	private Button[] buttonArray;
	private QuizHelper quizHelper;
	private TextView quizPinyinView;

	private int idOfAnswer;
	private boolean correctAnswerShown = false;
	private boolean pinyinShown = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quiz);
		
		//generate new or recover cached quiz and answer
		List<QuizHanzi> quizWordList = new ArrayList<QuizHanzi>(4);	
		int indexOfAnswer = generateQuiz(quizWordList);
		
		//display quiz
		displayQuiz(quizWordList, indexOfAnswer);
	}

	private int generateQuiz(List<QuizHanzi> quizWordList) {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_WORLD_READABLE);
		boolean isCached = settings.getBoolean(IS_CACHED, false);
		idOfAnswer = settings.getInt(ID_OF_ANSWER, -1);
		Log.d(LOG_TAG, "isCached: " + isCached + 
				" idOfAnswer: " + idOfAnswer);
		
		quizHelper = new QuizHelper(this);
		List<QuizHanzi> list = null;
		if (isCached == true) { //cached quiz
			list = quizHelper.makeQuizList(isCached);
			if (list == null) { //if list is null, invalidate cache
				quizHelper.invalidateCache();
				isCached = false;
			}
		}
		if (isCached == false) { //new quiz			
			list = quizHelper.makeQuizList(isCached);
			idOfAnswer = quizHelper.chooseCorrectAnswer(list);
			updateCache(list);
		}
		
		int indexOfAnswer = -1;
		quizWordList.addAll(list);
		for (int i = 0; i < quizWordList.size(); i++) {
			if (quizWordList.get(i).getId() == idOfAnswer) {
				indexOfAnswer = i;
				break;
			}
		}
		
		return indexOfAnswer;
	}

	private void updateCache(List<QuizHanzi> list) {
		quizHelper.cacheQuiz(list);
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_WORLD_WRITEABLE);
		Editor editor = prefs.edit();
		editor.putInt(ID_OF_ANSWER, idOfAnswer);
		editor.putBoolean(IS_CACHED, true);
		editor.commit();
	}

	private void displayQuiz(List<QuizHanzi> quizWordList, int indexOfAnswer) {
		OnHanziClickListener onHanziClickListener = new OnHanziClickListener();
		quizWordView = (TextView) findViewById(R.id.quizWordView);
		quizWordView.setText(quizWordList.get(indexOfAnswer).getWord());
		quizWordView.setOnClickListener(onHanziClickListener);
		quizPinyinView = (TextView) findViewById(R.id.pinyinLabel);
		quizPinyinView.setText(quizWordList.get(indexOfAnswer).getPinyin());
		quizPinyinView.setOnClickListener(onHanziClickListener);
		if (pinyinShown) {
			quizPinyinView.setVisibility(View.VISIBLE);
		}
		buttonArray = new Button[idArray.length];
		for (int i = 0; i < idArray.length; i++) {
			Button defButton = (Button) findViewById(idArray[i]);
			buttonArray[i] = defButton;
			defButton.setText(quizWordList.get(i).getDefinition());
			defButton.setBackgroundResource(R.drawable.btn_recolored);
			int id = quizWordList.get(i).getId();
			defButton.setOnClickListener(new OnQuizAnswerListener(id));
		}
	}

	private void resetQuiz() {
		List<QuizHanzi> quizWordList = quizHelper.makeQuizList(false);
		idOfAnswer = quizHelper.chooseCorrectAnswer(quizWordList);
		
		int indexOfAnswer = -1;
		for (int i = 0; i < quizWordList.size(); i++) {
			if (quizWordList.get(i).getId() == idOfAnswer) {
				indexOfAnswer = i;
				break;
			}
		}
		
		quizWordView.setText(quizWordList.get(indexOfAnswer).getWord());
		quizPinyinView.setText(quizWordList.get(indexOfAnswer).getPinyin());
		if (pinyinShown) {
			quizPinyinView.setVisibility(View.VISIBLE);
		}
		for (int i = 0; i < idArray.length; i++) {
			buttonArray[i].setText(quizWordList.get(i).getDefinition());
			buttonArray[i].setBackgroundResource(R.drawable.btn_recolored);
			int id = quizWordList.get(i).getId();
			buttonArray[i].setOnClickListener(new OnQuizAnswerListener(id));
		}
		
		updateCache(quizWordList);
	}

	private class OnQuizAnswerListener implements OnClickListener {
		private final int id;
		
		public OnQuizAnswerListener(int id) {
			this.id = id;
		}
		
		public void onClick(View view) {
			if (id == idOfAnswer) { //correct answer
				new AsyncColorSwitcher((Button) view, Color.GREEN).execute((Object[])null);
			} else { //wrong answer
				new AsyncColorSwitcher((Button) view, Color.RED).execute((Object[])null);
				//TODO color correct answer green when wrong answer is given
			}
		}
	}
	
	private class AsyncColorSwitcher extends AsyncTask<Object,Integer,Boolean> {
		private final Button button;
		private final int color;

		public AsyncColorSwitcher(Button view, int color) {
			this.button = view;
			this.color = color;
		}
		
		@Override
		protected void onPreExecute() {
			button.setBackgroundColor(color);
		}
		
		@Override
		protected Boolean doInBackground(Object... arg0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			resetQuiz();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.quizmenu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.show_correct_answer:
	            if (correctAnswerShown == true) {
	            	correctAnswerShown = false;
	            } else {
	            	correctAnswerShown = true;
	            }
	            return true;
	        case R.id.enable_pinyin:
	        	//TODO lagra i sharedpref's
	            if (pinyinShown) {
	            	pinyinShown = false;
	            	quizPinyinView.setVisibility(View.GONE);
	            } else {
	            	pinyinShown = true;
	            	quizPinyinView.setVisibility(View.VISIBLE);
	            }
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private class OnHanziClickListener implements OnClickListener {
		@Override
		public void onClick(View v) { //TODO finish sound playback code
			//get current quiz word
			
			//get soundfilename
			
			//initialize asyncsoundplayer and play pronunciation
			
		}
	}
}
