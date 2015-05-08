package com.testify.ecfeed.serialization;

class ValueHolder<T> {
	
	private T value;
	
	ValueHolder(T initialValue) {
		value = initialValue;
	}
	
	public void set(T newValue) {
		value = newValue;
	}
	
	public T get() {
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
	
	public static String encode(String value) {
		
		String result1 = value.replace(BACKSLASH_STR, DBL_BACKSLASH_STR);
		String result2 = result1.replace(" ", BACKSLASH_S_STR);
		
		return result2;		
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
			
			System.out.println("XYX decodeItemAt 02 - special item - item: " + item.get() + " decoded item:" + decodedItem.get());
			System.out.println("XYX decodeItemAt 03 - builder: |" + builder.toString() + "|");
			
			replaceAt(index, item.get(), decodedItem.get(), builder);
			
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
		
		if (getItemWithDecodeDefAt(index, BACKSLASH_S_STR, " ", builder, outItem, outDecodedItem)) {
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
			
			outItem.set(item);
			outDecodedItem.set(decodedItem);
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