package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.model.BasicStatement;
import com.testify.ecfeed.model.ExpectedValueStatement;
import com.testify.ecfeed.model.IStatementVisitor;
import com.testify.ecfeed.model.PartitionedParameterStatement;
import com.testify.ecfeed.model.StatementArray;
import com.testify.ecfeed.model.StaticStatement;
import com.testify.ecfeed.model.PartitionedParameterStatement.LabelCondition;
import com.testify.ecfeed.model.PartitionedParameterStatement.PartitionCondition;

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
		public Object visit(PartitionedParameterStatement statement)
				throws Exception {
			PartitionedParameterStatementInterface statementIf = new PartitionedParameterStatementInterface(fUpdateContext);
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
