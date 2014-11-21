package com.testify.ecfeed.adapter.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.testify.ecfeed.generators.CartesianProductGenerator;
import com.testify.ecfeed.junit.OnlineRunner;
import com.testify.ecfeed.junit.annotations.Constraints;
import com.testify.ecfeed.junit.annotations.EcModel;
import com.testify.ecfeed.junit.annotations.Generator;

@RunWith(OnlineRunner.class)
@EcModel("test/com.testify.ecfeed.adapter.java.ect")
@Generator(CartesianProductGenerator.class)
@Constraints(Constraints.ALL)
public class ChoiceValueParserTest{

	private ChoiceValueParser fParser;

	public enum ImplementedType{
		IMPLEMENTED
	}

	public ChoiceValueParserTest() {
		ClassLoader parentLoader = this.getClass().getClassLoader();
		ModelClassLoader loader = new ModelClassLoader(new URL[]{}, parentLoader);
		fParser =  new ChoiceValueParser(loader);
	}

	@Test
	public void parseBooleanTest(String valueString, boolean value, boolean nullExpected){
//		System.out.println("parseBooleanTest(" + valueString + ", " + value + ")");

		Object parsed = fParser.parseValue(valueString, Constants.TYPE_NAME_BOOLEAN);
		if(nullExpected){
			assertTrue(parsed == null);
		}
		else{
			assertEquals(value, fParser.parseValue(valueString, Constants.TYPE_NAME_BOOLEAN));
		}
	}

	@Test
	public void parseByteTest(String valueString, byte parsedValue){
//		valueString = "string";
//		parsedValue = 0;
//		System.out.println("parseByteTest(" + valueString + ", " + parsedValue + ")");
		Object parsed = fParser.parseValue(valueString, Constants.TYPE_NAME_BYTE);

		if(valueString == null || valueString.equals("string")){
			assertEquals(null, parsed);
		}
		else{
			assertEquals(parsedValue, parsed);
		}
	}

	@Test
	public void parseShortTest(String valueString, short parsedValue){
//		System.out.println("parseShortTest(" + valueString + ", " + parsedValue + ")");
		Object parsed = fParser.parseValue(valueString, Constants.TYPE_NAME_SHORT);

		if(valueString == null || valueString.equals("string")){
			assertEquals(null, parsed);
		}
		else{
			assertEquals(parsedValue, parsed);
		}
	}

	@Test
	public void parseIntegerTest(String valueString, int parsedValue){
//		System.out.println("parseIntegerTest(" + valueString + ", " + parsedValue + ")");
		Object parsed = fParser.parseValue(valueString, Constants.TYPE_NAME_INT);

		if(valueString == null || valueString.equals("string")){
			assertEquals(null, parsed);
		}
		else{
			assertEquals(parsedValue, parsed);
		}
	}

	@Test
	public void parseLongTest(String valueString, long parsedValue){
//		System.out.println("parseLongTest(" + valueString + ", " + parsedValue + ")");
		Object parsed = fParser.parseValue(valueString, Constants.TYPE_NAME_LONG);

		if(valueString == null || valueString.equals("string")){
			assertEquals(null, parsed);
		}
		else{
			assertEquals(parsedValue, parsed);
		}
	}

	@Test
	public void parseCharTest(String valueString, char parsedValue){
//		System.out.println("parseCharTest(" + valueString + ", " + parsedValue + ")");
		Object parsed = fParser.parseValue(valueString, Constants.TYPE_NAME_CHAR);

		if(valueString == null || valueString.equals("string")){
			assertEquals(null, parsed);
		}
		else{
			assertEquals(parsedValue, parsed);
		}
	}

	@Test
	public void parseFloatTest(String valueString, float parsedValue){
//		System.out.println("parseFloatTest(" + valueString + ", " + parsedValue + ")");
		Object parsed = fParser.parseValue(valueString, Constants.TYPE_NAME_FLOAT);

		if(valueString == null || valueString.equals("string")){
			assertEquals(null, parsed);
		}
		else{
			assertEquals(parsedValue, parsed);
		}
	}

	@Test
	public void parseDoubleTest(String valueString, double parsedValue){
//		System.out.println("parseDoubleTest(" + valueString + ", " + parsedValue + ")");
		Object parsed = fParser.parseValue(valueString, Constants.TYPE_NAME_DOUBLE);

		if(valueString == null || valueString.equals("string")){
			assertEquals(null, parsed);
		}
		else{
			assertEquals(parsedValue, parsed);
		}
	}

	@Test
	public void parseStringTest(String valueString, String parsedValue){
//		System.out.println("parseStringTest(" + valueString + ", " + parsedValue + ")");
		Object parsed = fParser.parseValue(valueString, Constants.TYPE_NAME_STRING);
		assertEquals(parsedValue, parsed);
	}

	@Test
	public void parseUserTypeTest(String valueString, String type, boolean parsedCorrectly){
//		System.out.println("parseUserTypeTest(" + valueString + ", " + type + ", " + parsedCorrectly + ")");

		Object parsed = fParser.parseValue(valueString, type);
		if(parsedCorrectly){
			assertEquals(ImplementedType.IMPLEMENTED, parsed);
		}
		else{
			assertEquals(null, parsed);
		}

	}

}

