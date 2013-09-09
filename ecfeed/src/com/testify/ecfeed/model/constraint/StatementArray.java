package com.testify.ecfeed.model.constraint;

import java.util.Vector;

import com.testify.ecfeed.model.PartitionNode;

public class StatementArray extends BasicStatement{
	private Operator fOperator;
	private Vector<BasicStatement> fStatements;
	
	public StatementArray(Operator operator){
		fStatements = new Vector<BasicStatement>();
		fOperator = operator;
	}

	public void addStatement(BasicStatement statement){
		fStatements.add(statement);
		statement.setParent(this);
	}
	
	public Operator getOperator(){
		return fOperator;
	}
	
	@Override
	public Vector<BasicStatement> getChildren(){
		return fStatements;
	}
	
	@Override
	public boolean evaluate(Vector<PartitionNode> values) {
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
			result += fStatements.elementAt(i).toString();
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
	public boolean equals(Object obj){
		if(obj == null){
			return false;
		}

		if(obj instanceof StatementArray == false){
			return false;
		}
		StatementArray array = (StatementArray)obj;
		if(fOperator != array.getOperator()){
			return false;
		}
		if(getChildren().equals(array.getChildren()) == false){
			return false;
		}
		return super.equals(obj);
	}

	public void setOperator(Operator operator) {
		fOperator = operator;
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
}