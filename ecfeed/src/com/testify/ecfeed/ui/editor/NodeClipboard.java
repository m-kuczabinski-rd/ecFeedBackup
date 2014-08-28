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

import com.testify.ecfeed.model.GenericNode;

public class NodeClipboard{

	private GenericNode fClipboardNode = null;
	private GenericNode fOriginalNode = null;

	public GenericNode getClipboardNode(){
		return fClipboardNode;
	}

	public GenericNode getOriginalNode(){
		return fOriginalNode;
	}

	public void setClipboardNode(GenericNode node){
		fClipboardNode = node.getCopy();
		fOriginalNode = node;
	}

}
