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
	public Vector<ClassNode> fClasses;

	public RootNode(String name) {
		super(name);
		fClasses = new Vector<ClassNode>();
	}
	
	public void addClass(ClassNode node){
		fClasses.add(node);
		node.setParent(this);
	}

	public Vector<? extends IGenericNode> getChildren(){
		return fClasses;
	}
	
	public Vector<ClassNode> getClasses() {
		return fClasses;
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
