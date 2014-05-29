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

package com.testify.ecfeed.model;

import java.util.ArrayList;
import java.util.List;

import static com.testify.ecfeed.model.Constants.*;

public class RootNode extends GenericNode {
	public List<ClassNode> fClasses;

	public RootNode(String name) {
		super(name);
		fClasses = new ArrayList<ClassNode>();
	}
	
	public List<? extends IGenericNode> getChildren(){
		return fClasses;
	}

	public void addClass(ClassNode node){
		fClasses.add(node);
		node.setParent(this);
	}

	public List<ClassNode> getClasses() {
		return fClasses;
	}
	
	public ClassNode getClassModel(String name) {
		for(ClassNode childClass : getClasses()){
			if(childClass.getQualifiedName().equals(name)){
				return childClass;
			}
		}
		return null;
	}

	public boolean removeClass(ClassNode classNode){
		return fClasses.remove(classNode);
	}

	public static boolean validateModelName(String name){
		return name.matches(ROOT_NODE_NAME_REGEX);
	}
	
	public Object convert(IConverter converter){
		return converter.convert(this);
	}
}
