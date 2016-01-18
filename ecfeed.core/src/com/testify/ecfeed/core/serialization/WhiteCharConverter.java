package com.testify.ecfeed.core.serialization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WhiteCharConverter {

	class StringHolder{

		private String fValue;

		StringHolder(String initialValue) {
			fValue = initialValue;
		}

		StringHolder() {
			this("");
		}

		public void setValue(String newValue) {
			fValue = newValue;
		}

		public String getValue() {
			return fValue;
		}
	}

	class SpecialItem {
		public String decoded;
		public String encoded;

		SpecialItem(String decoded, String encoded) {
			this.decoded = decoded;
			this.encoded = encoded;
		}
	}

	private static final String BACKSLASH_STR = "\\";
	private static final String DBL_BACKSLASH_STR = "\\\\";

	private static final String NEWLINE_STR = "\n";
	private static final String BACKSLASH_N_STR = "\\n";

	private static final String TAB_STR = "\t";
	private static final String BACKSLASH_T_STR = "\\t";

	private static final String SPACE_STR = " ";
	private static final String BACKSLASH_S_STR = "\\s";

	private List<SpecialItem> fSpecialItems = new ArrayList<SpecialItem>(Arrays.asList(new SpecialItem[]{
			//Do not change the order (double backslash must be first)
		new SpecialItem(BACKSLASH_STR, DBL_BACKSLASH_STR),
		new SpecialItem(NEWLINE_STR, BACKSLASH_N_STR),
		new SpecialItem(TAB_STR, BACKSLASH_T_STR),
		new SpecialItem(SPACE_STR, BACKSLASH_S_STR)
	}));

	public String encode(String value) {

		if (value == null)
			return null;

		String str = value;

		for(SpecialItem item : fSpecialItems){
			str = str.replace(item.decoded, item.encoded);
		}

		return str;
	}

	public String decode(String value) {

		if (value == null)
			return null;

		StringBuilder builder = new StringBuilder(value);

		for(int index = 0; index < builder.length(); index = index + 1) {
			decodeItemAt(index, builder);
		}

		return builder.toString();
	}

	private void decodeItemAt(int index, StringBuilder builder) {

		StringHolder item = new StringHolder();
		StringHolder decodedItem = new StringHolder();

		if (isSpecialItemAt(index, builder, item, decodedItem)) {
			replaceAt(index, item.getValue(), decodedItem.getValue(), builder);
		}
	}

	private boolean isSpecialItemAt(int index,StringBuilder builder,
			StringHolder outItem, StringHolder outDecodedItem) {

		for(SpecialItem specialItem : fSpecialItems){
			if (isItemAt(index, specialItem.encoded, builder)) {
				outItem.setValue(specialItem.encoded);
				outDecodedItem.setValue(specialItem.decoded);
				return true;
			}
		}

		return false;
	}

	private boolean isItemAt(int index, String item, StringBuilder builder) {

		if (builder.indexOf(item, index) == index ) {
			return true;
		}

		return false;
	}

	private void replaceAt(
			int startIndex,
			String oldString,
			String newString,
			StringBuilder builder) {

		int endIndex = startIndex + oldString.length();
		builder.replace(startIndex, endIndex, newString);
	}
}