package com.testify.ecfeed.spec.gui;

import com.testify.ecfeed.testutils.ETypeName;

public class ModelDetailsPage{

	public void initialModelNameTest(String filename){
		// TODO Auto-generated method stub
		System.out.println("initialModelNameTest(" + filename + ")");
	}

	public void modelNameChange(String new_name){
		// TODO Auto-generated method stub
		System.out.println("modelNameChange(" + new_name + ")");
	}

	public void implementationTest(EChildrenImplementationStatus nodesStatus){
		// TODO Auto-generated method stub
		System.out.println("implementationTest(" + nodesStatus + ")");
	}

	public void renameClass(String new_name){
		// TODO Auto-generated method stub
		System.out.println("renameClass(" + new_name + ")");
	}

	public void renamePackage(String new_package_name){
		// TODO Auto-generated method stub
		System.out.println("renamePackage(" + new_package_name + ")");
	}

	/*
	 * Parameters are just requirements to fulfill while creating methods in the class. Check integrity of imported methods.
	 * Also it shows how nice would be "at least one" or "no more than X like..." constraint.
	 * In this case I need "no more than 1 like: hasParameterlessMethods=false, hasParametrizedmethods=false"
	 * because it is no use to test empty class 16 times.
	 * Of course I can exclude all cases like that and then add one manually, sure.
	 * Also if class is not already contained - in should be implemented after import, so check status.
	 */
	public void addImplementedTest(boolean isAlreadyContained, boolean hasAnnotatedMethods, boolean hasParameterlessMethods, boolean hasParametrizedMethods, boolean hasExpectedParameters, boolean hasEnumTypes, boolean hasOtherUserTypes){
		// TODO Auto-generated method stub
		System.out.println("addImplementedTest(" + isAlreadyContained + ", " + hasAnnotatedMethods + ", " + hasParameterlessMethods + ", " + hasParametrizedMethods + ", " + hasExpectedParameters + ", " + hasEnumTypes + ", " + hasOtherUserTypes + ")");
	}

	/*
	 * this method will create class with one method of given parameter type and expected status;
	 * then class will me imported into model and it should be implemented in model.
	 * This test is justified by actual bug with initializing default partitions. Should be handy in the future.
	 */
	public void addImplementedTypewiseTest(ETypeName type, boolean isExpected){
		// TODO Auto-generated method stub
		System.out.println("addImplementedTypewiseTest(" + isExpected + ")");
	}

	public void addNewClassTest(boolean duplicate){
		// TODO Auto-generated method stub
		System.out.println("addNewClassTest(" + duplicate + ")");
	}

	/*
	 * We use only few constants of ESelectionType here, since all classes are of same type and parent;
	 */
	public void removeSelectedClassesTest(ESelectionType selectionType){
		// TODO Auto-generated method stub
		System.out.println("removeSelectedClassesTest(" + selectionType + ")");
	}

}

