package edu.openhsk;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import edu.openhsk.data.QuizHanzi;
import edu.openhsk.utils.QuizHelper;

public class QuizActivity extends Activity {
	public static final String LOG_TAG = "QuizActivity";
	private int idOfAnswer;
	private final static int idArray[] = new int[] {R.id.defView0, R.id.defView1, R.id.defView2, R.id.defView3};
	private TextView quizWordView;
	private Button[] buttonArray;
	private QuizHelper quizHelper;
	private TextView quizPinyinView;
	private boolean correctAnswerShown = false;
	private boolean pinyinShown = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quiz);
		
		quizHelper = new QuizHelper(this);
		List<QuizHanzi> quizWordList = quizHelper.makeQuizList();
		idOfAnswer = quizHelper.chooseCorrectAnswer(quizWordList);
		int indexOfAnswer = -1;
		for (int i = 0; i < quizWordList.size(); i++) {
			if (quizWordList.get(i).getId() == idOfAnswer) {
				indexOfAnswer = i;
				break;
			}
		}
		
		quizWordView = (TextView) findViewById(R.id.quizWordView);
		quizWordView.setText(quizWordList.get(indexOfAnswer).getWord());
		quizPinyinView = (TextView) findViewById(R.id.pinyinLabel);
		quizPinyinView.setText(quizWordList.get(indexOfAnswer).getPinyin());
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
		List<QuizHanzi> quizWordList = quizHelper.makeQuizList();
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
	}

	private class OnQuizAnswerListener implements OnClickListener {
		private final int id;
		
		public OnQuizAnswerListener(int id) {
			this.id = id;
		}
		
		@Override
		public void onClick(View view) {
			if (id == idOfAnswer) { //correct answer
				new AsyncColorSwitcher((Button) view, Color.GREEN).execute((Object[])null);
			} else { //wrong answer
				new AsyncColorSwitcher((Button) view, Color.RED).execute((Object[])null);
				//TODO color correct answer green
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
}
