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

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class ClassNode extends GenericNode {
	private Vector<MethodNode> fMethods;

	public ClassNode(String qualifiedName) {
		super(qualifiedName);
		fMethods = new Vector<MethodNode>();
	}

	public String getQualifiedName() {
		return super.getName();
	}
	
	public String getLocalName(){
		return getLocalName(getName());
	}

	//TODO unit tests
	public void addMethod(MethodNode method) {
		super.addChild(method);
		fMethods.add(method);
	}
	
	//TODO unit tests
	public boolean removeChild(MethodNode method){
		fMethods.remove(method);
		return super.removeChild(method);
	}
	
	private String getLocalName(String qualifiedName){
		int lastDotIndex = qualifiedName.lastIndexOf('.');
		return (lastDotIndex == -1)?qualifiedName: qualifiedName.substring(lastDotIndex + 1);
	}
	
	public String toString(){
		return getLocalName();
	}

	//TODO unit tests
	public MethodNode getMethod(String name, Vector<String> argTypes) {
		for(MethodNode methodNode : getMethods()){
			Vector<String> args = new Vector<String>();
			for(CategoryNode arg : methodNode.getCategories()){
				args.add(arg.getType());
			}
			if(methodNode.getName().equals(name) && args.equals(argTypes)){
				return methodNode;
			}
		}
		return null;
	}

	//TODO unit tests
	public Vector<MethodNode> getMethods() {
		return fMethods;
	}
	
	//TODO uni tests
	public Set<String> getTestSuites(){
		Set<String> suites = new HashSet<String>();
		for(MethodNode method : getMethods()){
			suites.addAll(method.getTestSuites());
		}
		return suites;
	}
}
