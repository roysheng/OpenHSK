package edu.openhsk;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
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
import edu.openhsk.data.QuizHanzi;
import edu.openhsk.utils.AsyncSoundPlayer;
import edu.openhsk.utils.CharacterDAO;
import edu.openhsk.utils.DatabaseHelper;
import edu.openhsk.utils.QuizHelper;
import edu.openhsk.utils.SoundManager;

public class QuizActivity extends Activity {
	public static final String PREFS_NAME = "edu.openhsk.quiz.prefs";
	public static final String IS_CACHED = "isCached";
	private static final String ID_OF_ANSWER = "idOfAnswer";
	private static final String LOG_TAG = "QuizActivity";
	private static final String PINYIN_SHOWN = "pinyinShown";
	private static final String CORRECT_ANSWER_SHOWN = "correctAnswerShown";

	private final static int idArray[] = new int[] {R.id.defView0, R.id.defView1, R.id.defView2, R.id.defView3};
	private TextView quizWordView;
	private Button[] buttonArray;
	private QuizHelper quizHelper;
	private TextView quizPinyinView;

	private SoundManager soundManager;
	private int idOfAnswer; //the Hanzi id of the answer
	private boolean correctAnswerShown = true;
	private boolean pinyinShown = true;
	private int answerButtonIndex = -1; //the correct answer button array index

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quiz);
		
		//generate new or recover cached quiz and answer
		List<QuizHanzi> quizWordList = new ArrayList<QuizHanzi>(4);	
		int indexOfAnswer = generateQuiz(quizWordList, checkCache());
		
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_WORLD_READABLE);
		pinyinShown = prefs.getBoolean(PINYIN_SHOWN, true);
		correctAnswerShown = prefs.getBoolean(CORRECT_ANSWER_SHOWN, true);
		
		//display quiz
		displayQuiz(quizWordList, indexOfAnswer);
		
		soundManager = new SoundManager(getAssets());
	}

	private int generateQuiz(List<QuizHanzi> quizWordList, boolean isCached) {
		if (quizHelper == null) {
				quizHelper = new QuizHelper(this);
		}
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
	
	private boolean checkCache() {
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_WORLD_READABLE);
		boolean isCached = prefs.getBoolean(IS_CACHED, false);
		idOfAnswer = prefs.getInt(ID_OF_ANSWER, -1);
		Log.d(LOG_TAG, "isCached: " + isCached + 
				" idOfAnswer: " + idOfAnswer);
		return isCached;
	}

	private void updateCache(List<QuizHanzi> list) {
		quizHelper.cacheQuiz(list);
		SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_WORLD_WRITEABLE);
		Editor editor = prefs.edit();
		editor.putInt(ID_OF_ANSWER, idOfAnswer);
		editor.putBoolean(IS_CACHED, true);
		editor.commit();
	}

	/**
	 * Initialization and display of the quiz in the UI. This method is 
	 * only called at the start of the activity. 
	 * @param quizWordList the generated list of words included in the quiz
	 * @param indexOfAnswer the index of the correct word in the quizWordList
	 */
	private void displayQuiz(List<QuizHanzi> quizWordList, int indexOfAnswer) {
		PlaySoundClickListener onHanziClickListener = new PlaySoundClickListener(this);
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
			if (id == idOfAnswer) {
				answerButtonIndex = i;
			}
		}
	}

	/**
	 * Resets the UI and displays a new quiz.
	 */
	private void resetQuiz() {
		List<QuizHanzi> quizWordList = new ArrayList<QuizHanzi>(4);
		int indexOfAnswer = generateQuiz(quizWordList, false);
		
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
			if (id == idOfAnswer) {
				answerButtonIndex = i;
			}
		}
	}

	private class OnQuizAnswerListener implements OnClickListener {
		private final int id;
		
		public OnQuizAnswerListener(int id) {
			this.id = id;
		}
		
		public void onClick(View view) {
			if (id == idOfAnswer) { //correct answer
				new AsyncColorSwitcher((Button) view, null).execute((Object[])null);
			} else { //wrong answer
				Button correctButton = buttonArray[answerButtonIndex];
				Button incorrectButton = (Button) view;
				new AsyncColorSwitcher(correctButton, incorrectButton).execute((Object[])null);
			}
		}
	}
	
	private class AsyncColorSwitcher extends AsyncTask<Object,Integer,Boolean> {
		private final Button correctButton;
		private final Button incorrectButton;
		
		public AsyncColorSwitcher(Button correctButton, Button incorrectButton) {
			this.correctButton = correctButton;
			this.incorrectButton = incorrectButton;
		}
		
		@Override
		protected void onPreExecute() {
			if (incorrectButton != null) { //wrong answer chosen
				incorrectButton.setBackgroundColor(Color.RED);
				if (correctAnswerShown) { //show correct answer only if set in preferences
					correctButton.setBackgroundColor(Color.GREEN);
				}
			} else { //correct answer chosen
				correctButton.setBackgroundColor(Color.GREEN);
			}
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
	        case R.id.show_correct_answer: {
	        	SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_WORLD_WRITEABLE);
	        	boolean isCorrectAnswerShown = prefs.getBoolean(CORRECT_ANSWER_SHOWN, true);
	        	Editor editor = prefs.edit();
	            if (isCorrectAnswerShown == true) {
	            	correctAnswerShown = false;
	            	editor.putBoolean(CORRECT_ANSWER_SHOWN, false);
	            } else {
	            	correctAnswerShown = true;
	            	editor.putBoolean(CORRECT_ANSWER_SHOWN, true);
	            }
	            editor.commit();
	            return true;
	        }
	        case R.id.enable_pinyin: {
	        	SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_WORLD_WRITEABLE);
	        	boolean isPinyinShown = prefs.getBoolean(PINYIN_SHOWN, true);
	        	Editor editor = prefs.edit();
	            if (isPinyinShown == true) {
	            	pinyinShown = false;
	            	editor.putBoolean(PINYIN_SHOWN, false);
	            	quizPinyinView.setVisibility(View.GONE);
	            } else {
	            	pinyinShown = true;
	            	editor.putBoolean(PINYIN_SHOWN, true);
	            	quizPinyinView.setVisibility(View.VISIBLE);
	            }
	            editor.commit();
	            return true;
	        }
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private class PlaySoundClickListener implements OnClickListener {
		private final Context context;
		
		public PlaySoundClickListener(Context context) {
			this.context = context;
		}
		
		@Override
		public void onClick(View v) {
			TextView tv = (TextView) findViewById(R.id.quizWordView);
			String word = tv.getText().toString();
			
			String fileName = new CharacterDAO(
					new DatabaseHelper(context))
						.getFileNameByWord(word);
			new AsyncSoundPlayer().execute(fileName, soundManager);
		}
	}
}
