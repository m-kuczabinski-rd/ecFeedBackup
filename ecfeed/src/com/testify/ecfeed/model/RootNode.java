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

public class RootNode extends ParametersParentNode {
	public List<ClassNode> fClasses;

	@Override
	public List<? extends AbstractNode> getChildren(){
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

	public boolean addClass(ClassNode node){
		return addClass(node, fClasses.size());
	}

	public boolean addClass(ClassNode node, int index){
		if(index >= 0 && index <= fClasses.size()){
			fClasses.add(index, node);
			node.setParent(this);
			return fClasses.indexOf(node) == index;
		}
		return false;
	}

	public List<ClassNode> getClasses() {
		return fClasses;
	}

	public ClassNode getClassModel(String name) {
		for(ClassNode childClass : getClasses()){
			if(childClass.getName().equals(name)){
				return childClass;
			}
		}
		return null;
	}

	public boolean removeClass(ClassNode classNode){
		return fClasses.remove(classNode);
	}

	@Override
	public boolean compare(AbstractNode node){
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
	public Object accept(IModelVisitor visitor) throws Exception{
		return visitor.visit(this);
	}

}
