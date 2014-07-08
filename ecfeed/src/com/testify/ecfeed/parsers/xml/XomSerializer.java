package com.testify.ecfeed.parsers.xml;

import java.io.IOException;
import java.io.OutputStream;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;

public class XomSerializer implements IModelVisitor {

	private OutputStream fOutputStream;
	private XomConverter fConverter;

	public XomSerializer(OutputStream ostream){
		fOutputStream = ostream;
		fConverter = new XomConverter();
	}
	
	@Override
	public Object visit(RootNode node) throws Exception {
		Element element = (Element)node.accept(fConverter);
		writeDocument(element);
		return null;
	}

	@Override
	public Object visit(ClassNode node) throws Exception {
		Element element = (Element)node.accept(fConverter);
		writeDocument(element);
		return null;
	}

	@Override
	public Object visit(MethodNode node) throws Exception {
		Element element = (Element)node.accept(fConverter);
		writeDocument(element);
		return null;
	}

	@Override
	public Object visit(CategoryNode node) throws Exception {
		Element element = (Element)node.accept(fConverter);
		writeDocument(element);
		return null;
	}

	@Override
	public Object visit(TestCaseNode node) throws Exception {
		Element element = (Element)node.accept(fConverter);
		writeDocument(element);
		return null;
	}

	@Override
	public Object visit(ConstraintNode node) throws Exception {
		Element element = (Element)node.accept(fConverter);
		writeDocument(element);
		return null;
	}

	@Override
	public Object visit(PartitionNode node) throws Exception {
		Element element = (Element)node.accept(fConverter);
		writeDocument(element);
		return null;
	}

	private void writeDocument(Element element) throws IOException {
		Document document = new Document(element);
		Serializer serializer = new Serializer(fOutputStream);
		// Uncomment for pretty formatting. This however will affect 
		// whitespaces in the document's ... infoset
		serializer.setIndent(4);
		serializer.write(document);
	}

}
