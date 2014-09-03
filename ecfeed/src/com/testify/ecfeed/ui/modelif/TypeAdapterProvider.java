package com.testify.ecfeed.ui.modelif;

import static com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_BOOLEAN;
import static com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_BYTE;
import static com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_CHAR;
import static com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_DOUBLE;
import static com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_FLOAT;
import static com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_INT;
import static com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_LONG;
import static com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_SHORT;
import static com.testify.ecfeed.modelif.java.Constants.TYPE_NAME_STRING;

import java.util.Arrays;

import com.testify.ecfeed.modelif.java.JavaUtils;
import com.testify.ecfeed.modelif.java.category.ITypeAdapter;
import com.testify.ecfeed.modelif.java.category.ITypeAdapterProvider;

public class TypeAdapterProvider implements ITypeAdapterProvider{

	private final String USER_TYPE = "USER_TYPE";
	private final String[] TYPES_CONVERTABLE_TO_BOOLEAN = new String[]{
			TYPE_NAME_STRING
	};
	private final String[] TYPES_CONVERTABLE_TO_NUMBERS = new String[]{
			TYPE_NAME_INT, 
			TYPE_NAME_FLOAT, 
			TYPE_NAME_DOUBLE, 
			TYPE_NAME_LONG, 
			TYPE_NAME_SHORT, 
			TYPE_NAME_STRING, 
			TYPE_NAME_BYTE
	};
	private final String[] TYPES_CONVERTABLE_TO_STRING = new String[]{
			TYPE_NAME_INT, 
			TYPE_NAME_FLOAT, 
			TYPE_NAME_DOUBLE, 
			TYPE_NAME_LONG, 
			TYPE_NAME_SHORT, 
			TYPE_NAME_STRING, 
			TYPE_NAME_BYTE,
			TYPE_NAME_CHAR,
			TYPE_NAME_BOOLEAN,
			USER_TYPE
	};
	private final String[] TYPES_CONVERTABLE_TO_USER_TYPE = new String[]{
			TYPE_NAME_STRING 
	};
	private final String[] TYPES_CONVERTABLE_TO_CHAR = new String[]{
			TYPE_NAME_STRING 
	};

	private class BooleanTypeAdapter implements ITypeAdapter{
		@Override
		public boolean compatible(String type){
			return Arrays.asList(TYPES_CONVERTABLE_TO_BOOLEAN).contains(type);
		}

		public String convert(String value){
			if(value.toLowerCase().equals(Constants.BOOLEAN_TRUE_STRING_REPRESENTATION.toLowerCase())){
				return Constants.BOOLEAN_TRUE_STRING_REPRESENTATION;
			}
			else if(value.toLowerCase().equals(Constants.BOOLEAN_FALSE_STRING_REPRESENTATION.toLowerCase())){
				return Constants.BOOLEAN_FALSE_STRING_REPRESENTATION;
			};
			return null;
		}

		@Override
		public String defaultValue() {
			return Constants.DEFAULT_EXPECTED_BOOLEAN_VALUE;
		}
	}

	private class StringTypeAdapter implements ITypeAdapter{
		@Override
		public boolean compatible(String type){
			return Arrays.asList(TYPES_CONVERTABLE_TO_STRING).contains(type);
		}

		public String convert(String value){
			return value;
		}

		@Override
		public String defaultValue() {
			return Constants.DEFAULT_EXPECTED_STRING_VALUE;
		}
	}

	private class UserTypeTypeAdapter implements ITypeAdapter{

		private String fType;

		public UserTypeTypeAdapter(String type){
			fType = type;
		}

		@Override
		public boolean compatible(String type){
			return Arrays.asList(TYPES_CONVERTABLE_TO_USER_TYPE).contains(type);
		}

		public String convert(String value){
			return JavaUtils.isValidJavaIdentifier(value) ? value : null;
		}

		@Override
		public String defaultValue() {
			return new EclipseModelBuilder().getDefaultExpectedValue(fType);
		}
	}

	private class CharTypeAdapter implements ITypeAdapter{
		@Override
		public boolean compatible(String type){
			return Arrays.asList(TYPES_CONVERTABLE_TO_CHAR).contains(type);
		}

		public String convert(String value){
			if(value.length() > 0){
				byte firstByte = value.getBytes()[0]; 
				return String.valueOf(firstByte);
			}
			return null;
		}

		@Override
		public String defaultValue() {
			return Constants.DEFAULT_EXPECTED_CHAR_VALUE;
		}
	}

	private abstract class NumericTypeAdapter implements ITypeAdapter{

		private String[] NUMERIC_SPECIAL_VALUES = new String[]{
				Constants.MAX_VALUE_STRING_REPRESENTATION,
				Constants.MIN_VALUE_STRING_REPRESENTATION
		};

		@Override
		public boolean compatible(String type){
			return Arrays.asList(TYPES_CONVERTABLE_TO_NUMBERS).contains(type);
		}

		@Override
		public String convert(String value){
			return Arrays.asList(NUMERIC_SPECIAL_VALUES).contains(value) ? value : null;
		}

		@Override
		public String defaultValue(){
			return Constants.DEFAULT_EXPECTED_NUMERIC_VALUE;
		}
	}

	private abstract class FloatingPointTypeAdapter extends NumericTypeAdapter{
		private String[] FLOATING_POINT_SPECIAL_VALUES = new String[]{
				Constants.POSITIVE_INFINITY_STRING_REPRESENTATION,
				Constants.NEGATIVE_INFINITY_STRING_REPRESENTATION
		};

		@Override
		public boolean compatible(String type){
			return Arrays.asList(TYPES_CONVERTABLE_TO_NUMBERS).contains(type);
		}

		@Override
		public String convert(String value){
			String result = super.convert(value);
			if(result == null){
				result = Arrays.asList(FLOATING_POINT_SPECIAL_VALUES).contains(value) ? value : null;
			}
			return result;
		}

		@Override
		public String defaultValue(){
			return Constants.DEFAULT_EXPECTED_FLOATING_POINT_VALUE;
		}
	}

	private class FloatTypeAdapter extends FloatingPointTypeAdapter{
		@Override
		public String convert(String value){
			String result = super.convert(value);
			if(result == null){
				try{
					result = String.valueOf(Float.parseFloat(value));
				}
				catch(NumberFormatException e){
					result = null;
				}
			}
			return result;
		}
	}

	private class DoubleTypeAdapter extends FloatingPointTypeAdapter{
		@Override
		public String convert(String value){
			String result = super.convert(value);
			if(result == null){
				try{
					result = String.valueOf(Double.parseDouble(value));
				}
				catch(NumberFormatException e){
					result = null;
				}
			}
			return result;
		}
	}

	private class ByteTypeAdapter extends NumericTypeAdapter{
		@Override
		public String convert(String value){
			String result = super.convert(value);
			if(result == null){
				try{
					result = String.valueOf(Byte.parseByte(value));
				}
				catch(NumberFormatException e){
					result = null;
				}
			}
			return result;
		}
	}

	private class IntTypeAdapter extends NumericTypeAdapter{
		@Override
		public String convert(String value){
			String result = super.convert(value);
			if(result == null){
				try{
					result = String.valueOf(Integer.parseInt(value));
				}
				catch(NumberFormatException e){
					result = null;
				}
			}
			return result;
		}
	}

	private class LongTypeAdapter extends NumericTypeAdapter{
		@Override
		public String convert(String value){
			String result = super.convert(value);
			if(result == null){
				try{
					result = String.valueOf(Long.parseLong(value));
				}
				catch(NumberFormatException e){
					result = null;
				}
			}
			return result;
		}
	}

	private class ShortTypeAdapter extends NumericTypeAdapter{
		@Override
		public String convert(String value){
			String result = super.convert(value);
			if(result == null){
				try{
					result = String.valueOf(Short.parseShort(value));
				}
				catch(NumberFormatException e){
					result = null;
				}
			}
			return result;
		}
	}

	public ITypeAdapter getAdapter(String type){
		if(JavaUtils.isPrimitive(type) == false){
			type = USER_TYPE;
		}
		switch(type){
		case TYPE_NAME_BOOLEAN:
			return new BooleanTypeAdapter();
		case TYPE_NAME_BYTE:
			return new ByteTypeAdapter();
		case TYPE_NAME_CHAR:
			return new CharTypeAdapter();
		case TYPE_NAME_DOUBLE:
			return new DoubleTypeAdapter();
		case TYPE_NAME_FLOAT:
			return new FloatTypeAdapter();
		case TYPE_NAME_INT:
			return new IntTypeAdapter();
		case TYPE_NAME_LONG:
			return new LongTypeAdapter();
		case TYPE_NAME_SHORT:
			return new ShortTypeAdapter();
		case TYPE_NAME_STRING:
			return new StringTypeAdapter();
		default:
			return new UserTypeTypeAdapter(type);
		}
	}
}
