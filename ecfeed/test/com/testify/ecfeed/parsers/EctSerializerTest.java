package com.testify.ecfeed.parsers;

import static com.testify.ecfeed.testutils.ModelTestUtils.assertElementsEqual;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.parsers.etc.EctParser;
import com.testify.ecfeed.parsers.etc.EtcSerializer;
import com.testify.ecfeed.parsers.etc.XmlModelParser;
import com.testify.ecfeed.parsers.etc.XmlModelSerializer;
import com.testify.ecfeed.testutils.RandomModelGenerator;

public class EctSerializerTest {
	
	RandomModelGenerator fGenerator = new RandomModelGenerator();
	
	@Test
	public void modelSerializerTest(){
		RootNode model = fGenerator.generateModel(3);
		
		OutputStream ostream = new ByteArrayOutputStream();
		EtcSerializer serializer = new EtcSerializer(ostream);
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
	
//	@Test
	public void modelSerializerCrossTest1(){
		RootNode model = fGenerator.generateModel(3);
		OutputStream ostream = new ByteArrayOutputStream();
		XmlModelSerializer oldSerializer = new XmlModelSerializer(ostream);
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
			IModelSerializer serializer = new EtcSerializer(ostream);
			try {
				serializer.serialize(model);
				InputStream istream = new ByteArrayInputStream(((ByteArrayOutputStream)ostream).toByteArray());
				IModelParser parser = new XmlModelParser();
				RootNode parsedModel = parser.parseModel(istream);
				assertElementsEqual(model, parsedModel);

			} catch (Exception e) {
				fail("Unexpected exception: " + e.getMessage());
			}		
		}
	}

}
