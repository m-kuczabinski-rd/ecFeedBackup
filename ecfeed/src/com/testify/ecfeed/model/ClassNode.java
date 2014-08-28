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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassNode extends GenericNode {
	private List<MethodNode> fMethods;
	
	@Override
	public List<? extends GenericNode> getChildren(){
		return fMethods;
	}

	@Override
	public String toString(){
		return getLocalName();
	}
	
	@Override
	public ClassNode getCopy(){
		ClassNode copy = new ClassNode(getQualifiedName());
		for(MethodNode method : fMethods){
			copy.addMethod(method.getCopy());
		}
		copy.setParent(getParent());
		return copy;
	}
	
	@Override
	public RootNode getRoot(){
		return (RootNode) getParent();
	}

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
	
	public boolean addMethod(MethodNode method) {
		return addMethod(method, fMethods.size());
	}
	
	public boolean addMethod(MethodNode method, int index) {
		if(index >= 0 && index <= fMethods.size()){
			fMethods.add(index, method);
			method.setParent(this);
			return fMethods.indexOf(method) == index;
		}
		return false;
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

	private String getLocalName(String qualifiedName){
		int lastDotIndex = qualifiedName.lastIndexOf('.');
		return (lastDotIndex == -1)?qualifiedName: qualifiedName.substring(lastDotIndex + 1);
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception {
		return visitor.visit(this);
	}
	
	@Override
	public boolean compare(GenericNode node){
		if(node instanceof ClassNode == false){
			return false;
		}
		ClassNode compared = (ClassNode) node;
		List<MethodNode> comparedMethods = compared.getMethods();
		
		if(getMethods().size() != comparedMethods.size()){
			return false;
		}
		
		for(int i = 0; i < comparedMethods.size(); i++){
			if(getMethods().get(i).compare(comparedMethods.get(i)) == false){
				return false;
			}
		}
		
		return super.compare(node);
	}
}
