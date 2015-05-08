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
	
	private static final String BACKSLASH_STR = "\\";
	private static final String DBL_BACKSLASH_STR = "\\\\";
	private static final String BACKSLASH_S_STR = "\\s";
	private static final String NEWLINE_STR = "\n";
	private static final String BACKSLASH_N_STR = "\\n";
	private static final String SPACE_STR = " ";
	private static final String BACKSLASH_T_STR = "\\t";
	private static final String TAB_STR = "\t";
	
	
	public static String encode(String value) {
		
		System.out.println("XYX encode 01 - value: |" + value + "| length: " + value.length());
		
		String result1 = value.replace(BACKSLASH_STR, DBL_BACKSLASH_STR);
		System.out.println("XYX encode 02 - value: |" + result1 + "| length: " + result1.length());
		
		String result2 = result1.replace(NEWLINE_STR, BACKSLASH_N_STR); 
		System.out.println("XYX encode 03 - value: |" + result2 + "| length: " + result2.length());
		
		String result3 = result2.replace(TAB_STR, BACKSLASH_T_STR); 
		System.out.println("XYX encode 03 - value: |" + result2 + "| length: " + result2.length());
		
		String result4 = result3.replace(SPACE_STR, BACKSLASH_S_STR);
		System.out.println("XYX encode 04 - value: |" + result3 + "| length: " + result3.length());
		
		return result4;		
	}
		
	public static String decode(String value) {

		System.out.println("XYX decode - value: |" + value + "| length: " + value.length());
		
		StringBuilder builder = new StringBuilder(value);
		
		for(int index = 0; index < builder.length(); index = index + 1) {
			decodeItemAt(index, builder);
		}
		
		System.out.println("XYX decode - result: |" + builder.toString() + "|");
		
		return builder.toString();
	}
	
	private static void decodeItemAt(int index, StringBuilder builder) {
		
		StringHolder item = new StringHolder();
		StringHolder decodedItem = new StringHolder();
		
		System.out.println("XYX decodeItemAt 01 - index: " + index + "-----------------------");
		
		if (isSpecialItemAt(index, builder, item, decodedItem)) {
			
			System.out.println("XYX decodeItemAt 02 - special item - item: " + item.getValue() + " decoded item:" + decodedItem.getValue());
			System.out.println("XYX decodeItemAt 03 - builder: |" + builder.toString() + "|");
			
			replaceAt(index, item.getValue(), decodedItem.getValue(), builder);
			
			System.out.println("XYX decodeItemAt 04 - builder: |" + builder.toString() + "|");
		}
	}
	
	private static boolean isSpecialItemAt(
									int index, 
									StringBuilder builder, 
									StringHolder outItem, 
									StringHolder outDecodedItem) {
		
		if (getItemWithDecodeDefAt(index, DBL_BACKSLASH_STR, BACKSLASH_STR, builder, outItem, outDecodedItem)) {
			return true;
		}
		
		if (getItemWithDecodeDefAt(index, BACKSLASH_S_STR, SPACE_STR, builder, outItem, outDecodedItem)) {
			return true;
		}
		
		if (getItemWithDecodeDefAt(index, BACKSLASH_N_STR, NEWLINE_STR, builder, outItem, outDecodedItem)) {
			return true;
		}
		
		if (getItemWithDecodeDefAt(index, BACKSLASH_T_STR, TAB_STR, builder, outItem, outDecodedItem)) {
			return true;
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