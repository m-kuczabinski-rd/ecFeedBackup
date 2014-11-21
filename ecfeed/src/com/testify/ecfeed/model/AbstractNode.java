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
import java.util.List;

public abstract class AbstractNode{
	private String fName;
	private AbstractNode fParent;
	private final int fId;
	private static int fLastId = 0;
	protected final List<AbstractNode> EMPTY_CHILDREN_ARRAY = new ArrayList<AbstractNode>();

	public AbstractNode(String name){
		fId = ++fLastId;
		this.fName = name;
	}
	
	public int getId(){
		return fId;
	}
	
	public int getIndex(){
		if(getParent() == null){
			return -1;
		}
		return getParent().getChildren().indexOf(this);
	}
	
	public String getName() {
		return fName;
	}

	public void setName(String name) {
		fName = name;
	}
	
	public void setParent(AbstractNode newParent) {
		fParent = newParent;
	}

	public List<? extends AbstractNode> getChildren() {
		return EMPTY_CHILDREN_ARRAY;
	}
	
	public boolean hasChildren(){
		if(getChildren() != null){
			return (getChildren().size() > 0);
		}
		return false;
	}
	
	public List<AbstractNode> getAncestors(){
		List<AbstractNode> ancestors;
		AbstractNode parent = getParent();
		if(parent != null){
			ancestors = parent.getAncestors();
			ancestors.add(parent);
		}
		else{
			ancestors = new ArrayList<>();
		}
		return ancestors;
	}
	
	public AbstractNode getParent(){
		return fParent;
	}
	
	public AbstractNode getRoot(){
		if(getParent() == null){
			return this;
		}
		return getParent().getRoot();
	}

	public AbstractNode getChild(String qualifiedName) {
		String[] tokens = qualifiedName.split(":");
		if(tokens.length == 0){
			return null;
		}
		if(tokens.length == 1){
			for(AbstractNode child : getChildren()){
				if(child.getName().equals(tokens[0])){
					return child;
				}
			}
		}
		else{
			AbstractNode nextChild = getChild(tokens[0]);
			if(nextChild == null) return null;
			tokens = Arrays.copyOfRange(tokens, 1, tokens.length);
			String newName = qualifiedName.substring(qualifiedName.indexOf(":") + 1);
			return nextChild.getChild(newName);
		}
		return null;
	}

	public AbstractNode getSibling(String name){
		if(getParent() == null) return null;
		for(AbstractNode sibling : getParent().getChildren()){
			if(sibling.getName().equals(name) && sibling != this){
				return sibling;
			}
		}
		return null;
	}
	
	public boolean hasSibling(String name){
		return getSibling(name) != null;
	}
	
	public int subtreeSize(){
		int size = 1;
		for(AbstractNode child : getChildren()){
			size += child.subtreeSize();
		}
		return size;
	}

	public String toString(){
		return getName();
	}

	public boolean equals(Object obj){
		if(obj instanceof AbstractNode){
			return ((AbstractNode)obj).getId() == fId;
		}
		return false;
	}
	
	public boolean compare(AbstractNode node){
		return getName().equals(node.getName());
	}

	public int getMaxIndex() {
		if(getParent() != null){
			return getParent().getChildren().size();
		}
		return -1;
	}
	
	public abstract AbstractNode getCopy();
	public abstract Object accept(IModelVisitor visitor) throws Exception;

	public int getMaxChildIndex(AbstractNode potentialChild) {
		return getChildren().size();
	}
}
