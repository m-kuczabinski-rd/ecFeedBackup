package com.testify.ecfeed.parsers.xml;

import static com.testify.ecfeed.parsers.Constants.*;
import nu.xom.Attribute;
import nu.xom.Element;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.IConverter;
import com.testify.ecfeed.model.IGenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.RootNode;

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

	private Element createNamedElement(String nodeTag, IGenericNode node){
		Element element = new Element(nodeTag);
		Attribute nameAttr = new Attribute(NODE_NAME_ATTRIBUTE, node.getName());
		element.addAttribute(nameAttr);
		return element;
	}
	
}
