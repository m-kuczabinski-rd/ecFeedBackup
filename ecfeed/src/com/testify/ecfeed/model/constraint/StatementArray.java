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

import java.util.ArrayList;
import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;

public class StatementArray extends BasicStatement{
	private Operator fOperator;
	private List<BasicStatement> fStatements;
	
	public StatementArray(Operator operator){
		fStatements = new ArrayList<BasicStatement>();
		fOperator = operator;
	}

	public Operator getOperator(){
		return fOperator;
	}

	public void setOperator(Operator operator) {
		fOperator = operator;
	}

	public void addStatement(BasicStatement statement){
		fStatements.add(statement);
		statement.setParent(this);
	}
	
	@Override
	public List<BasicStatement> getChildren(){
		return fStatements;
	}
	
	@Override
	public boolean mentions(PartitionNode partition){
		for(BasicStatement child : fStatements){
			if(child.mentions(partition)){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mentions(CategoryNode category){
		for(BasicStatement child : fStatements){
			if(child.mentions(category)){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean evaluate(List<PartitionNode> values) {
		if(fStatements.size() == 0){
			return false;
		}
		switch (fOperator){
			case AND:
				for(IStatement statement : fStatements){
					if(statement.evaluate(values) == false){
						return false;
					}
				}
				return true;
			case OR:
				for(IStatement statement : fStatements){
					if(statement.evaluate(values) == true){
						return true;
					}
				}
				return false;
		}
		return false;
	}
	
	@Override
	public String toString(){
		String result = new String("(");
		for(int i = 0; i < fStatements.size(); i++){
			result += fStatements.get(i).toString();
			if(i < fStatements.size() - 1){
				switch(fOperator){
				case AND:
					result += " \u2227 ";
					break;
				case OR:
					result += " \u2228 ";
					break;
				}
			}
		}
		return result + ")";
	}
}