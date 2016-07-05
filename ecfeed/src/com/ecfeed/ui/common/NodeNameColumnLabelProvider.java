/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.common;

import com.ecfeed.core.model.AbstractNode;

public class NodeNameColumnLabelProvider extends NodeViewerColumnLabelProvider {

	public NodeNameColumnLabelProvider() {
		super();
	}

	private final String EMPTY_STRING = "";
	
	@Override
	public String getText(Object element) {
		if(element instanceof AbstractNode){
			return ((AbstractNode)element).getName();
		}
		return EMPTY_STRING;
	}

}
