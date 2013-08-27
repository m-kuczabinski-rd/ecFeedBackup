package com.testify.ecfeed.model;

import java.util.Vector;

import com.testify.ecfeed.api.IConstraint;

public class Constraint implements IConstraint {
	public interface IStatement{
		public boolean evaluate(Vector<PartitionNode> values);
	}
	
	public enum Operator{
		AND, OR
	};
	
	public class StatementArray implements IStatement{
		private Operator fOperator;
		private Vector<IStatement> fStatements;
		
		@Override
		public boolean evaluate(Vector<PartitionNode> values) {
			switch (fOperator){
				case AND:
					for(IStatement statement : fStatements){
						if(statement.evaluate(values) == false){
							return false;
						}
						return true;
					}
				case OR:
					for(IStatement statement : fStatements){
						if(statement.evaluate(values) == true){
							return true;
						}
						return false;
					}
			}
			return false;
		}
	}
	
	public class Statement implements IStatement{
		private PartitionNode fCondition = null;
		private boolean fStaticValue = false;
		
		public Statement(PartitionNode condition){
			fCondition = condition;
		}
		public Statement(boolean staticValue){
			fStaticValue = staticValue;
		}

		@Override
		public boolean evaluate(Vector<PartitionNode> values) {
			if(fCondition == null){
				return fStaticValue;
			}
			for(int i = 0; i < values.size(); i++){
				if(values.elementAt(i) == fCondition){
					//if the values vector contains required partition, check if it is on right element
					CategoryNode parentCategory = (CategoryNode)fCondition.getParent();
					MethodNode parentMethod = (MethodNode)parentCategory.getParent();
					if(i == parentMethod.getCategories().indexOf(parentCategory)){
						return true;
					}
				}
			}
			return false;
		}
		
	};
	
	private Statement fPremise;
	private Statement fConsequence; 
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean evaluate(Vector values) {
		for(Object value : values){
			if(value instanceof PartitionNode == false){
				return false;
			}
		}
		if(fPremise.evaluate(values) == true){
			return fConsequence.evaluate(values);
		}
		return true;
	}

}
