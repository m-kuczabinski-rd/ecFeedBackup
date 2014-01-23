/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.model.constraint;

import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.IGenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;

public class Statement extends BasicStatement{
	private PartitionNode fCondition = null;
	private Relation fRelation;

	public Statement(PartitionNode condition, Relation relation){
		fCondition = condition;
		fRelation = relation;
	}
	
	public PartitionNode getCondition(){
		return fCondition;
	}
	
	public void setCondition(PartitionNode condition) {
		fCondition = condition;
	}

	public Relation getRelation(){
		return fRelation;
	}

	public void setRelation(Relation relation) {
		fRelation = relation;
	}

	@Override
	public boolean mentions(PartitionNode partition){
		return fCondition == partition;
	}

	@Override
	public boolean mentions(CategoryNode category){
		return fCondition.getCategory() == category;
	}

	@Override
	public boolean evaluate(List<PartitionNode> values) {
		CategoryNode parentCategory = fCondition.getCategory();
		MethodNode methodAncestor = parentCategory.getMethod();
		int categoryIndex = methodAncestor.getCategories().indexOf(parentCategory);

		if(values.size() < categoryIndex + 1){
			return false;
		}
		
		IGenericNode node = values.get(categoryIndex);
		boolean isCondition = false;

		while(node instanceof PartitionNode){
			PartitionNode partition = (PartitionNode)node;
			if(partition == fCondition){
				isCondition = true;
				break;
			}
			node = node.getParent();
		}

		switch (fRelation){
		case EQUAL:
			return isCondition;
		case NOT:
			return !isCondition;
		default:
			return false;
		}
	}
	
	@Override
	public String toString(){
		return fCondition.getParent().getName() + " " + fRelation + " " + fCondition.getName();
	}
}