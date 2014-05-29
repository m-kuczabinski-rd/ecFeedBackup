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

import java.util.List;

public interface IGenericNode {
	
	public boolean hasChildren();
	public List<? extends IGenericNode> getChildren();
	public IGenericNode getChild(String name);
	
	/*
	 * Returns a sibling node with the provided name. If the node is the only one 
	 * with the name, returns null
	 */
	public IGenericNode getSibling(String name);
	
	/*
	 * Returns true if there is a sibling node with provided name 
	 */
	public boolean hasSibling(String name);
	
	public void moveChild(IGenericNode child, boolean moveUp);
	public IGenericNode getRoot();
	public IGenericNode getParent();
	public void setParent(IGenericNode parent);
	public String getName();
	public void setName(String newName);
	public int subtreeSize();
	
	public boolean compare(IGenericNode node);
}
