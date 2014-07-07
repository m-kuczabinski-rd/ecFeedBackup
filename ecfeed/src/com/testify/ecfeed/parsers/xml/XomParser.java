package com.testify.ecfeed.parsers.xml;

import static com.testify.ecfeed.parsers.Constants.*;
import static com.testify.ecfeed.parsers.Constants.METHOD_NODE_NAME;
import static com.testify.ecfeed.parsers.Constants.ROOT_NODE_NAME;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.parsers.Constants;
import com.testify.ecfeed.parsers.ParserException;

public class XomParser {
	public RootNode parseRoot(Element element) throws ParserException{
		assertNodeTag(element.getQualifiedName(), ROOT_NODE_NAME);
		String name = getElementName(element);
		
		RootNode root = new RootNode(name);
		
		for(Element child : getIterableChildren(element)){
			root.addClass(parseClass(child));
		}
		
		return root;
	}
	
	public ClassNode parseClass(Element element) throws ParserException{
		assertNodeTag(element.getQualifiedName(), CLASS_NODE_NAME);
		String name = getElementName(element);
		
		ClassNode _class = new ClassNode(name);
		
		for(Element child : getIterableChildren(element)){
			_class.addMethod(parseMethod(child));
		}
		
		return _class;
	}

	public MethodNode parseMethod(Element element) throws ParserException{
		assertNodeTag(element.getQualifiedName(), METHOD_NODE_NAME);
		String name = getElementName(element);
		
		MethodNode method = new MethodNode(name);
		
		return method;
	}

	public PartitionNode parsePartition(Element element) throws ParserException{
		String name = getElementName(element);
		String value = getAttributeValue(element, VALUE_ATTRIBUTE);
		
		PartitionNode partition = new PartitionNode(name, value);
		
		for(Element child : getIterableChildren(element)){
			if(child.getLocalName() == Constants.PARTITION_NODE_NAME){
				partition.addPartition(parsePartition(child));
			}
			if(child.getLocalName() == Constants.LABEL_NODE_NAME){
				partition.addLabel(child.getAttributeValue(Constants.LABEL_ATTRIBUTE_NAME));
			}
		}

		return partition;
	}

	private void assertNodeTag(String qualifiedName, String expectedName) throws ParserException {
		if(qualifiedName.equals(expectedName) == false){
			throw new ParserException("Unexpected node name: " + qualifiedName + " instead of " + expectedName);
		}
	}
	
	protected List<Element> getIterableChildren(Element element){
		ArrayList<Element> list = new ArrayList<Element>();
		Elements children = element.getChildElements();
		for(int i = 0; i < children.size(); i++){
			Node node = children.get(i);
			if(node instanceof Element){
				list.add((Element)node);
			}
		}
		return list;
	}

	protected String getElementName(Element element) throws ParserException {
		String name = element.getAttributeValue(Constants.NODE_NAME_ATTRIBUTE);
		if(name == null){
			throw new ParserException(Messages.MISSING_ATTRIBUTE(element, Constants.NODE_NAME_ATTRIBUTE));
		}
		return name;
	}

	protected String getAttributeValue(Element element, String attributeName) throws ParserException{
		String value = element.getAttributeValue(attributeName);
		if(value == null){
			throw new ParserException(Messages.MISSING_ATTRIBUTE(element, attributeName));
		}
		return value;
	}


}
