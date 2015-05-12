package com.testify.ecfeed.serialization;

class ValueHolder<T> {
	
	private T value;
	
	ValueHolder(T initialValue) {
		value = initialValue;
	}
	
	public void setValue(T newValue) {
		value = newValue;
	}
	
	public T getValue() {
		return value;
	}
}

class IntHolder extends ValueHolder<Integer> {

	IntHolder(Integer initialValue) {
		super(initialValue);
	}
}

class StringHolder extends ValueHolder<String> {

	StringHolder() {
		super("");
	}
	
	StringHolder(String initialValue) {
		super(initialValue);
	}
}

public class WhiteCharConverter {
	
	class SpecialItem {
		public String decoded;
		public String encoded;
		
		SpecialItem(String decoded, String encoded) {
			this.decoded = decoded;
			this.encoded = encoded;
		}
	}
	
	class SpecialItems {
		
		private static final String BACKSLASH_STR = "\\";
		private static final String DBL_BACKSLASH_STR = "\\\\";
		
		private static final String NEWLINE_STR = "\n";
		private static final String BACKSLASH_N_STR = "\\n";
		
		private static final String TAB_STR = "\t";
		private static final String BACKSLASH_T_STR = "\\t";
		
		private static final String SPACE_STR = " ";
		private static final String BACKSLASH_S_STR = "\\s";
		
		private SpecialItem[] tab = {
			new SpecialItem(BACKSLASH_STR, DBL_BACKSLASH_STR),
			new SpecialItem(NEWLINE_STR, BACKSLASH_N_STR),
			new SpecialItem(TAB_STR, BACKSLASH_T_STR),
			new SpecialItem(SPACE_STR, BACKSLASH_S_STR) 
		};
		
		public int length() {
			return tab.length;
		}
		
		public SpecialItem itemAt(int index) {
			return tab[index];
		}
	}
	
	SpecialItems specialItems = new SpecialItems();

	public String encode(String value) {

		if (value == null)
			return null;
		
		String str = value;
		
		for(int cnt = 0; cnt < specialItems.length(); cnt++) {
			str = str.replace(specialItems.itemAt(cnt).decoded, specialItems.itemAt(cnt).encoded);
		}
		
		return str;
	}
		
	public String decode(String value) {
		
		if (value == null)
			return null;

		SpecialItems specialItems = new SpecialItems();
		StringBuilder builder = new StringBuilder(value);
		
		for(int index = 0; index < builder.length(); index = index + 1) {
			decodeItemAt(index, specialItems, builder);
		}
		
		return builder.toString();
	}
	
	private void decodeItemAt(
							int index, 
							SpecialItems specialItems, 
							StringBuilder builder) {
		
		StringHolder item = new StringHolder();
		StringHolder decodedItem = new StringHolder();
		
		if (isSpecialItemAt(index, specialItems, builder, item, decodedItem)) {
			replaceAt(index, item.getValue(), decodedItem.getValue(), builder);
		}
	}
	
	private boolean isSpecialItemAt(
								int index,
								SpecialItems specialItems,
								StringBuilder builder, 
								StringHolder outItem, 
								StringHolder outDecodedItem) {

		for(int cnt = 0; cnt < specialItems.length(); cnt++) {
			
			SpecialItem specialItem = specialItems.itemAt(cnt);
			
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