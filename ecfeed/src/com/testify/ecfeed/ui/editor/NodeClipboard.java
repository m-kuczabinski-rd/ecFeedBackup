/*******************************************************************************
 * Copyright (c) 2014 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor;

import com.testify.ecfeed.model.IGenericNode;

public class NodeClipboard{

	private IGenericNode fClipboardNode = null;
	private IGenericNode fOriginalNode = null;

	public IGenericNode getClipboardNode(){
		return fClipboardNode;
	}

	public IGenericNode getOriginalNode(){
		return fOriginalNode;
	}

	public void setClipboardNode(IGenericNode node){
		fClipboardNode = node.getCopy();
		fOriginalNode = node;
	}

}
