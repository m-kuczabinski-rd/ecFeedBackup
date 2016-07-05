/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/
package com.ecfeed.spec.gui;

import com.ecfeed.testutils.ENodeType;

public class CommentSection{

	public void addComment(ENodeType node, String text){
		// TODO Auto-generated method stub
		System.out.println("addComment(" + node + ", " + text + ")");
	}

	public void exportJavaDoc(ENodeType node, String text, boolean isApplicable){
		// TODO Auto-generated method stub
		System.out.println("exportJavaDoc(" + node + ", " + text + ", " + isApplicable + ")");
	}

	public void importJavaDoc(String text, ENodeType node){
		// TODO Auto-generated method stub
		System.out.println("importJavaDoc(" + text + ", " + node + ")");
	}
}

