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

package com.testify.ecfeed.core.serialization;

import static com.testify.ecfeed.testutils.ModelTestUtils.assertElementsEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

import com.testify.ecfeed.core.model.ChoiceNode;
import com.testify.ecfeed.core.model.ChoicesParentStatement;
import com.testify.ecfeed.core.model.ClassNode;
import com.testify.ecfeed.core.model.Constraint;
import com.testify.ecfeed.core.model.ConstraintNode;
import com.testify.ecfeed.core.model.EStatementRelation;
import com.testify.ecfeed.core.model.GlobalParameterNode;
import com.testify.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.core.model.MethodParameterNode;
import com.testify.ecfeed.core.model.ModelConverter;
import com.testify.ecfeed.core.model.ModelVersionDistributor;
import com.testify.ecfeed.core.model.RootNode;
import com.testify.ecfeed.core.serialization.IModelParser;
import com.testify.ecfeed.core.serialization.IModelSerializer;
import com.testify.ecfeed.core.serialization.ect.EctParser;
import com.testify.ecfeed.core.serialization.ect.EctSerializer;
import com.testify.ecfeed.testutils.RandomModelGenerator;

public class EctSerializerTest {

	RandomModelGenerator fGenerator = new RandomModelGenerator();

	public void modelSerializerTest(int version) { 
		RootNode model = new RootNode("model", version);
		model.addClass(new ClassNode("com.example.TestClass1"));
		model.addClass(new ClassNode("com.example.TestClass2"));
		model.addParameter(new GlobalParameterNode("globalParameter1", "int"));
		model.addParameter(new GlobalParameterNode("globalParameter2", "com.example.UserType"));

		OutputStream ostream = new ByteArrayOutputStream();
		EctSerializer serializer = new EctSerializer(ostream, version);
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
	public void modelSerializerTestVersion0() {
		modelSerializerTest(0);
	}

	@Test
	public void modelSerializerTestVersion1() {
		modelSerializerTest(1);
	}	

	private void classSerializerTest(boolean runOnAndroid, String androidBaseRunner, int version){
		ClassNode classNode = new ClassNode("com.example.TestClass", runOnAndroid, androidBaseRunner);
		classNode.addMethod(new MethodNode("testMethod1"));
		classNode.addMethod(new MethodNode("testMethod2"));
		classNode.addParameter(new GlobalParameterNode("parameter1", "int"));
		classNode.addParameter(new GlobalParameterNode("parameter2", "float"));
		classNode.addParameter(new GlobalParameterNode("parameter3", "com.example.UserType"));

		RootNode model = new RootNode("model", version);
		model.addClass(classNode);

		OutputStream ostream = new ByteArrayOutputStream();
		EctSerializer serializer = new EctSerializer(ostream, version);
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
		classSerializerTest(true, "com.example.AndroidBaseRunner", 0);
	}

	@Test
	public void classSerializerTestWithoutAndroidBaseRunner(){
		classSerializerTest(false, null, 0);
	}

	@Test
	public void classSerializerTestVersion1(){
		classSerializerTest(false, null, 1);
	}	

	@Test
	public void wrongTypeStreamTest(){
		int version = ModelVersionDistributor.getCurrentVersion();
		RootNode r = new RootNode("model", version);

		OutputStream ostream = new ByteArrayOutputStream();
		EctSerializer serializer = new EctSerializer(ostream, version);
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

	private RootNode createModel(int version) {

		ChoiceNode choice = new ChoiceNode("choice", "0");

		MethodParameterNode parameter = new MethodParameterNode("parameter", "int", "0", false);
		parameter.addChoice(choice);

		MethodNode methodNode = new MethodNode("testMethod1");
		methodNode.addParameter(parameter);

		Constraint constraint = new Constraint(
				new ChoicesParentStatement(parameter, EStatementRelation.EQUAL, choice),
				new ChoicesParentStatement(parameter, EStatementRelation.EQUAL, choice));

		ConstraintNode constraintNode = new ConstraintNode("name1", constraint);
		methodNode.addConstraint(constraintNode);

		ClassNode classNode = new ClassNode("com.example.TestClass", false, null);
		classNode.addMethod(methodNode);

		RootNode model = new RootNode("model", version);
		model.addClass(classNode);
		model.setVersion(version);

		return model;
	}

	private String getSerializedString(RootNode convertedModel) {
		OutputStream convertedModelStream = new ByteArrayOutputStream();
		IModelSerializer convertedSerializer = 
				new EctSerializer(convertedModelStream, ModelVersionDistributor.getCurrentVersion());

		try {
			convertedSerializer.serialize(convertedModel);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}

		return convertedModelStream.toString();
	}

	@Test
	public void serializerTestWithModelConversion(){
		RootNode convertedModel  = ModelConverter.convertToCurrentVersion(createModel(0));
		String convertedString = getSerializedString(convertedModel);

		RootNode currentModel = createModel(ModelVersionDistributor.getCurrentVersion());
		String currentString = getSerializedString(currentModel);

		assertEquals(ModelVersionDistributor.getCurrentVersion(), convertedModel.getModelVersion());
		assertEquals(ModelVersionDistributor.getCurrentVersion(), currentModel.getModelVersion());

		assertEquals(currentString, convertedString);
	}
}
