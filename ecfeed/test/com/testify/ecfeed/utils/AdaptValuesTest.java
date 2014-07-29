/*******************************************************************************
 * Copyright (c) 2014 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.utils;

import static com.testify.ecfeed.modelif.Constants.TYPE_NAME_BOOLEAN;
import static com.testify.ecfeed.modelif.Constants.TYPE_NAME_BYTE;
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
		String value;
		byte byteval = (byte)127;
		short shortval = (short) 1231;
		int intval = 12312312;
		long longval = (long)((long)1231231231 * (long)12);
		float floatval = 123.05f;
		double doubleval = 123.05d;

		type = TYPE_NAME_BOOLEAN;
		// from boolean
		assertEquals("true", adaptValueToType("true", type));
		assertEquals("false", adaptValueToType("false", type));
		assertEquals("false", adaptValueToType("Somestr", type));

		type = TYPE_NAME_BYTE;
		// from byte
		value = Byte.toString(byteval);
		assertEquals(Byte.toString(byteval), adaptValueToType(value, type));
		// from short
		value = Short.toString(shortval);
		assertEquals(Byte.toString((byte)shortval), adaptValueToType(value, type));
		// from integer
		value = Integer.toString(intval);
		assertEquals(Byte.toString((byte)intval), adaptValueToType(value, type));
		// from long
		value = Long.toString(longval);
		assertEquals(Byte.toString((byte)longval), adaptValueToType(value, type));
		// from float
		value = Float.toString(floatval);
		assertEquals(Byte.toString((byte)floatval), adaptValueToType(value, type));
		// from double
		value = Double.toString(doubleval);
		assertEquals(Byte.toString((byte)doubleval), adaptValueToType(value, type));
		// from char
		value = "d";
		assertEquals(null, adaptValueToType(value, type));
		// from String
		value = "122";
		assertEquals(value, adaptValueToType(value, type));
		value = "21.2";
		assertEquals("21", adaptValueToType(value, type));
		// from default
		value = EnumType.value1.name();
		assertEquals(null, adaptValueToType(value, type));
	}
	
}
