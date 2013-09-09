/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.model;

import java.util.Vector;

public class RootNode extends GenericNode {

	public RootNode(String name) {
		super(name);
	}
	
	public void addClass(ClassNode node){
		addChild(node);
	}

	public Vector<ClassNode> getClasses() {
		Vector<ClassNode> classes = new Vector<ClassNode>();
		for(GenericNode child : getChildren()){
			if(child instanceof ClassNode){
				classes.add((ClassNode)child);
			}
		}
		return classes;
	}

	//TODO unit tests
	public ClassNode getClassModel(String name) {
		for(ClassNode childClass : getClasses()){
			if(childClass.getQualifiedName().equals(name)){
				return childClass;
			}
		}
		return null;
	}
}
