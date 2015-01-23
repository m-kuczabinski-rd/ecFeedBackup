package com.testify.ecfeed.spec.gui;

public class TestClassDetailsPage{

	/*
	 * Test if button is disabled for implemented and enabled otherwise.
	 * Also, tests if it does properly implement class. No need for parameter.
	 */
	public void implementClassTest(){
		// TODO Auto-generated method stub
		System.out.println("implementClassTest()");
	}

	public void changePackageTest(String new_package_name){
		// TODO Auto-generated method stub
		System.out.println("changePackageTest(" + new_package_name + ")");
	}

	/*
	 * Test if:
	 * - class name and package did change
	 * - ok button is disabled, when no class is selected
	 * - error is displayed, when already contained class is selected
	 * - class model remains unchanged
	 * - the implementation status of class and methods is updated, along with "other methods" section
	 * There is no reason to make these boolean or any conditions. They are not really correlated.
	 * Thus I guess it will be fixed parameterless test case.
	 */
	public void reassignClassTest(){
		// TODO Auto-generated method stub
		System.out.println("reassignClassTest()");
	}

	public void addNewMethod(boolean duplicate){
		// TODO Auto-generated method stub
		System.out.println("addNewMethod(" + duplicate + ")");
	}

	public void removeSelectedMethodsTest(ESelectionType selectionType){
		// TODO Auto-generated method stub
		System.out.println("removeSelectedMethodsTest(" + selectionType + ")");
	}

	public void OtherMethodsViewerTest(boolean doesMethodExist, boolean doesReturnVoid, boolean isConstructor, boolean isParameterless){
		// TODO Auto-generated method stub
		System.out.println("OtherMethodsViewerTest(" + doesMethodExist + ", " + doesReturnVoid + ", " + isConstructor + ", " + isParameterless + ")");
	}
	
	public void OtherMethodsViewerTest2(boolean doesMethodExist, boolean doesReturnVoid, boolean isConstructor, boolean isParameterless){
		// TODO Auto-generated method stub
		System.out.println("OtherMethodsViewerTest(" + doesMethodExist + ", " + doesReturnVoid + ", " + isConstructor + ", " + isParameterless + ")");
	}

	public void renameMethodTest(String new_name, boolean isValid){
		// TODO Auto-generated method stub
		System.out.println("renameMethodTest(" + new_name + ", " + isValid + ")");
	}

	public void renameClassTest(String new_name, boolean isValid){
		// TODO Auto-generated method stub
		System.out.println("renameClassTest(" + new_name + ", " + isValid + ")");
	}

}

