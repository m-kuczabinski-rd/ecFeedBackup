package com.testify.ecfeed.serialization.ect;

import java.io.IOException;
import java.io.OutputStream;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.serialization.IModelSerializer;

public class EtcSerializer implements IModelSerializer{

	private OutputStream fOutputStream;
	private XomBuilder fConverter;

	public EtcSerializer(OutputStream ostream){
		fOutputStream = ostream;
		fConverter = new XomBuilder();
	}
	
	public Object serialize(RootNode node) throws Exception{
		Element element = (Element)node.accept(fConverter);
		writeDocument(element);
		return null;
	}
	
	public Object serialize(ClassNode node) throws Exception {
		Element element = (Element)node.accept(fConverter);
		writeDocument(element);
		return null;
	}

	public Object serialize(MethodNode node) throws Exception {
		Element element = (Element)node.accept(fConverter);
		writeDocument(element);
		return null;
	}

	public Object serialize(CategoryNode node) throws Exception {
		Element element = (Element)node.accept(fConverter);
		writeDocument(element);
		return null;
	}

	public Object serialize(TestCaseNode node) throws Exception {
		Element element = (Element)node.accept(fConverter);
		writeDocument(element);
		return null;
	}

	public Object serialize(ConstraintNode node) throws Exception {
		Element element = (Element)node.accept(fConverter);
		writeDocument(element);
		return null;
	}

	public Object serialize(PartitionNode node) throws Exception {
		Element element = (Element)node.accept(fConverter);
		writeDocument(element);
		return null;
	}

	private void writeDocument(Element element) throws IOException {
		Document document = new Document(element);
		Serializer serializer = new Serializer(fOutputStream);
		// Uncomment for pretty formatting. This however will affect 
		// whitespaces in the document's ... infoset
//		serializer.setIndent(4);
		serializer.write(document);
	}

}
