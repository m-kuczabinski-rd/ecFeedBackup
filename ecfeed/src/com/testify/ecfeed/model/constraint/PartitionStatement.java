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

public class PartitionStatement extends CategoryConditionStatement{

	public PartitionStatement(CategoryNode category, Relation relation, PartitionNode condition){
		super(category, relation, condition);
	}
	
	public void setCondition(PartitionNode condition){
		super.setCondition(condition);
	}
	
	public PartitionNode getPartitionCondition(){
		return (PartitionNode)getCondition();
	}
	
	@Override
	public boolean mentions(PartitionNode partition){
		return getCondition() == partition;
	}

	@Override
	public String getConditionName(){
		return getPartitionCondition().getQualifiedName();
	}

	@Override
	public boolean evaluate(List<PartitionNode> values) {
		CategoryNode parentCategory = getPartitionCondition().getCategory();
		MethodNode methodAncestor = parentCategory.getMethod();
		int categoryIndex = methodAncestor.getCategories().indexOf(parentCategory);

		if(values.size() < categoryIndex + 1){
			return false;
		}
		
		IGenericNode node = values.get(categoryIndex);
		boolean isCondition = false;

		while(node instanceof PartitionNode){
			PartitionNode partition = (PartitionNode)node;
			if(partition == getPartitionCondition()){
				isCondition = true;
				break;
			}
			node = node.getParent();
		}

		switch (getRelation()){
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
		return getCategory().getName() + getRelation() + getPartitionCondition().getQualifiedName();
	}
}