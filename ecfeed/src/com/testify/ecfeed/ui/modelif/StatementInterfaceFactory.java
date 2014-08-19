package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.ExpectedValueStatement;
import com.testify.ecfeed.model.constraint.IStatementVisitor;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement.LabelCondition;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement.PartitionCondition;
import com.testify.ecfeed.model.constraint.StatementArray;
import com.testify.ecfeed.model.constraint.StaticStatement;
import com.testify.ecfeed.modelif.ModelOperationManager;

public class StatementInterfaceFactory{

	ModelOperationManager fOperationManager;
	
	public StatementInterfaceFactory(ModelOperationManager operationManager){
		fOperationManager = operationManager;
	}
	
	private class InterfaceProvider implements IStatementVisitor{
		@Override
		public Object visit(StaticStatement statement) throws Exception {
			StaticStatementInterface statementIf = new StaticStatementInterface(fOperationManager);
			statementIf.setTarget(statement);
			return statementIf;
		}

		@Override
		public Object visit(StatementArray statement) throws Exception {
			StatementArrayInterface statementIf = new StatementArrayInterface(fOperationManager);
			statementIf.setTarget(statement);
			return statementIf;
		}

		@Override
		public Object visit(ExpectedValueStatement statement) throws Exception {
			ExpectedValueStatementInterface statementIf = new ExpectedValueStatementInterface(fOperationManager);
			statementIf.setTarget(statement);
			return statementIf;
		}

		@Override
		public Object visit(PartitionedCategoryStatement statement)
				throws Exception {
			PartitionedCategoryStatementInterface statementIf = new PartitionedCategoryStatementInterface(fOperationManager);
			statementIf.setTarget(statement);
			return statementIf;
		}

		@Override
		public Object visit(LabelCondition condition) throws Exception {
			return null;
		}

		@Override
		public Object visit(PartitionCondition condition) throws Exception {
			return null;
		}
	}

	public BasicStatementInterface getInterface(BasicStatement statement){
		try {
			return (BasicStatementInterface) statement.accept(new InterfaceProvider());
		} catch (Exception e) {
			return new BasicStatementInterface(fOperationManager);
		}
	}
}
