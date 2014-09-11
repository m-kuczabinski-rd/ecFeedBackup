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
		@Override
		public Object visit(StaticStatement statement) throws Exception {
			StaticStatementInterface statementIf = new StaticStatementInterface();
			statementIf.setTarget(statement);
			return statementIf;
		}

		@Override
		public Object visit(StatementArray statement) throws Exception {
			StatementArrayInterface statementIf = new StatementArrayInterface();
			statementIf.setTarget(statement);
			return statementIf;
		}

		@Override
		public Object visit(ExpectedValueStatement statement) throws Exception {
			ExpectedValueStatementInterface statementIf = new ExpectedValueStatementInterface();
			statementIf.setTarget(statement);
			return statementIf;
		}

		@Override
		public Object visit(PartitionedCategoryStatement statement)
				throws Exception {
			PartitionedCategoryStatementInterface statementIf = new PartitionedCategoryStatementInterface();
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

	public static BasicStatementInterface getInterface(BasicStatement statement){
		try {
			return (BasicStatementInterface) statement.accept(new InterfaceProvider());
		} catch (Exception e) {
			return new BasicStatementInterface();
		}
	}
}
