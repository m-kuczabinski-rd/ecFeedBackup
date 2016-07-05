package com.ecfeed.spec.gui;

import com.ecfeed.testutils.ETypeName;

public class TestChoiceDetailsPage{

	public void changeChoiceNameTest(String newName, boolean isDuplicate, boolean validation, boolean isTopLevel){
		// TODO Auto-generated method stub
		System.out.println("changeChoiceNameTest(" + newName + ", " + isDuplicate + ", " + validation + ", " + isTopLevel + ")");
	}

	public void changeChoiceValueTest(ETypeName type, String newValue, boolean isConvertable){
		// TODO Auto-generated method stub
		System.out.println("changeChoiceValueTest(" + type + ", " + newValue + ", " + isConvertable + ")");
	}

	public void predefinedValuesTest(ETypeName type, String value){
		// TODO Auto-generated method stub
		System.out.println("predefinedValuesTest(" + type + ", " + value + ")");
	}

	/*
	 * Test adding at least 3 in a row to ensure names are properly changed to not duplicate.
	 */
	public void addChoiceTest(){
		// TODO Auto-generated method stub
		System.out.println("addChoiceTest()");
	}

	public void removeSelectedChoicesTest(ESelectionType selectionType){
		// TODO Auto-generated method stub
		System.out.println("removeSelectedChoicesTest(" + selectionType + ")");
	}

	/*
	 * test for choice viewer
	 */
	public void changeChoiceNameViewerTest(String newName, boolean isDuplicate, boolean validation){
		// TODO Auto-generated method stub
		System.out.println("changeChoiceNameViewerTest(" + newName + ", " + isDuplicate + ", " + validation + ")");
	}

	/*
	 * test for choice viewer
	 */
	public void changeChoiceValueViewerTest(ETypeName type, String newValue, boolean isExpected, boolean isConvertable){
		// TODO Auto-generated method stub
		System.out.println("changeChoiceValueViewerTest(" + type + ", " + newValue + ", " + isExpected + ", " + isConvertable + ")");
	}

	
	public void uniqueLabelsTest(boolean isInherited){
		// TODO Auto-generated method stub
		System.out.println("uniqueLabelsTest(" + isInherited + ")");
	}

	/*
	 * Fixed test - test the following cases:
	 * - create choice, add label to it then add children choice. 
	 * - create choice, add children choice, then add label
	 * - create label in children choice and ensure it doesn't affect parent
	 * - ensure that editing and removing is possible only in top level labels
	 */
	public void labelInheritanceTest(){
		// TODO Auto-generated method stub
		System.out.println("labelInheritanceTest()");
	}



}

