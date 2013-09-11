package com.testify.ecfeed.model;

import java.util.Collection;
import java.util.Vector;

public interface IGenericNode {
	public boolean hasChildren();
	public Vector<? extends IGenericNode> getChildren();
	public IGenericNode getChild(String name);
	public boolean removeChild(IGenericNode child);
	public boolean removeChildren(Collection<IGenericNode> children);
	public void moveChild(IGenericNode child, boolean moveUp);
	
	public IGenericNode getRoot();
	
	public IGenericNode getParent();
	public void setParent(IGenericNode parent);
	public boolean isParent(IGenericNode potentialChild);
	
	public String getName();
	public void setName(String newName);
	
	public int subtreeSize();
}
