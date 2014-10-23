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

package com.testify.ecfeed.ui.modelif;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.testify.ecfeed.model.GenericNode;

public class NodeClipboard{

	private static List<GenericNode> fClipboardNodes = new ArrayList<>();

	public static List<GenericNode> getContent(){
		return fClipboardNodes;
	}
	
	public static List<GenericNode> getContentCopy(){
		List<GenericNode> copy = new ArrayList<GenericNode>();
		for(GenericNode node : fClipboardNodes){
			copy.add(node.getCopy());
		}
		return copy;
	}
	
	public static void setContent(GenericNode node){
		fClipboardNodes.clear();
		fClipboardNodes.add(node.getCopy());
	}
	
	public static void setContent(List<GenericNode> nodes){
		fClipboardNodes.clear();
		for(GenericNode node : nodes){
			if(isPredecessorInCollection(node, nodes) == false){
				fClipboardNodes.add(node.getCopy());
			}
		}
	}
	
	private static boolean isPredecessorInCollection(GenericNode node, Collection<GenericNode> nodes) {
		GenericNode predecessor = node.getParent();
		while(predecessor != null){
			if(nodes.contains(predecessor)){
				return true;
			}
			predecessor = predecessor.getParent();
		}
		return false;
	}
}
