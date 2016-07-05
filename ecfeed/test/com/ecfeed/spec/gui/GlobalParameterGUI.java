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

public class GlobalParameterGUI{
	
	public enum ParameterScope{
		METHOD, CLASS, GLOBAL
	}

	/*
	 * @param isClassParameter : if true - it is class parameter, if not - it is global parameter
	 * @param siblingDuplicate : if true - it has duplicate sibling, if not - well, it doesn't
	 * @param inMethodDuplicate : if true - method with linked parameter has other parameter of same name. It shouldn't be affected and that is what we are testing.
	 * We don't really need cartesian of it, but whatever, noone will get hurt from that.
	 */
	public void globalParameterNameDuplicateTest(boolean isClassParameter, boolean siblingDuplicate, boolean inMethodDuplicate){
		// TODO Auto-generated method stub
		System.out.println("globalParameterNameDuplicateTest(" + isClassParameter + ", " + siblingDuplicate + ", " + inMethodDuplicate + ")");
	}
	
	/*
	 * Fixed test - types of arguments are not relevant. Steps to reproduce duplicate:
	 * Create global or class parameter, depending on isClassParameter value
	 * Create two methods within same class. Methods must have same name. Methods must have have at least 2 parameters.
	 * Make one parameter linked to previously created global parameter. They cannot be on the same position.
	 * Corresponding mirror parameters must be of same type, and mirror parameters for both linked parameters must have same type, for example:
	 * String int linked
	 * String linked int
	 * Now, when global parameter is set for corresponding type (int in the example above), it should stop the operation and display proper message dialog.
	 * Parameter names should be different in both methods to make sure it doesn't affect comparing.
	 */
	public void changeGlobalParameterTypeDuplicateTest(boolean isClassParameter){
		// TODO Auto-generated method stub
		System.out.println("changeGlobalParameterTypeDuplicateTest(" + isClassParameter + ")");
	}

	public void changeGlobalParameterNameTest(String newName, boolean isDuplicate, boolean validation){
		// TODO Auto-generated method stub
		System.out.println("changeGlobalParameterNameTest(" + newName + ", " + isDuplicate + ", " + validation + ")");
	}

	public void globalParameterDragNDrop(boolean isCut, ParameterScope source, ParameterScope target){
		// TODO Auto-generated method stub
		System.out.println("globalParameterDragNDrop(" + isCut + ", " + source + ", " + target + ")");
	}




}

