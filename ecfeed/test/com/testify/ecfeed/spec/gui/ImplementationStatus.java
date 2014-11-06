package com.testify.ecfeed.spec.gui;

public class ImplementationStatus{

	public void implementedPartitionTest(boolean primitive, boolean implementedValue, EChildrenStatus childrenStatus){
		// TODO Auto-generated method stub
		System.out.println("implementedPartitionTest(" + primitive + ", " + implementedValue + ", " + childrenStatus + ")");
	}

	public void implementedCategoryTest(boolean primitive, boolean implementedValue, EChildrenStatus childrenStatus){
		// TODO Auto-generated method stub
		System.out.println("implementedCategoryTest(" + primitive + ", " + implementedValue + ", " + childrenStatus + ")");
	}

	public void implementedMethodTest(EChildrenStatus childrenStatus, boolean hasImplementation){
		// TODO Auto-generated method stub
		System.out.println("implementedMethodTest(" + childrenStatus + ", " + hasImplementation + ")");
	}

	public void implementedClassTest(EChildrenStatus childrenStatus, boolean hasImplementation){
		// TODO Auto-generated method stub
		System.out.println("implementedClassTest(" + childrenStatus + ", " + hasImplementation + ")");
	}

	public void implementedTestCaseTest(EChildrenStatus childrenStatus){
		// TODO Auto-generated method stub
		System.out.println("implementedTestCaseTest(" + childrenStatus + ")");
	}


}

