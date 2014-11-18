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

package com.testify.ecfeed.model;

import java.util.ArrayList;
import java.util.List;

public class StatementArray extends BasicStatement{
	private EStatementOperator fOperator;
	private List<BasicStatement> fStatements;
	
	public StatementArray(EStatementOperator operator){
		fStatements = new ArrayList<BasicStatement>();
		fOperator = operator;
	}

	public String getLeftOperandName(){
		return fOperator.toString();
	}

	public EStatementOperator getOperator(){
		return fOperator;
	}

	public void setOperator(EStatementOperator operator) {
		fOperator = operator;
	}

	public void addStatement(BasicStatement statement, int index){
		fStatements.add(index, statement);
		statement.setParent(this);
	}
	
	public void addStatement(BasicStatement statement){
		addStatement(statement, fStatements.size());
	}
	
	@Override
	public List<BasicStatement> getChildren(){
		return fStatements;
	}
	
	public boolean removeChild(BasicStatement child){
		return getChildren().remove(child);
	}
	
	@Override
	public boolean mentions(ChoiceNode partition){
		for(BasicStatement child : fStatements){
			if(child.mentions(partition)){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mentions(ParameterNode parameter){
		for(BasicStatement child : fStatements){
			if(child.mentions(parameter)){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean evaluate(List<ChoiceNode> values) {
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
	
	@Override
	public StatementArray getCopy(){
		StatementArray copy = new StatementArray(fOperator);
		for(BasicStatement statement: fStatements){
			copy.addStatement(statement.getCopy());
		}
		return copy;
	}
	
	@Override
	public boolean updateReferences(MethodNode method){
		for(BasicStatement statement: fStatements){
			if(!statement.updateReferences(method)) return false;
		}
		return true;
	}
	
	List<BasicStatement> getStatements(){
		return fStatements;
	}
	
	@Override 
	public boolean compare(IStatement statement){
		if(statement instanceof StatementArray == false){
			return false;
		}
		StatementArray compared = (StatementArray)statement;

		if(getOperator() != compared.getOperator()){
			return false;
		}
		
		if(getStatements().size() != compared.getStatements().size()){
			return false;
		}
		
		for(int i = 0; i < getStatements().size(); i++){
			if(getStatements().get(i).compare(compared.getStatements().get(i)) == false){
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public Object accept(IStatementVisitor visitor) throws Exception {
		return visitor.visit(this);
	}
}
