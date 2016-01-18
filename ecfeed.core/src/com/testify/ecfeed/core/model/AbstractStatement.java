/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.core.model;

import java.util.List;

public abstract class AbstractStatement implements IStatement {
	AbstractStatement fParent = null;
	private static int fLastId = 0;
	private final int fId;
	
	public AbstractStatement(){
		fId = fLastId++;
	}
	
	public int getId(){
		return fId;
	}

	public abstract String getLeftOperandName();
	
	public AbstractStatement getParent() {
		return fParent;
	}

	public void setParent(AbstractStatement parent) {
		fParent = parent;
	}

	public List<AbstractStatement> getChildren(){
		return null;
	}

	public void replaceChild(AbstractStatement oldStatement, 
			AbstractStatement newStatement) {
		List<AbstractStatement> children = getChildren();
		if(children != null){
			int index = children.indexOf(oldStatement);
			if(index != -1){
				newStatement.setParent(this);
				children.set(index, newStatement);
			}
		}
	}

//	public void removeChild(BasicStatement child) {
//		List<BasicStatement> children = getChildren();
//		if(children != null){
//			children.remove(child);
//		}
//	}
	
	public void addStatement(AbstractStatement statement){
//		if(getParent() != null){
//			getParent().addStatement(statement);
//		}
	}

	public boolean mentions(ChoiceNode choice) {
		return false;
	}

	public boolean mentions(MethodParameterNode parameter) {
		return false;
	}

	public boolean mentions(MethodParameterNode parameter, String label) {
		return false;
	}

	@Override
	public boolean evaluate(List<ChoiceNode> values) {
		return false;
	}

	@Override
	public boolean equals(Object obj){
		if(obj instanceof AbstractStatement == false){
			return false;
		}
		return fId == ((AbstractStatement)obj).getId();
	}
	
	@Override
	public boolean adapt(List<ChoiceNode> values){
		return false;
	}
	
	public abstract AbstractStatement getCopy();
	
	public abstract boolean updateReferences(MethodNode method);
}
