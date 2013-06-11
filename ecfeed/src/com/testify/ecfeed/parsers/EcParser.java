package com.testify.ecfeed.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import nu.xom.*;

import com.testify.ecfeed.constants.Constants;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;

public class EcParser {
	
	public RootNode parseEctFile(InputStream istream){
		RootNode root = null;
		try {
			Builder parser = new Builder();
			Document document = parser.build(istream);
			if(document.getRootElement().getLocalName() == Constants.ROOT_NODE_NAME){
				root = (RootNode)parseRootElement(document.getRootElement());
			}
		} catch (IOException|ParsingException e) {
			System.out.println("Exception: " + e.getMessage());
		} 
		return root;
	}

	private RootNode parseRootElement(Element element) {
		String name = element.getAttributeValue(Constants.NODE_NAME_ATTRIBUTE);
		if(name == null || element.getLocalName() != Constants.ROOT_NODE_NAME){
			return null;
		}

		RootNode rootNode = new RootNode(name);
		for(Element child : getIterableElements(element.getChildElements())){
			if(child.getLocalName() == Constants.CLASS_NODE_NAME){
				rootNode.addClass(parseClassElement(child));
			}
		}
		return rootNode;
	}

	private ClassNode parseClassElement(Element element) {
		String qualifiedName = element.getAttributeValue(Constants.NODE_NAME_ATTRIBUTE);
		if (qualifiedName == null){
			return null;
		}
		
		ClassNode classNode = new ClassNode(qualifiedName);
		for(Element child : getIterableElements(element.getChildElements())){
			if(child.getLocalName() == Constants.METHOD_NODE_NAME){
				classNode.addMethod(parseMethodElement(child));
			}
		}

		return classNode;
	}

	//TODO unit tests
	private MethodNode parseMethodElement(Element element) {
		String name = element.getAttributeValue(Constants.NODE_NAME_ATTRIBUTE);
		if (name == null){
			return null;
		}
		
		MethodNode methodNode = new MethodNode(name);
		for(Element child : getIterableElements(element.getChildElements())){
			if(child.getLocalName() == Constants.CATEGORY_NODE_NAME){
				methodNode.addCategory(parseCategoryElement(child));
			}
			if(child.getLocalName() == Constants.TEST_CASE_NODE_NAME){
				methodNode.addTestCase(parseTestCaseElement(child, methodNode.getCategories()));
			}
		}
		return methodNode;
	}
	
	//TODO unit tests
	private TestCaseNode parseTestCaseElement(Element element, Vector<CategoryNode> categories) {
		String testSuiteName = element.getAttributeValue(Constants.TEST_SUITE_NAME_ATTRIBUTE);
		Vector<PartitionNode> testData = new Vector<PartitionNode>();
		Vector<Element> parameterElements = getIterableElements(element.getChildElements());
		
		if(categories.size() != parameterElements.size()){
			return null;
		}

		for(int i = 0; i < parameterElements.size(); i++){
			Element testParameterElement = parameterElements.elementAt(i);
			
			if(testParameterElement.getLocalName().equals(Constants.TEST_PARAMETER_NODE_NAME)){
				String partitionName = testParameterElement.getAttributeValue(Constants.PARTITION_ATTRIBUTE_NAME);
				PartitionNode partition = categories.elementAt(i).getPartition(partitionName);
				testData.add(partition);
			}
		}
		return new TestCaseNode(testSuiteName, testData);
	}

	private CategoryNode parseCategoryElement(Element element) {
		String name = element.getAttributeValue(Constants.NODE_NAME_ATTRIBUTE);
		String type = element.getAttributeValue(Constants.TYPE_NAME_ATTRIBUTE);
		if (name == null | type == null){
			return null;
		}
		
		CategoryNode categoryNode = new CategoryNode(name, type);
		for(Element child : getIterableElements(element.getChildElements())){
			if(child.getLocalName() == Constants.PARTITION_NODE_NAME){
				categoryNode.addPartition(parsePartitionElement(child, type));
			}
		}
		
		return categoryNode;
	}

	private PartitionNode parsePartitionElement(Element element, String typeSignature) {
		String name = element.getAttributeValue(Constants.NODE_NAME_ATTRIBUTE);
		String valueString = element.getAttributeValue(Constants.VALUE_ATTRIBUTE);
		if (name == null | valueString == null){
			return null;
		}
		Object value = parseValue(valueString, typeSignature);
		return new PartitionNode(name, value);
	}

	private Object parseValue(String valueString, String type) {
		switch(type){
		case "boolean":
			return Boolean.parseBoolean(valueString);
		case "byte":
			return Byte.parseByte(valueString);
		case "char":
			if (valueString.length() <= 0){
				return null;
			}
			int intValue = Integer.parseInt(valueString);
			return (char)intValue;
		case "double":
			return Double.parseDouble(valueString);
		case "float":
			return Float.parseFloat(valueString);
		case "int":
			return Integer.parseInt(valueString);
		case "long":
			return Long.parseLong(valueString);
		case "short":
			return Short.parseShort(valueString);
		case "String":
			return valueString.equals(Constants.NULL_VALUE_STRING_REPRESENTATION)?null:valueString;
		default:
			return null;
		}		
	}

	private Vector<Element> getIterableElements(Elements elements){
		Vector<Element> v = new Vector<Element>();
		for(int i = 0; i < elements.size(); i++){
			Node node = elements.get(i);
			if(node instanceof Element){
				v.add((Element)node);
			}
		}
		return v;
	}

}
