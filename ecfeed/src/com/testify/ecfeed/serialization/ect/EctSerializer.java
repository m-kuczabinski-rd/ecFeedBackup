/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.serialization.ect;

import java.io.IOException;
import java.io.OutputStream;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import com.testify.ecfeed.model.ParameterNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.serialization.IModelSerializer;

public class EctSerializer implements IModelSerializer{

	private OutputStream fOutputStream;
	private XomBuilder fConverter;

	public EctSerializer(OutputStream ostream){
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

	public Object serialize(ParameterNode node) throws Exception {
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
