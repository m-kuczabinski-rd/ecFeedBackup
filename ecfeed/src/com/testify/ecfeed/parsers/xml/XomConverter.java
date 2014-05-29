package com.testify.ecfeed.parsers.xml;

import static com.testify.ecfeed.parsers.Constants.*;
import nu.xom.Attribute;
import nu.xom.Element;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.IConverter;
import com.testify.ecfeed.model.IGenericNode;
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

	public Object convert(ClassNode node) {
		return createNamedElement(CLASS_NODE_NAME, node);
	}

	private Element createNamedElement(String nodeTag, IGenericNode node){
		Element element = new Element(nodeTag);
		Attribute nameAttr = new Attribute(NODE_NAME_ATTRIBUTE, node.getName());
		element.addAttribute(nameAttr);
		return element;
	}
	
}
