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

