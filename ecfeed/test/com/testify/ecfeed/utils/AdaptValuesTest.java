package com.testify.ecfeed.utils;

import static com.testify.ecfeed.model.Constants.TYPE_NAME_BOOLEAN;
import static com.testify.ecfeed.model.Constants.TYPE_NAME_BYTE;
import static com.testify.ecfeed.model.Constants.TYPE_NAME_CHAR;
import static com.testify.ecfeed.model.Constants.TYPE_NAME_DOUBLE;
import static com.testify.ecfeed.model.Constants.TYPE_NAME_FLOAT;
import static com.testify.ecfeed.model.Constants.TYPE_NAME_INT;
import static com.testify.ecfeed.model.Constants.TYPE_NAME_LONG;
import static com.testify.ecfeed.model.Constants.TYPE_NAME_SHORT;
import static com.testify.ecfeed.model.Constants.TYPE_NAME_STRING;
import static com.testify.ecfeed.utils.AdaptTypeSupport.adaptValueToType;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AdaptValuesTest{

	public enum EnumType{
		value1, value2;
	}

	@Test
	public void adaptValueToTypeTest(){
		String type;
		Object value;

		type = TYPE_NAME_BOOLEAN;
		// from boolean
		assertEquals(true, adaptValueToType(true, type));
		assertEquals(false, adaptValueToType(false, type));
		// from byte
		value= (byte)123;
		assertEquals(true, adaptValueToType(value, type));
		assertEquals(false, adaptValueToType((byte)0, type));
		// from short
		value=(short)1231;
		assertEquals(true, adaptValueToType(value, type));
		assertEquals(false, adaptValueToType((short)0, type));
		// from integer
		value = (int)12312312;
		assertEquals(true, adaptValueToType(value, type));
		assertEquals(false, adaptValueToType((int)0, type));
		// from long
		value = (long)((long)1231231231 * (long)12);
		assertEquals(true, adaptValueToType(value, type));
		assertEquals(false, adaptValueToType((long)0, type));
		// from float
		value = 123.05f;
		assertEquals(null, adaptValueToType(value, type));
		assertEquals(null, adaptValueToType((float)0, type));
		// from double
		value = 123.12d;
		assertEquals(null, adaptValueToType(value, type));
		assertEquals(null, adaptValueToType((double)0, type));
		// from char
		assertEquals(null, adaptValueToType('d', type));
		assertEquals(null, adaptValueToType('\0', type));
		// from String
		assertEquals(true, adaptValueToType("true", type));
		assertEquals(false, adaptValueToType("", type));
		assertEquals(false, adaptValueToType("f", type));
		// from default
		value = EnumType.value1;
		assertEquals(null, adaptValueToType(value, type));

		type = TYPE_NAME_BYTE;
		// from boolean
		assertEquals((byte)1, adaptValueToType(true, type));
		assertEquals((byte)0, adaptValueToType(false, type));
		// from byte
		value = (byte)127;
		assertEquals((byte)value, adaptValueToType(value, type));
		// from short
		value = (short)1231;
		assertEquals((byte)(short)value, adaptValueToType(value, type));
		// from integer
		value = (int)12312312;
		assertEquals((byte)(int)value, adaptValueToType(value, type));
		// from long
		value = (long)((long)1231231231 * (long)12);
		assertEquals((byte)(long)value, adaptValueToType(value, type));
		// from float
		value = 123.05f;
		assertEquals((byte)(float)value, adaptValueToType(value, type));
		// from double
		value = 123.05d;
		assertEquals((byte)(double)value, adaptValueToType(value, type));
		// from char
		value = 'd';
		assertEquals((byte)(char)value, adaptValueToType(value, type));
		value = '\0';
		assertEquals((byte)(char)value, adaptValueToType(value, type));
		// from String
		value = "122";
		assertEquals((byte)122, adaptValueToType(value, type));
		value = "21.2";
		assertEquals(null, adaptValueToType(value, type));
		// from default
		value = EnumType.value1;
		assertEquals(null, adaptValueToType(value, type));

		type = TYPE_NAME_SHORT;
		// from boolean
		assertEquals((short)1, adaptValueToType(true, type));
		assertEquals((short)0, adaptValueToType(false, type));
		// from short
		value = (byte)127;
		assertEquals((short)(byte)value, adaptValueToType(value, type));
		// from short
		value = (short)1231;
		assertEquals(value, adaptValueToType(value, type));
		// from integer
		value = (int)12312312;
		assertEquals((short)(int)value, adaptValueToType(value, type));
		// from long
		value = (long)((long)1231231231 * (long)12);
		assertEquals((short)(long)value, adaptValueToType(value, type));
		// from float
		value = 123.05f;
		assertEquals((short)(float)value, adaptValueToType(value, type));
		// from double
		value = 123.05d;
		assertEquals((short)(double)value, adaptValueToType(value, type));
		// from char
		value = 'd';
		assertEquals((short)(char)value, adaptValueToType(value, type));
		value = '\0';
		assertEquals((short)(char)value, adaptValueToType(value, type));
		// from String
		value = "122";
		assertEquals((short)122, adaptValueToType(value, type));
		value = "21.2";
		assertEquals(null, adaptValueToType(value, type));
		// from default
		value = EnumType.value1;
		assertEquals(null, adaptValueToType(value, type));

		type = TYPE_NAME_INT;
		// from boolean
		assertEquals(1, adaptValueToType(true, type));
		assertEquals(0, adaptValueToType(false, type));
		// from short
		value = (byte)127;
		assertEquals((int)(byte)value, adaptValueToType(value, type));
		// from short
		value = (short)1231;
		assertEquals((int)(short)value, adaptValueToType(value, type));
		// from integer
		value = (int)12312312;
		assertEquals(value, adaptValueToType(value, type));
		// from long
		value = (long)((long)1231231231 * (long)12);
		assertEquals((int)(long)value, adaptValueToType(value, type));
		// from float
		value = 123.05f;
		assertEquals((int)(float)value, adaptValueToType(value, type));
		// from double
		value = 123.05d;
		assertEquals((int)(double)value, adaptValueToType(value, type));
		// from char
		value = 'd';
		assertEquals(null, adaptValueToType(value, type));
		// from String
		value = "122";
		assertEquals(122, adaptValueToType(value, type));
		value = "21.2";
		assertEquals(null, adaptValueToType(value, type));
		// from default
		value = EnumType.value1;
		assertEquals(null, adaptValueToType(value, type));

		type = TYPE_NAME_LONG;
		// from boolean
		assertEquals((long)1, adaptValueToType(true, type));
		assertEquals((long)0, adaptValueToType(false, type));
		// from short
		value = (byte)127;
		assertEquals((long)(byte)value, adaptValueToType(value, type));
		// from short
		value = (short)1231;
		assertEquals((long)(short)value, adaptValueToType(value, type));
		// from integer
		value = (int)12312312;
		assertEquals((long)(int)value, adaptValueToType(value, type));
		// from long
		value = (long)((long)1231231231 * (long)12);
		assertEquals((long)value, adaptValueToType(value, type));
		// from float
		value = 123.05f;
		assertEquals((long)(float)value, adaptValueToType(value, type));
		// from double
		value = 123.05d;
		assertEquals((long)(double)value, adaptValueToType(value, type));
		// from char
		value = 'd';
		assertEquals(null, adaptValueToType(value, type));
		// from String
		value = "1231231200";
		assertEquals((long)(12312312 * 100), adaptValueToType(value, type));
		value = "21.2";
		assertEquals(null, adaptValueToType(value, type));
		// from default
		value = EnumType.value1;
		assertEquals(null, adaptValueToType(value, type));

		type = TYPE_NAME_FLOAT;
		// from boolean
		assertEquals(null, adaptValueToType(true, type));
		assertEquals(null, adaptValueToType(false, type));
		// from short
		value = (byte)127;
		assertEquals((float)(byte)value, adaptValueToType(value, type));
		// from short
		value = (short)1231;
		assertEquals((float)(short)value, adaptValueToType(value, type));
		// from integer
		value = (int)12312312;
		assertEquals((float)(int)value, adaptValueToType(value, type));
		// from long
		value = (long)((long)1231231231 * (long)12);
		assertEquals((float)(long)value, adaptValueToType(value, type));
		// from float
		value = 123.05f;
		assertEquals((float)value, adaptValueToType(value, type));
		// from double
		value = 123.05d;
		assertEquals((float)(double)value, adaptValueToType(value, type));
		// from char
		value = 'd';
		assertEquals(null, adaptValueToType(value, type));
		// from String
		value = "122";
		assertEquals((float)122, adaptValueToType(value, type));
		value = "21.2";
		assertEquals((float)21.2, adaptValueToType(value, type));
		// from default
		value = EnumType.value1;
		assertEquals(null, adaptValueToType(value, type));

		type = TYPE_NAME_DOUBLE;
		// from boolean
		assertEquals(null, adaptValueToType(true, type));
		assertEquals(null, adaptValueToType(false, type));
		// from short
		value = (byte)127;
		assertEquals((double)(byte)value, adaptValueToType(value, type));
		// from short
		value = (short)1231;
		assertEquals((double)(short)value, adaptValueToType(value, type));
		// from integer
		value = (int)12312312;
		assertEquals((double)(int)value, adaptValueToType(value, type));
		// from long
		value = (long)((long)1231231231 * (long)12);
		assertEquals((double)(long)value, adaptValueToType(value, type));
		// from double
		value = 123.05f;
		assertEquals((double)(float)value, adaptValueToType(value, type));
		// from double
		value = 123.05d;
		assertEquals((double)value, adaptValueToType(value, type));
		// from char
		value = 'd';
		assertEquals(null, adaptValueToType(value, type));
		// from String
		value = "122";
		assertEquals((double)122, adaptValueToType(value, type));
		value = "21.2";
		assertEquals((double)21.2, adaptValueToType(value, type));
		// from default
		value = EnumType.value1;
		assertEquals(null, adaptValueToType(value, type));
		 
		type = TYPE_NAME_CHAR;
		// from boolean
		assertEquals(null, adaptValueToType(true, type));
		assertEquals(null, adaptValueToType(false, type));
		// from short
		value = (byte)127;
		assertEquals((char)(byte)value, adaptValueToType(value, type));
		// from short
		value = (short)1231;
		assertEquals((char)(short)value, adaptValueToType(value, type));
		// from integer
		value = (int)12312312;
		assertEquals(null, adaptValueToType(value, type));
		// from long
		value = (long)((long)1231231231 * (long)12);
		assertEquals(null, adaptValueToType(value, type));
		// from float
		value = 123.05f;
		assertEquals(null, adaptValueToType(value, type));
		// from double
		value = 123.05d;
		assertEquals(null, adaptValueToType(value, type));
		// from char
		value = 'd';
		assertEquals((char)value, adaptValueToType(value, type));
		// from String
		value = "122";
		assertEquals(null, adaptValueToType(value, type));
		value = "2";
		assertEquals('2', adaptValueToType(value, type));
		value = "";
		assertEquals('\0', adaptValueToType(value, type));
		// from default
		value = EnumType.value1;
		assertEquals(null, adaptValueToType(value, type));
		 
		type = TYPE_NAME_STRING;
		// from boolean
		assertEquals("true", adaptValueToType(true, type));
		assertEquals("false", adaptValueToType(false, type));
		// from short
		value = (byte)127;
		assertEquals("127", adaptValueToType(value, type));
		// from short
		value = (short)1231;
		assertEquals("1231", adaptValueToType(value, type));
		// from integer
		value = (int)12312312;
		assertEquals("12312312", adaptValueToType(value, type));
		// from long
		value = (long)((long)1231231231 * (long)12);
		assertEquals("14774774772", adaptValueToType(value, type));
		// from float
		value = 123.05f;
		assertEquals("123.05", adaptValueToType(value, type));
		// from double
		value = 123.05d;
		assertEquals("123.05", adaptValueToType(value, type));
		// from char
		value = 'd';
		assertEquals("d", adaptValueToType(value, type));
		// from String
		value = "Stronk strink";
		assertEquals(value, adaptValueToType(value, type));
		value = "";
		assertEquals(value, adaptValueToType(value, type));
		// from default
		value = EnumType.value1;
		assertEquals(EnumType.value1.name(), adaptValueToType(value, type));
		
		type = "Any other type";
		// from boolean
		assertEquals(null, adaptValueToType(true, type));
		assertEquals(null, adaptValueToType(false, type));
		// from short
		value = (byte)127;
		assertEquals(null, adaptValueToType(value, type));
		// from short
		value = (short)1231;
		assertEquals(null, adaptValueToType(value, type));
		// from integer
		value = (int)12312312;
		assertEquals(null, adaptValueToType(value, type));
		// from long
		value = (long)((long)1231231231 * (long)12);
		assertEquals(null, adaptValueToType(value, type));
		// from float
		value = 123.05f;
		assertEquals(null, adaptValueToType(value, type));
		// from double
		value = 123.05d;
		assertEquals(null, adaptValueToType(value, type));
		// from char
		value = 'd';
		assertEquals(null, adaptValueToType(value, type));
		// from String
		value = "Stronk strink";
		assertEquals(null, adaptValueToType(value, type));
		// from default
		value = EnumType.value1;
		assertEquals(null, adaptValueToType(value, type));
	}
	
}
