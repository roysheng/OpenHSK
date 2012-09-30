package edu.openhsk.utils;

public class PinyinReplacer {

	/**
	 * Replacement lookup table for pinyin tone markers.
	 * DO NOT CHANGE THE ORDER OF THE ROWS!
	 */
	private static final String[][] replacementTable = {
		{"a1","ā"},
		{"a2","á"},
		{"a3","ǎ"},
		{"a4","à"},
		{"a5","a"},
		{"e1","ē"},
		{"e2","é"},
		{"e3","ě"},
		{"e4","è"},
		{"e5","e"},
		{"i1","ī"},
		{"i2","í"},
		{"i3","ǐ"},
		{"i4","ì"},
		{"i5","i"},
		{"o1","ō"},
		{"o2","ó"},
		{"o3","ǒ"},
		{"o4","ò"},
		{"o5","o"},
		{"u1","ū"},
		{"u2","ú"},
		{"u3","ǔ"},
		{"u4","ù"},
		{"u5","u"},
		{"ü1","ǖ"},
		{"ü2","ǘ"},
		{"ü3","ǚ"},
		{"ü4","ǜ"},
		{"ü5","ü"},
		{"an1","ān"},
		{"an2","án"},
		{"an3","ǎn"},
		{"an4","àn"},
		{"an5","an"},
		{"ang1","āng"},
		{"ang2","áng"},
		{"ang3","ǎng"},
		{"ang4","àng"},
		{"ang5","ang"},
		{"en1","ēn"},
		{"en2","én"},
		{"en3","ěn"},
		{"en4","èn"},
		{"en5","en"},
		{"eng1","ēng"},
		{"eng2","éng"},
		{"eng3","ěng"},
		{"eng4","èng"},
		{"eng5","eng"},
		{"in1","īn"},
		{"in2","ín"},
		{"in3","ǐn"},
		{"in4","ìn"},
		{"in5","in"},
		{"ing1","īng"},
		{"ing2","íng"},
		{"ing3","ǐng"},
		{"ing4","ìng"},
		{"ing5","ing"},
		{"ong1","ōng"},
		{"ong2","óng"},
		{"ong3","ǒng"},
		{"ong4","òng"},
		{"ong5","ong"},
		{"un1","ūn"},
		{"un2","ún"},
		{"un3","ǔn"},
		{"un4","ùn"},
		{"un5","un"},
		{"er1","ēr"},
		{"er2","ér"},
		{"er3","ěr"},
		{"er4","èr"},
		{"er5","er"},
		{"r5","er"},
		{"aō","āo"},
		{"aó","áo"},
		{"aǒ","ǎo"},
		{"aò","ào"},
		{"oū","ōu"},
		{"oú","óu"},
		{"oǔ","ǒu"},
		{"où","òu"},
		{"aī","āi"},
		{"aí","ái"},
		{"aǐ","ǎi"},
		{"aì","ài"},
		{"eī","ēi"},
		{"eí","éi"},
		{"eǐ","ěi"},
		{"eì","èi"}
	};

	/**
	 * Replaces numbered tone marks with visual tone marks. 
	 * @param input The pinyin string containing numbered tone marks.
	 * @return The resulting string containing visual tone marks.
	 */
	public static String fixToneMarks(String input) {
		if (input.contains(" ")) {
			String[] strings = input.split("\\s");
			StringBuilder sb = new StringBuilder();
			for (String string : strings) {
				sb.append((" " + replace(string)));
			}
			return sb.toString().trim();
		} else {
			input = replace(input);
		}
		return input;
	}

	private static String replace(String input) {
		for (int i = 0; i < replacementTable.length; i++) {
			if (input.contains(replacementTable[i][0])) {
				input = input.replace(replacementTable[i][0], 
					replacementTable[i][1]);
			}
		}
		return input;
	}

	/**
	 * Removes tone mark numbering from the included pinyin string.
	 * @param input The pinyin string to be modified.
	 * @return The new pinyin string with removed tone mark numbers.
	 */
	public static String removeToneMarks(String input) {
		String output = "";
		output = input.replaceAll("\\d", "");
		return output;
	}
}
