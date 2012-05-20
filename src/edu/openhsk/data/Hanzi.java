package edu.openhsk.data;

import android.content.ContentValues;

public class Hanzi {
	private String word; // the chinese character(s) in UTF-8
	private String pinyin; // the pronunciation in alphabet, with tone marks
	private String definition; // the meaning of the character(s) in english
	private String searchKey; // this is used for text searching by pinyin (no tones)
	private boolean isLearned; // a user set variable to track learned characters
	private int strokes; // the number of strokes used to write the chinese character
	private String soundfile; //the filename of the pronunciation recording

	public Hanzi() {
		// this ctor intentionally left empty
	}

	/**
	 * Constructor for use when parsing character data in CSV format.
	 */
	public Hanzi(String word, String pinyin, String definition, String searchKey) {
		this.word = word;
		this.pinyin = pinyin;
		this.definition = definition;
		this.strokes = 0;
		this.searchKey = searchKey;
		this.isLearned = false;
		this.soundfile = "";
	}

	/**
	 * Constructor for use when deserializing character data from the database.
	 */
	public Hanzi(String word, String pinyin, String definition, 
			boolean isLearned, int strokes, String soundfile) {
		this.word = word;
		this.pinyin = pinyin;
		this.definition = definition;
		this.isLearned = isLearned;
		this.strokes = strokes;
		this.soundfile = soundfile;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	public boolean isLearned() {
		return isLearned;
	}

	public void setIsLearned(boolean learned) {
		this.isLearned = learned;
	}

	public int getStrokes() {
		return strokes;
	}

	public void setStrokes(int strokes) {
		this.strokes = strokes;
	}
	
	public String getSoundfile() {
		return soundfile;
	}

	public void setSoundfile(String soundfile) {
		this.soundfile = soundfile;
	}

	@Override
	public String toString() {
		return word + "," + pinyin + "," + definition + "," + searchKey + ","
				+ isLearned + "," + strokes + "," + soundfile;
	}

	public ContentValues toContentValues() {
		ContentValues cv = new ContentValues(7);
		cv.put("word", word);
		cv.put("pinyin", pinyin);
		cv.put("definition", definition);
		cv.put("searchkey", searchKey);
		if (isLearned) {
			cv.put("islearned", 1);
		} else {
			cv.put("islearned", 0);
		}
		cv.put("strokes", strokes);
		cv.put("soundfile", soundfile);
		return cv;
	}

}
