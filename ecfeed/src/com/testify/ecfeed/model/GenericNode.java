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

import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

public class GenericNode {
	private String fName;
	private Vector<GenericNode> fChildren;
	private GenericNode fParent;

	public GenericNode(String name){
		this.fName = name;
		this.fChildren = new Vector<GenericNode>();
	}
	
	public String getName() {
		return fName;
	}

	public void setName(String name) {
		this.fName = name;
	}
	
	//TODO unit tests
	public void addChild(GenericNode child){
		addChild(fChildren.size(), child);
	}

	//TODO Unit tests
	public void addChild(int index, GenericNode child) {
		if(!isParent(child)){
			fChildren.add(index, child);
			child.setParent(this);
		}
	}

	//Should not be used explicitly
	public void setParent(GenericNode newParent) {
		fParent = newParent;
		if(newParent != null){
			newParent.addChild(this);
		}
	}

	public Vector<GenericNode> getChildren() {
		return fChildren;
	}
	
	public boolean hasChildren(){
		if(getChildren() != null){
			return (getChildren().size() > 0);
		}
		return false;
	}
	
	public GenericNode getParent(){
		return fParent;
	}
	
	public GenericNode getRoot(){
		if(getParent() == null){
			return this;
		}
		return getParent().getRoot();
	}

	@Override
	public String toString(){
		return getName();
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof GenericNode == false){
			return false;
		}
		GenericNode node = (GenericNode)obj;
		if (!fName.equals(node.getName())){
			return false;
		}
		int childrenCount = getChildren().size();
		int nodeChildrenCount = node.getChildren().size();
		if(childrenCount != nodeChildrenCount){
			return false;
		}
		
		Vector<GenericNode> nodeChildren = node.getChildren();
		for(int i = 0; i < childrenCount; i++){
			GenericNode thisChild = getChildren().elementAt(i);
			GenericNode nodeChild = nodeChildren.elementAt(i);
			if(!thisChild.equals(nodeChild)){
				return false;
			}
		}
		return true;
	}

	public boolean removeChild(GenericNode child) {
		boolean result = fChildren.remove(child);
		if(result){
			child.setParent(null);
		}
		return result;
	}
	
	public boolean removeChildren(Collection<? extends GenericNode> children){
		return fChildren.removeAll(children);
	}
	
	protected boolean isParent(GenericNode potentialChild){
		for(GenericNode child : fChildren){
			if(child == potentialChild){
				return true;
			}
		}
		return false;
	}

	//TODO unit tests
	public GenericNode getChild(String name) {
		for(GenericNode child : fChildren){
			if (name.equals(child.getName())){
				return child;
			}
		}
		return null;
	}

	//TODO unit tests
	public void moveChild(GenericNode child, boolean moveUp) {
		int childIndex = fChildren.indexOf(child);
		if(moveUp && childIndex > 0){
			Collections.swap(fChildren, childIndex, childIndex - 1);
		}
		if(!moveUp && childIndex < fChildren.size() - 1){
			Collections.swap(fChildren, childIndex, childIndex + 1);
		}
	}
}
