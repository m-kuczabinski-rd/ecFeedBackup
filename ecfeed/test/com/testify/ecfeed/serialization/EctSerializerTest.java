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
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.serialization.ect.EctParser;
import com.testify.ecfeed.serialization.ect.EctSerializer;
import com.testify.ecfeed.testutils.RandomModelGenerator;

public class EctSerializerTest {

	RandomModelGenerator fGenerator = new RandomModelGenerator();

	@Test
	public void modelSerializerTest(){
		RootNode model = new RootNode("model");
		model.addClass(new ClassNode("com.example.TestClass1"));
		model.addClass(new ClassNode("com.example.TestClass2"));
		model.addParameter(new GlobalParameterNode("globalParameter1", "int"));
		model.addParameter(new GlobalParameterNode("globalParameter2", "com.example.UserType"));

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

	private void classSerializerTest(boolean runOnAndroid, String androidBaseRunner){
		ClassNode classNode = new ClassNode("com.example.TestClass", runOnAndroid, androidBaseRunner);
		classNode.addMethod(new MethodNode("testMethod1"));
		classNode.addMethod(new MethodNode("testMethod2"));
		classNode.addParameter(new GlobalParameterNode("parameter1", "int"));
		classNode.addParameter(new GlobalParameterNode("parameter2", "float"));
		classNode.addParameter(new GlobalParameterNode("parameter3", "com.example.UserType"));

		RootNode model = new RootNode("model");
		model.addClass(classNode);

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
	public void classSerializerTestWithAndroidBaseRunner(){
		classSerializerTest(true, "com.example.AndroidBaseRunner");
	}

	@Test
	public void classSerializerTestWithoutAndroidBaseRunner(){
		classSerializerTest(false, null);
	}	

	@Test
	public void wrongTypeStreamTest(){
		//		RootNode r = fGenerator.generateModel(3);
		RootNode r = new RootNode("model");

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
	//	public void modelSerializerCrossTest1(){
	//		RootNode model = fGenerator.generateModel(3);
	//		OutputStream ostream = new ByteArrayOutputStream();
	//		ObsoleteXmlModelSerializer oldSerializer = new ObsoleteXmlModelSerializer(ostream);
	//		try {
	//			oldSerializer.writeXmlDocument(model);
	//			InputStream istream = new ByteArrayInputStream(((ByteArrayOutputStream)ostream).toByteArray());
	//			IModelParser parser = new EctParser();
	//			RootNode parsedModel = parser.parseModel(istream);
	//			assertElementsEqual(model, parsedModel);
	//
	//		} catch (Exception e) {
	//			fail("Unexpected exception: " + e.getMessage());
	//		}
	//	}


	//	@Test
	//	public void modelSerializerCrossTest2(){
	//		for(int i = 0; i < 10; i++){
	//			RootNode model = fGenerator.generateModel(5);
	//			OutputStream ostream = new ByteArrayOutputStream();
	//			IModelSerializer serializer = new EctSerializer(ostream);
	//			try {
	//				serializer.serialize(model);
	//				InputStream istream = new ByteArrayInputStream(((ByteArrayOutputStream)ostream).toByteArray());
	//				IModelParser parser = new ObsoleteXmlModelParser();
	//				RootNode parsedModel = parser.parseModel(istream);
	//				assertElementsEqual(model, parsedModel);
	//
	//			} catch (Exception e) {
	//				fail("Unexpected exception: " + e.getMessage());
	//			}
	//		}
	//	}
}
