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

class EncodeDecodeItem {
	public String decoded;
	public String encoded;
	
	EncodeDecodeItem(String decoded, String encoded) {
		this.decoded = decoded;
		this.encoded = encoded;
	}
}

class EncodeDecodeTab {
	
	private static final String BACKSLASH_STR = "\\";
	private static final String DBL_BACKSLASH_STR = "\\\\";
	
	private static final String NEWLINE_STR = "\n";
	private static final String BACKSLASH_N_STR = "\\n";
	
	private static final String TAB_STR = "\t";
	private static final String BACKSLASH_T_STR = "\\t";
	
	private static final String SPACE_STR = " ";
	private static final String BACKSLASH_S_STR = "\\s";
	
	private EncodeDecodeItem[] tab = {
		new EncodeDecodeItem(BACKSLASH_STR, DBL_BACKSLASH_STR),
		new EncodeDecodeItem(NEWLINE_STR, BACKSLASH_N_STR),
		new EncodeDecodeItem(TAB_STR, BACKSLASH_T_STR),
		new EncodeDecodeItem(SPACE_STR, BACKSLASH_S_STR) 
	};
	
	public int length() {
		return tab.length;
	}
	
	public EncodeDecodeItem itemAt(int index) {
		return tab[index];
	}
}

public class WhiteCharConverter {

	public static String encode(String value) {
		
		String str = value;
		EncodeDecodeTab tab = new EncodeDecodeTab();
		
		for(int cnt = 0; cnt < tab.length(); cnt++) {
			str = str.replace(tab.itemAt(cnt).decoded, tab.itemAt(cnt).encoded);
		}
		
		return str;
	}
		
	public static String decode(String value) {

		EncodeDecodeTab encodeDecodeTab = new EncodeDecodeTab();
		StringBuilder builder = new StringBuilder(value);
		
		for(int index = 0; index < builder.length(); index = index + 1) {
			decodeItemAt(index, encodeDecodeTab, builder);
		}
		
		return builder.toString();
	}
	
	private static void decodeItemAt(
								int index, 
								EncodeDecodeTab encodeDecodeTab, 
								StringBuilder builder) {
		
		StringHolder item = new StringHolder();
		StringHolder decodedItem = new StringHolder();
		
		if (isSpecialItemAt(index, encodeDecodeTab, builder, item, decodedItem)) {
			replaceAt(index, item.getValue(), decodedItem.getValue(), builder);
		}
	}
	
	private static boolean isSpecialItemAt(
									int index,
									EncodeDecodeTab encodeDecodeTab,
									StringBuilder builder, 
									StringHolder outItem, 
									StringHolder outDecodedItem) {

		for(int cnt = 0; cnt < encodeDecodeTab.length(); cnt++) {
			if (getItemWithDecodeDefAt(
									index, 
									encodeDecodeTab.itemAt(cnt).encoded, 
									encodeDecodeTab.itemAt(cnt).decoded,
									builder, 
									outItem, 
									outDecodedItem)) {
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean getItemWithDecodeDefAt(
								int index,
								String item,
								String decodedItem,
								StringBuilder builder,
								StringHolder outItem, 
								StringHolder outDecodedItem) {
		
		if (builder.indexOf(item, index) == index ) {
			
			outItem.setValue(item);
			outDecodedItem.setValue(decodedItem);
			return true;
		}
		
		return false;
	}
	
	public static void replaceAt(
			int startIndex, 
			String oldString, 
			String newString, 
			StringBuilder builder) {

		int endIndex = startIndex + oldString.length();
		builder.replace(startIndex, endIndex, newString);
	}
}