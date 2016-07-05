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

