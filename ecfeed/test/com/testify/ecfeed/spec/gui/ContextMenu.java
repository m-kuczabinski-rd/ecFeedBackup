package com.testify.ecfeed.spec.gui;

import com.testify.ecfeed.testutils.ENodeType;

public class ContextMenu{

	public void addChildTest(ENodeType nodeType, ESelectionType selectionType){
		// TODO Auto-generated method stub
		System.out.println("addChildTest(" + nodeType + ", " + selectionType + ")");
	}

	public void copyTest(ENodeType sourceType, ESelectionType selectionType){
		// TODO Auto-generated method stub
		System.out.println("copyTest(" + sourceType + ", " + selectionType + ")");
	}

	public void pasteSelectionPossibilityTest(ESelectionType selectionType, ENodeType targetType){
		// TODO Auto-generated method stub
		System.out.println("pasteSelectionPossibilityTest(" + selectionType + ", " + targetType + ")");
	}

	public void pastePossibilityTest(ENodeType targetType, ENodeType sourceType){
		// TODO Auto-generated method stub
		System.out.println("pastePossibilityTest(" + targetType + ", " + sourceType + ")");
	}

	public void deleteTest(){
		// TODO Auto-generated method stub
		System.out.println("deleteTest()");
	}

	public void selectAllTest(){
		// TODO Auto-generated method stub
		System.out.println("selectAllTest()");
	}

	public void expandCollapseTest(){
		// TODO Auto-generated method stub
		System.out.println("expandCollapseTest()");
	}

	public void MoveUpDownTest(){
		// TODO Auto-generated method stub
		System.out.println("MoveUpDownTest()");
	}

	public void pasteDuplicateNameTest(ENodeType nodeType, boolean duplicateName){
		// TODO Auto-generated method stub
		System.out.println("pasteDuplicateNameTest(" + nodeType + ", " + duplicateName + ")");
	}

	public void pasteBulkAtomicTest(ENodeType nodeType, ENodeValidationStatus validNodes){
		// TODO Auto-generated method stub
		System.out.println("pasteBulkAtomicTest(" + nodeType + ")");
	}

	public void pasteNodesTest(ENodeType sourceType){
		// TODO Auto-generated method stub
		System.out.println("pasteNodesTest(" + sourceType + ")");
	}

	public void cutTest(ENodeType nodeType, ESelectionType selectionType){
		// TODO Auto-generated method stub
		System.out.println("cutTest(" + nodeType + ", " + selectionType + ")");
	}

}

