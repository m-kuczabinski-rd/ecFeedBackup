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

import static com.testify.ecfeed.modelif.Constants.*;

public class RootNode extends GenericNode {
	public List<ClassNode> fClasses;

	@Override
	public List<? extends IGenericNode> getChildren(){
		return fClasses;
	}
	
	@Override
	public RootNode getCopy(){
		RootNode copy = new RootNode(this.getName());

		for(ClassNode classnode : fClasses){
			copy.addClass(classnode.getCopy());
		}
		copy.setParent(this.getParent());
		return copy;
	}
	
	public RootNode(String name) {
		super(name);
		fClasses = new ArrayList<ClassNode>();
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
		return name.matches(REGEX_ROOT_NODE_NAME);
	}
	
	@Override
	public boolean compare(IGenericNode node){
		if(node instanceof RootNode == false){
			return false;
		}

		RootNode root = (RootNode)node;
		if(getClasses().size() != root.getClasses().size()){
			return false;
		}
		
		for(int i = 0; i < getClasses().size(); i++){
			if(getClasses().get(i).compare(root.getClasses().get(i)) == false){
				return false;
			}
		}
		
		return super.compare(root);
	}
	
	@Override
	public Object accept(IModelVisitor converter) throws Exception{
		return converter.visit(this);
	}
	
}
