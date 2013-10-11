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
import java.util.List;

public interface IGenericNode {
	public boolean hasChildren();
	public List<? extends IGenericNode> getChildren();
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
