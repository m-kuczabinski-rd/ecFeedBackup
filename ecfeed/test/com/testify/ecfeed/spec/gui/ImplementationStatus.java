package com.testify.ecfeed.spec.gui;

public class ImplementationStatus{

	public void implementedPartitionTest(boolean primitive, boolean implementedValue, EChildrenImplementationStatus childrenStatus){
		// TODO Auto-generated method stub
		System.out.println("implementedPartitionTest(" + primitive + ", " + implementedValue + ", " + childrenStatus + ")");
	}

	public void implementedCategoryTest(boolean primitive, boolean implementedValue, EChildrenImplementationStatus childrenStatus){
		// TODO Auto-generated method stub
		System.out.println("implementedCategoryTest(" + primitive + ", " + implementedValue + ", " + childrenStatus + ")");
	}

	public void implementedMethodTest(EChildrenImplementationStatus childrenStatus, boolean hasImplementation){
		// TODO Auto-generated method stub
		System.out.println("implementedMethodTest(" + childrenStatus + ", " + hasImplementation + ")");
	}

	public void implementedClassTest(EChildrenImplementationStatus childrenStatus, boolean hasImplementation){
		// TODO Auto-generated method stub
		System.out.println("implementedClassTest(" + childrenStatus + ", " + hasImplementation + ")");
	}

	public void implementedTestCaseTest(EChildrenImplementationStatus childrenStatus){
		// TODO Auto-generated method stub
		System.out.println("implementedTestCaseTest(" + childrenStatus + ")");
	}

	public void implementedRootTest(EChildrenImplementationStatus childrenStatus){
		// TODO Auto-generated method stub
		System.out.println("implementedRootTest(" + childrenStatus + ")");
	}


}

