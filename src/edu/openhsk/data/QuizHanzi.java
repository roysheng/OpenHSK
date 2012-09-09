package edu.openhsk.data;

public class QuizHanzi {
	private final int id;
	private final String word;
	private final String pinyin;
	private final String definition;
	private final String soundfile;

	public QuizHanzi(int id, String word, String pinyin, 
			String definition, String soundfile) {
		this.id = id;
		this.word = word;
		this.pinyin = pinyin;
		this.definition = definition;
		this.soundfile = soundfile;
	}
	
	public int getId() {
		return id;
	}

	public String getWord() {
		return word;
	}

	public String getPinyin() {
		return pinyin;
	}

	public String getDefinition() {
		return definition;
	}

	public String getSoundfile() {
		return soundfile;
	}
}
