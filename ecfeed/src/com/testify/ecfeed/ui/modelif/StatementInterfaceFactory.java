/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.model.AbstractStatement;
import com.testify.ecfeed.model.ExpectedValueStatement;
import com.testify.ecfeed.model.IStatementVisitor;
import com.testify.ecfeed.model.ChoicesParentStatement;
import com.testify.ecfeed.model.StatementArray;
import com.testify.ecfeed.model.StaticStatement;
import com.testify.ecfeed.model.ChoicesParentStatement.LabelCondition;
import com.testify.ecfeed.model.ChoicesParentStatement.ChoiceCondition;

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
		public Object visit(ChoicesParentStatement statement)
				throws Exception {
			ChoicesParentStatementInterface statementIf = new ChoicesParentStatementInterface(fUpdateContext);
			statementIf.setTarget(statement);
			return statementIf;
		}

		@Override
		public Object visit(LabelCondition condition) throws Exception {
			return null;
		}

		@Override
		public Object visit(ChoiceCondition condition) throws Exception {
			return null;
		}
	}

	public static AbstractStatementInterface getInterface(AbstractStatement statement, IModelUpdateContext updateContext){
		try {
			return (AbstractStatementInterface) statement.accept(new InterfaceProvider(updateContext));
		} catch (Exception e) {
			return new AbstractStatementInterface(updateContext);
		}
	}
}
