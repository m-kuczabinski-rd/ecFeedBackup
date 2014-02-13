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

package com.testify.ecfeed.model.constraint;

import com.testify.ecfeed.model.CategoryNode;

public class CategoryConditionStatement extends BasicStatement {

	private CategoryNode fCategory;
	private Relation fRelation;
	private Object fCondition;
	
	CategoryConditionStatement(CategoryNode category, Relation relation, Object condition){
		fCategory = category;
		fRelation = relation;
		fCondition = condition;
	}
	
	public void setCategory(CategoryNode category){
		fCategory= category;
	}
	
	public CategoryNode getCategory(){
		return fCategory;
	}
	
	public void setRelation(Relation relation){
		fRelation = relation;
	}
	
	public Relation getRelation(){
		return fRelation;
	}
	
	protected void setCondition(Object condition){
		fCondition = condition;
	}
	
	public Object getCondition(){
		return fCondition;
	}

	@Override
	public boolean mentions(CategoryNode category){
		return getCategory() == category;
	}

	public String getConditionName() {
		return null;
	}


}

