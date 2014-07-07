package com.testify.ecfeed.parsers.xml;

import static com.testify.ecfeed.parsers.Constants.*;
import nu.xom.Attribute;
import nu.xom.Element;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.IConverter;
import com.testify.ecfeed.model.IGenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.parsers.Constants;

public class XomConverter implements IConverter {

	@Override
	public Object convert(RootNode node) {
		Element element = createNamedElement(ROOT_NODE_NAME, node); 
				
		for(ClassNode _class : node.getClasses()){
			element.appendChild((Element)convert(_class));
		}
		
		return element;
	}

	@Override
	public Object convert(ClassNode node) {
		Element element = createNamedElement(CLASS_NODE_NAME, node);
		
		for(MethodNode method : node.getMethods()){
			element.appendChild((Element)convert(method));
		}
		return element;
	}

	@Override
	public Object convert(MethodNode node) {
		Element element = createNamedElement(METHOD_NODE_NAME, node);
		
		return element;
	}
	
	@Override
	public Object convert(CategoryNode node){
		Element element = createNamedElement(CATEGORY_NODE_NAME, node);
		element.addAttribute(new Attribute(TYPE_NAME_ATTRIBUTE, node.getType()));
		element.addAttribute(new Attribute(CATEGORY_IS_EXPECTED_ATTRIBUTE_NAME, Boolean.toString(node.isExpected())));
		element.addAttribute(new Attribute(DEFAULT_EXPECTED_VALUE_ATTRIBUTE_NAME, node.getDefaultValueString()));

		for(PartitionNode child : node.getPartitions()){
			element.appendChild((Element)child.convert(this));
		}
	
		return element;
	}
	
	@Override
	public Object convert(PartitionNode node){
		Element element = createNamedElement(PARTITION_NODE_NAME, node);
		String value = node.getValueString();
		//remove disallowed XML characters
		String xml10pattern = "[^"
                + "\u0009\r\n"
                + "\u0020-\uD7FF"
                + "\uE000-\uFFFD"
                + "\ud800\udc00-\udbff\udfff"
                + "]";
		value.replaceAll(xml10pattern, "");

		element.addAttribute(new Attribute(VALUE_ATTRIBUTE, value));
		
		for(String label : node.getLabels()){
			Element labelElement = new Element(Constants.LABEL_NODE_NAME);
			labelElement.addAttribute(new Attribute(Constants.LABEL_ATTRIBUTE_NAME, label));
			element.appendChild(labelElement);
		}
		
		for(PartitionNode child : node.getPartitions()){
			element.appendChild((Element)child.convert(this));
		}
		
		return element;
	}

	private Element createNamedElement(String nodeTag, IGenericNode node){
		Element element = new Element(nodeTag);
		Attribute nameAttr = new Attribute(NODE_NAME_ATTRIBUTE, node.getName());
		element.addAttribute(nameAttr);
		return element;
	}
	
}
