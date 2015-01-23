package com.testify.ecfeed.spec.gui;

import com.testify.ecfeed.testutils.ETypeName;


public class TestParameterDetailsPage{
	
	public enum EUserType{
		ENUM_ALL_IMPLEMENTED, UNIMPLEMENTED, ENUM_NOT_ALL_IMPLEMENTED, NON_ENUM_IMPLEMENTED;
	}


	public void implementParameterTest(EUserType userType){
		// TODO Auto-generated method stub
		System.out.println("implementParameterTest(" + userType + ")");
	}
	

	public void changeParameterNameTest(String newName, boolean isDuplicate, boolean validation){
		// TODO Auto-generated method stub
		System.out.println("changeParameterNameTest(" + newName + ", " + isDuplicate + ", " + validation + ")");
	}


	public void changeParameterTypeTest(ETypeName oldType, ETypeName newType, String originalValue, boolean isExpected, boolean isConvertable){
		// TODO Auto-generated method stub
		System.out.println("changeParameterTypeTest(" + oldType + ", " + newType + ", " + originalValue + ", " + isExpected + ", " + isConvertable + ")");
	}


	public void changeParametersExpectedStatusTest(boolean isExpected, ETypeName type, String defaultValues){
		// TODO Auto-generated method stub
		System.out.println("changeParametersExpectedStatusTest(" + isExpected + ", " + type + ", " + defaultValues + ")");
	}


	public void changeUserTypeParameterExpectedStatus(boolean toExpected, boolean hasChoices, boolean isImplemented){
		// TODO Auto-generated method stub
		System.out.println("changeUserTypeParameterExpectedStatus(" + toExpected + ", " + hasChoices + ", " + isImplemented + ")");
	}

	/*
	 * Two birds with one stone - if isExpected, then test default value change;
	 * If not, test choice value change.
	 */
	public void changeChoiceValueTest(ETypeName type, String newValue, boolean isExpected, boolean isConvertable){
		// TODO Auto-generated method stub
		System.out.println("changeChoiceValueTest(" + type + ", " + newValue + ", " + isExpected + ", " + isConvertable + ")");
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


	public void changeChoiceNameTest(String newName, boolean isDuplicate, boolean validation){
		// TODO Auto-generated method stub
		System.out.println("changeChoiceNameTest(" + newName + ", " + isDuplicate + ", " + validation + ")");
	}

}
