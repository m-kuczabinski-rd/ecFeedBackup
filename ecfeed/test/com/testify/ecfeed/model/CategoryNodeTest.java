package com.testify.ecfeed.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class CategoryNodeTest extends CategoryNode {

	public CategoryNodeTest() {
		super("CategoryNode", "dummy");
	}

//	@Test
//	public void testGetBooleanValueFromString() {
//		CategoryNode category = new CategoryNode("cat", "boolean");
//
////		assertTrue(EcisStringValueValid("true"));
////		assertTrue(EcisStringValueValid("True"));
////		assertTrue(EcisStringValueValid("TRUE"));
////		assertTrue(EcisStringValueValid("false"));
////		assertTrue(EcisStringValueValid(""));
////		assertTrue(EcisStringValueValid("anything"));
////
////		assertEquals(true, category.getValueFromString("true"));
////		assertEquals(true, category.getValueFromString("True"));
////		assertEquals(true, category.getValueFromString("TRUE"));
////		assertEquals(false, category.getValueFromString("1"));
////		assertEquals(false, category.getValueFromString(""));
////		assertEquals(false, category.getValueFromString("anything"));
//	}
//
//
//
//	@Test
//	public void testGetByteValueFromString() {
//		String byteMax = Byte.toString(Byte.MAX_VALUE);
//		String byteMin = Byte.toString(Byte.MIN_VALUE);
//		String byteMinOverflowed = "-1000000000000";
//		String byteMaxOverflowed = "1000000000000";
//		String byteZero = "0";
//		String byteOne = "1";
//		String byteMinusOne = "-1";
//
//		CategoryNode category = new CategoryNode("cat", "byte");
//
////		assertFalse(category.isStringValueValid("string"));
////		assertTrue(category.isStringValueValid(byteMin));
////		assertTrue(category.isStringValueValid(byteMax));
////		assertTrue(category.isStringValueValid(byteZero));
////		assertTrue(category.isStringValueValid(byteOne));
////		assertTrue(category.isStringValueValid(byteMinusOne));
////		assertFalse(category.isStringValueValid(byteMinOverflowed));
////		assertFalse(category.isStringValueValid(byteMaxOverflowed));
////
////		assertEquals(Byte.MAX_VALUE, category.getValueFromString(byteMax));
////		assertEquals(Byte.MIN_VALUE, category.getValueFromString(byteMin));
////		assertEquals((byte)-1, category.getValueFromString(byteMinusOne));
////		assertEquals((byte) 0, category.getValueFromString(byteZero));
////		assertEquals((byte) 1, category.getValueFromString(byteOne));
////		assertEquals(null, category.getValueFromString(byteMaxOverflowed));
////		assertEquals(null, category.getValueFromString(byteMinOverflowed));
////		assertEquals(null, category.getValueFromString("string"));
////		assertEquals(null, category.getValueFromString("1.0"));
//	}
//
//	@Test
//	public void testGetIntValueFromString() {
//		String intMax = Integer.toString(Integer.MAX_VALUE);
//		String intMin = Integer.toString(Integer.MIN_VALUE);
//		String intMinOverflowed = "-1000000000000";
//		String intMaxOverflowed = "1000000000000";
//		String intZero = "0";
//		String intOne = "1";
//		String intMinusOne = "-1";
//
//		CategoryNode category = new CategoryNode("cat", "int");
//
////		assertFalse(category.isStringValueValid("string"));
////		assertTrue(category.isStringValueValid(intMin));
////		assertTrue(category.isStringValueValid(intMax));
////		assertTrue(category.isStringValueValid(intZero));
////		assertTrue(category.isStringValueValid(intOne));
////		assertTrue(category.isStringValueValid(intMinusOne));
////		assertFalse(category.isStringValueValid(intMinOverflowed));
////		assertFalse(category.isStringValueValid(intMaxOverflowed));
////
////		assertEquals(Integer.MAX_VALUE, category.getValueFromString(intMax));
////		assertEquals(Integer.MIN_VALUE, category.getValueFromString(intMin));
////		assertEquals((int)-1, category.getValueFromString(intMinusOne));
////		assertEquals((int) 0, category.getValueFromString(intZero));
////		assertEquals((int) 1, category.getValueFromString(intOne));
////		assertEquals(null, category.getValueFromString(intMaxOverflowed));
////		assertEquals(null, category.getValueFromString(intMinOverflowed));
////		assertEquals(null, category.getValueFromString("string"));
////		assertEquals(null, category.getValueFromString("1.0"));
//	}
//
//	@Test
//	public void testGetLongValueFromString() {
//		String longMax = Long.toString(Long.MAX_VALUE);
//		String longMin = Long.toString(Long.MIN_VALUE);
//		String longMinOverflowed = "-100000000000000000000000000000";
//		String longMaxOverflowed = "1000000000000000000000000000000";
//		String longZero = "0";
//		String longOne = "1";
//		String longMinusOne = "-1";
//
//		CategoryNode category = new CategoryNode("cat", "long");
//
////		assertFalse(category.isStringValueValid("string"));
////		assertTrue(category.isStringValueValid(longMin));
////		assertTrue(category.isStringValueValid(longMax));
////		assertTrue(category.isStringValueValid(longZero));
////		assertTrue(category.isStringValueValid(longOne));
////		assertTrue(category.isStringValueValid(longMinusOne));
////		assertFalse(category.isStringValueValid(longMinOverflowed));
////		assertFalse(category.isStringValueValid(longMaxOverflowed));
////
////		assertEquals(Long.MAX_VALUE, category.getValueFromString(longMax));
////		assertEquals(Long.MIN_VALUE, category.getValueFromString(longMin));
////		assertEquals((long)-1, category.getValueFromString(longMinusOne));
////		assertEquals((long) 0, category.getValueFromString(longZero));
////		assertEquals((long) 1, category.getValueFromString(longOne));
////		assertEquals(null, category.getValueFromString(longMaxOverflowed));
////		assertEquals(null, category.getValueFromString(longMinOverflowed));
////		assertEquals(null, category.getValueFromString("string"));
////		assertEquals(null, category.getValueFromString("1.0"));
//	}
//
//	@Test
//	public void testGetShortValueFromString() {
//		String shortMax = Short.toString(Short.MAX_VALUE);
//		String shortMin = Short.toString(Short.MIN_VALUE);
//		String shortMinOverflowed = "-100000000000000000000000000000";
//		String shortMaxOverflowed = "1000000000000000000000000000000";
//		String shortZero = "0";
//		String shortOne = "1";
//		String shortMinusOne = "-1";
//
//		CategoryNode category = new CategoryNode("cat", "short");
//
////		assertFalse(category.isStringValueValid("string"));
////		assertTrue(category.isStringValueValid(shortMin));
////		assertTrue(category.isStringValueValid(shortMax));
////		assertTrue(category.isStringValueValid(shortZero));
////		assertTrue(category.isStringValueValid(shortOne));
////		assertTrue(category.isStringValueValid(shortMinusOne));
////		assertFalse(category.isStringValueValid(shortMinOverflowed));
////		assertFalse(category.isStringValueValid(shortMaxOverflowed));
////
////		assertEquals(Short.MAX_VALUE, category.getValueFromString(shortMax));
////		assertEquals(Short.MIN_VALUE, category.getValueFromString(shortMin));
////		assertEquals((short)-1, category.getValueFromString(shortMinusOne));
////		assertEquals((short) 0, category.getValueFromString(shortZero));
////		assertEquals((short) 1, category.getValueFromString(shortOne));
////		assertEquals(null, category.getValueFromString(shortMaxOverflowed));
////		assertEquals(null, category.getValueFromString(shortMinOverflowed));
////		assertEquals(null, category.getValueFromString("string"));
////		assertEquals(null, category.getValueFromString("1.0"));
//	}
//
//	@Test
//	public void testGetFloatValueFromString() {
////		String floatMax = Float.toString(Float.MAX_VALUE);
////		String floatMin = Float.toString(Float.MIN_VALUE);
////
////		String floatMinOverflowed = "1.6E-48";
////		String floatMaxOverflowed = "35.5E129";
////		String floatZero = "0";
////		String floatOne = "1";
////		String floatMinusOne = "-1";
////		String floatZeroDotOne = "0.1";
////		String floatOneDotOne = "1.1";
////		String floatMinusOneDotOne = "-1.1";
////		String floatPositiveInfinity = Float.toString(Float.POSITIVE_INFINITY);
////		String floatNegativeInfinity = Float.toString(Float.NEGATIVE_INFINITY);
////
////		CategoryNode category = new CategoryNode("cat", "float");
//
////		assertFalse(category.isStringValueValid("string"));
////		assertTrue(category.isStringValueValid(floatMin));
////		assertTrue(category.isStringValueValid(floatMax));
////		assertTrue(category.isStringValueValid(floatZero));
////		assertTrue(category.isStringValueValid(floatOne));
////		assertTrue(category.isStringValueValid(floatMinusOne));
////		assertTrue(category.isStringValueValid(floatMinOverflowed));
////		assertTrue(category.isStringValueValid(floatMaxOverflowed));
////
////		assertEquals(Float.MAX_VALUE, category.getValueFromString(floatMax));
////		assertEquals(Float.MIN_VALUE, category.getValueFromString(floatMin));
////		assertEquals((float)-1, category.getValueFromString(floatMinusOne));
////		assertEquals((float) 0, category.getValueFromString(floatZero));
////		assertEquals((float) 1, category.getValueFromString(floatOne));
////		assertEquals((float)-1.1, category.getValueFromString(floatMinusOneDotOne));
////		assertEquals((float) 0.1, category.getValueFromString(floatZeroDotOne));
////		assertEquals((float) 1.1, category.getValueFromString(floatOneDotOne));
////		assertEquals(Float.POSITIVE_INFINITY, category.getValueFromString(floatMaxOverflowed));
////		assertEquals(Float.POSITIVE_INFINITY, category.getValueFromString(floatPositiveInfinity));
////		assertEquals(Float.NEGATIVE_INFINITY, category.getValueFromString("-" + floatMaxOverflowed));
////		assertEquals(Float.NEGATIVE_INFINITY, category.getValueFromString(floatNegativeInfinity));
////		assertEquals((float)0.0, category.getValueFromString(floatMinOverflowed));
////
////		assertEquals(null, category.getValueFromString("string"));
//	}
//
//	@Test
//	public void testGetDoubleValueFromString() {
//		String doubleMax = "1.7976931348623157E308";
//		String doubleMin = "4.9E-324";
//
//		String doubleMinOverflowed = "4.9E-325";
//		String doubleMaxOverflowed = "1.8E308";
//		String doubleZero = "0";
//		String doubleOne = "1";
//		String doubleMinusOne = "-1";
//		String doubleZeroDotOne = "0.1";
//		String doubleOneDotOne = "1.1";
//		String doubleMinusOneDotOne = "-1.1";
//		String doublePositiveInfinity = Double.toString(Double.POSITIVE_INFINITY);
//		String doubleNegativeInfinity = Double.toString(Double.NEGATIVE_INFINITY);
//
//		CategoryNode category = new CategoryNode("cat", "double");
//
////		assertFalse(category.isStringValueValid("string"));
////		assertTrue(category.isStringValueValid(doubleMin));
////		assertTrue(category.isStringValueValid(doubleMax));
////		assertTrue(category.isStringValueValid(doubleZero));
////		assertTrue(category.isStringValueValid(doubleOne));
////		assertTrue(category.isStringValueValid(doubleMinusOne));
////		assertTrue(category.isStringValueValid(doubleMinOverflowed));
////		assertTrue(category.isStringValueValid(doubleMaxOverflowed));
////
////		assertEquals(Double.MAX_VALUE, category.getValueFromString(doubleMax));
////		assertEquals(Double.MIN_VALUE, category.getValueFromString(doubleMin));
////		assertEquals((double)-1,   category.getValueFromString(doubleMinusOne));
////		assertEquals((double) 0,   category.getValueFromString(doubleZero));
////		assertEquals((double) 1,   category.getValueFromString(doubleOne));
////		assertEquals((double)-1.1, category.getValueFromString(doubleMinusOneDotOne));
////		assertEquals((double) 0.1, category.getValueFromString(doubleZeroDotOne));
////		assertEquals((double) 1.1, category.getValueFromString(doubleOneDotOne));
////		assertEquals(Double.POSITIVE_INFINITY, category.getValueFromString(doubleMaxOverflowed));
////		assertEquals(Double.POSITIVE_INFINITY, category.getValueFromString(doublePositiveInfinity));
////		assertEquals(Double.NEGATIVE_INFINITY, category.getValueFromString("-" + doubleMaxOverflowed));
////		assertEquals(Double.NEGATIVE_INFINITY, category.getValueFromString(doubleNegativeInfinity));
////		assertEquals((double)0.0, category.getValueFromString(doubleMinOverflowed));
////
////		assertEquals(null, category.getValueFromString("string"));
//	}
//	
	@Test 
	public void testEquals(){
		CategoryNode cat = new CategoryNode("cat", "boolean");
		cat.addPartition(new PartitionNode("true", 	true));
		cat.addPartition(new PartitionNode("false", false));

		CategoryNode catCopy = new CategoryNode("cat", "boolean");
		PartitionNode part1 = new PartitionNode("true", true);
		PartitionNode part2 = new PartitionNode("false", false);
		catCopy.addPartition(part1);
		catCopy.addPartition(part2);
		
		assertTrue(cat.equals(catCopy));
		part2.setValue(true);
		assertFalse(cat.equals(catCopy));
		
	}
}
