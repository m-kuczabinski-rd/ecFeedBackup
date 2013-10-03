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
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

public class ClassNode extends GenericNode {
	private List<MethodNode> fMethods;

	public ClassNode(String qualifiedName) {
		super(qualifiedName);
		fMethods = new ArrayList<MethodNode>();
	}

	@Override
	public List<? extends IGenericNode> getChildren(){
		return fMethods;
	}
	
	public String getQualifiedName() {
		return super.getName();
	}
	
	public String getLocalName(){
		return getLocalName(getName());
	}

	//TODO unit tests
	public void addMethod(MethodNode method) {
		fMethods.add(method);
		method.setParent(this);
	}
	
	private String getLocalName(String qualifiedName){
		int lastDotIndex = qualifiedName.lastIndexOf('.');
		return (lastDotIndex == -1)?qualifiedName: qualifiedName.substring(lastDotIndex + 1);
	}
	
	@Override
	public String toString(){
		return getLocalName();
	}

	//TODO unit tests
	public MethodNode getMethod(String name, ArrayList<String> argTypes) {
		for(MethodNode methodNode : getMethods()){
			ArrayList<String> args = new ArrayList<String>();
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
	public List<MethodNode> getMethods() {
		return fMethods;
	}
	
	//TODO unit tests
	public Set<String> getTestSuites(){
		Set<String> suites = new HashSet<String>();
		for(MethodNode method : getMethods()){
			suites.addAll(method.getTestSuites());
		}
		return suites;
	}
}
