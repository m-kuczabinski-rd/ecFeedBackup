package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.ExpectedValueStatement;
import com.testify.ecfeed.model.constraint.IStatementVisitor;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement.LabelCondition;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement.PartitionCondition;
import com.testify.ecfeed.model.constraint.StatementArray;
import com.testify.ecfeed.model.constraint.StaticStatement;

public class StatementInterfaceFactory{

	private static class InterfaceProvider implements IStatementVisitor{
		
		private IModelUpdateContext fUpdateContext;

		public InterfaceProvider(IModelUpdateContext updateContext) {
			fUpdateContext = updateContext;
		}

		@Override
		public Object visit(StaticStatement statement) throws Exception {
			StaticStatementInterface statementIf = new StaticStatementInterface(fUpdateContext);
			statementIf.setTarget(statement);
			return statementIf;
		}

		@Override
		public Object visit(StatementArray statement) throws Exception {
			StatementArrayInterface statementIf = new StatementArrayInterface(fUpdateContext);
			statementIf.setTarget(statement);
			return statementIf;
		}

		@Override
		public Object visit(ExpectedValueStatement statement) throws Exception {
			ExpectedValueStatementInterface statementIf = new ExpectedValueStatementInterface(fUpdateContext);
			statementIf.setTarget(statement);
			return statementIf;
		}

		@Override
		public Object visit(PartitionedCategoryStatement statement)
				throws Exception {
			PartitionedCategoryStatementInterface statementIf = new PartitionedCategoryStatementInterface(fUpdateContext);
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

	public static BasicStatementInterface getInterface(BasicStatement statement, IModelUpdateContext updateContext){
		try {
			return (BasicStatementInterface) statement.accept(new InterfaceProvider(updateContext));
		} catch (Exception e) {
			return new BasicStatementInterface(updateContext);
		}
	}
}
