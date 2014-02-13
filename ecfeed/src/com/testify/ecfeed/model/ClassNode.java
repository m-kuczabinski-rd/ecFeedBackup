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

	public String getLocalName(){
		return getLocalName(getName());
	}

	public String getQualifiedName() {
		return super.getName();
	}
	
	public void addMethod(MethodNode method) {
		fMethods.add(method);
		method.setParent(this);
	}
	
	public MethodNode getMethod(String name, List<String> argTypes) {
		for(MethodNode methodNode : getMethods()){
			List<String> args = new ArrayList<String>();
			for(CategoryNode arg : methodNode.getCategories()){
				args.add(arg.getType());
			}
			if(methodNode.getName().equals(name) && args.equals(argTypes)){
				return methodNode;
			}
		}
		return null;
	}

	public List<MethodNode> getMethods() {
		return fMethods;
	}
	
	public RootNode getRoot(){
		return (RootNode) getParent();
	}
	
	public boolean removeMethod(MethodNode method) {
		return fMethods.remove(method);
	}

	public Set<String> getTestSuites(){
		Set<String> suites = new HashSet<String>();
		for(MethodNode method : getMethods()){
			suites.addAll(method.getTestSuites());
		}
		return suites;
	}

	@Override
	public List<? extends IGenericNode> getChildren(){
		return fMethods;
	}

	@Override
	public String toString(){
		return getLocalName();
	}

	private String getLocalName(String qualifiedName){
		int lastDotIndex = qualifiedName.lastIndexOf('.');
		return (lastDotIndex == -1)?qualifiedName: qualifiedName.substring(lastDotIndex + 1);
	}
}
