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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class GenericNode implements IGenericNode{
	private String fName;
	private IGenericNode fParent;
	private final int fId;
	private static int fLastId = 0;
	protected final ArrayList<IGenericNode> EMPTY_CHILDREN_ARRAY = new ArrayList<IGenericNode>();

	public GenericNode(String name){
		fId = ++fLastId;
		this.fName = name;
	}
	
	public int getId(){
		return fId;
	}
	
	@Override
	public String getName() {
		return fName;
	}

	@Override
	public void setName(String name) {
		this.fName = name;
	}
	
	@Override
	public void setParent(IGenericNode newParent) {
		fParent = newParent;
	}

	@Override
	public List<? extends IGenericNode> getChildren() {
		return EMPTY_CHILDREN_ARRAY;
	}
	
	@Override
	public boolean hasChildren(){
		if(getChildren() != null){
			return (getChildren().size() > 0);
		}
		return false;
	}
	
	@Override
	public IGenericNode getParent(){
		return fParent;
	}
	
	@Override
	public IGenericNode getRoot(){
		if(getParent() == null){
			return this;
		}
		return getParent().getRoot();
	}

	@Override
	public IGenericNode getChild(String qualifiedName) {
		String[] tokens = qualifiedName.split(":");
		if(tokens.length == 0){
			return null;
		}
		if(tokens.length == 1){
			for(IGenericNode child : getChildren()){
				if(child.getName().equals(tokens[0])){
					return child;
				}
			}
		}
		else{
			IGenericNode nextChild = getChild(tokens[0]);
			if(nextChild == null) return null;
			tokens = Arrays.copyOfRange(tokens, 1, tokens.length);
			String newName = qualifiedName.substring(qualifiedName.indexOf(":") + 1);
			return nextChild.getChild(newName);
		}
		return null;
	}

	@Override
	public IGenericNode getSibling(String name){
		if(getParent() == null) return null;
		for(IGenericNode sibling : getParent().getChildren()){
			if(sibling.getName().equals(name) && sibling != this){
				return sibling;
			}
		}
		return null;
	}
	
	@Override
	public boolean hasSibling(String name){
		return getSibling(name) != null;
	}
	
	@Override
	public boolean moveChild(IGenericNode child, boolean moveUp) {
		int childIndex = getChildren().indexOf(child);
		if(moveUp && childIndex > 0){
			Collections.swap(getChildren(), childIndex, childIndex - 1);
			return true;
		}
		if(!moveUp && childIndex < getChildren().size() - 1){
			Collections.swap(getChildren(), childIndex, childIndex + 1);
			return true;
		}
		return false;
	}
	
	@Override
	public int subtreeSize(){
		int size = 1;
		for(IGenericNode child : getChildren()){
			size += child.subtreeSize();
		}
		return size;
	}

	@Override
	public String toString(){
		return getName();
	}

	@Override
	public boolean equals(Object obj){
		if(obj instanceof GenericNode){
			return ((GenericNode)obj).getId() == fId;
		}
		return false;
	}
	
	@Override
	public boolean compare(IGenericNode node){
		return getName().equals(node.getName());
	}
}
