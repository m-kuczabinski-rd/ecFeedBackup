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

package com.testify.ecfeed.serialization;

import static com.testify.ecfeed.testutils.ModelTestUtils.assertElementsEqual;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.serialization.IModelParser;
import com.testify.ecfeed.serialization.IModelSerializer;
import com.testify.ecfeed.serialization.ect.EctParser;
import com.testify.ecfeed.serialization.ect.EctSerializer;
import com.testify.ecfeed.serialization.ect.ObsoleteXmlModelParser;
import com.testify.ecfeed.serialization.ect.ObsoleteXmlModelSerializer;
import com.testify.ecfeed.testutils.RandomModelGenerator;

public class EctSerializerTest {
	
	RandomModelGenerator fGenerator = new RandomModelGenerator();
	
	@Test
	public void modelSerializerTest(){
		RootNode model = fGenerator.generateModel(3);
		
		OutputStream ostream = new ByteArrayOutputStream();
		EctSerializer serializer = new EctSerializer(ostream);
		try {
			serializer.serialize(model);
			InputStream istream = new ByteArrayInputStream(((ByteArrayOutputStream)ostream).toByteArray());
			IModelParser parser = new EctParser();
			RootNode parsedModel = parser.parseModel(istream);
			
			assertElementsEqual(model, parsedModel);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void classSerializerTest(){
		ClassNode c = fGenerator.generateClass(3);
		
		OutputStream ostream = new ByteArrayOutputStream();
		EctSerializer serializer = new EctSerializer(ostream);
		try {
			serializer.serialize(c);
			InputStream istream = new ByteArrayInputStream(((ByteArrayOutputStream)ostream).toByteArray());
			IModelParser parser = new EctParser();
			ClassNode parsedC = parser.parseClass(istream);
			
			assertElementsEqual(c, parsedC);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	public void wrongTypeStreamTest(){
		RootNode r = fGenerator.generateModel(3);
		
		OutputStream ostream = new ByteArrayOutputStream();
		EctSerializer serializer = new EctSerializer(ostream);
		try {
			serializer.serialize(r);
			InputStream istream = new ByteArrayInputStream(((ByteArrayOutputStream)ostream).toByteArray());
			IModelParser parser = new EctParser();
			parser.parseClass(istream);
			fail("Exception expected");
		} catch (Exception e) {
//			System.out.println("Exception caught: " + e.getMessage());
		}
	}
	
//	@Test
	public void modelSerializerCrossTest1(){
		RootNode model = fGenerator.generateModel(3);
		OutputStream ostream = new ByteArrayOutputStream();
		ObsoleteXmlModelSerializer oldSerializer = new ObsoleteXmlModelSerializer(ostream);
		try {
			oldSerializer.writeXmlDocument(model);
			InputStream istream = new ByteArrayInputStream(((ByteArrayOutputStream)ostream).toByteArray());
			IModelParser parser = new EctParser();
			RootNode parsedModel = parser.parseModel(istream);
			assertElementsEqual(model, parsedModel);
			
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}		
	}


//	@Test
	public void modelSerializerCrossTest2(){
		for(int i = 0; i < 10; i++){
			RootNode model = fGenerator.generateModel(5);
			OutputStream ostream = new ByteArrayOutputStream();
			IModelSerializer serializer = new EctSerializer(ostream);
			try {
				serializer.serialize(model);
				InputStream istream = new ByteArrayInputStream(((ByteArrayOutputStream)ostream).toByteArray());
				IModelParser parser = new ObsoleteXmlModelParser();
				RootNode parsedModel = parser.parseModel(istream);
				assertElementsEqual(model, parsedModel);

			} catch (Exception e) {
				fail("Unexpected exception: " + e.getMessage());
			}		
		}
	}

}
