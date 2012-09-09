package edu.openhsk.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import edu.openhsk.data.Hanzi;


import android.util.Log;

public class CustomHanziParser {
	private static final String LOG_TAG = CustomHanziParser.class.getName();
	private CharacterDAO dao;

	public CustomHanziParser(CharacterDAO dao) {
		this.dao = dao;
	}

	public void parseHSK1CSV(BufferedReader br) {
		try {
			br.readLine(); //skip first line (file metadata)
			int lineNumber = 2;
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				
				Hanzi h = null;
				StringTokenizer st = new StringTokenizer(line,",");
				int i = 0;
				String word = null, pinyin = null, definition = null;
				String searchKey = null, soundfile = null;
				int strokes = 0;
				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					switch (i) {
					case 0: //order
						break;
					case 1: //hsk level-order
						break;
					case 2: //word
						word = token;
						break;
					case 3: //pronunc
						pinyin = PinyinReplacer.fixToneMarks(token);
						searchKey = PinyinReplacer.removeToneMarks(token.toLowerCase());
						break;
					case 4: //definition
						definition = token.replace(";", ",");
						break;
					case 5: //soundfile
						soundfile = token;
						break;
					case 6: //strokes
						strokes = Integer.parseInt(token);
						break;
					default:
						Log.d(LOG_TAG, "Token not in recognized group: " + token);
						break;
					}
					i++;
				}
				h = new Hanzi(word, pinyin, definition, searchKey);
				h.setStrokes(strokes);
				h.setSoundfile(soundfile);
				lineNumber++;
				dao.insertCharacterIntoDB(h);
				Log.d(LOG_TAG, lineNumber + ": " + h.toString());
			}
			Log.d(LOG_TAG, "Characters read from file: " + lineNumber);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			dao.closeDB();
		}
	}
	
	public void parseHSK2CSV(BufferedReader br) { //TODO finish parser
		try {
			int lineNumber = 1;
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				
				Hanzi h = null;
				StringTokenizer st = new StringTokenizer(line,",");
				int i = 0;
				String word = null, pinyin = null, definition = null;
				String searchKey = null, soundfile = null;
				int strokes = 0;
				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					switch (i) {
					case 0: //order
						break;
					case 1: //hsk level-order
						break;
					case 2: //word
						word = token;
						break;
					case 3: //pronunc
						pinyin = PinyinReplacer.fixToneMarks(token);
						searchKey = PinyinReplacer.removeToneMarks(token.toLowerCase());
						break;
					case 4: //definition
						definition = token.replace(";", ",");
						break;
					case 5: //soundfile
						soundfile = token;
						break;
					case 6: //strokes
						strokes = Integer.parseInt(token);
						break;
					default:
						Log.d(LOG_TAG, "Token not in recognized group: " + token);
						break;
					}
					i++;
				}
				h = new Hanzi(word, pinyin, definition, searchKey);
				h.setStrokes(strokes);
				h.setSoundfile(soundfile);
				lineNumber++;
				dao.insertCharacterIntoDB(h);
				Log.d(LOG_TAG, lineNumber + ": " + h.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			dao.closeDB();
		}
	}

}
