/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.modelif;

import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.ChoicesParentStatement;
import com.ecfeed.core.model.ExpectedValueStatement;
import com.ecfeed.core.model.IStatementVisitor;
import com.ecfeed.core.model.StatementArray;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.model.ChoicesParentStatement.ChoiceCondition;
import com.ecfeed.core.model.ChoicesParentStatement.LabelCondition;

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
