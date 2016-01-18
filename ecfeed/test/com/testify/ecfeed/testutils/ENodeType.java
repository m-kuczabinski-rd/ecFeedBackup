package com.testify.ecfeed.testutils;

import com.testify.ecfeed.core.model.ChoiceNode;
import com.testify.ecfeed.core.model.ClassNode;
import com.testify.ecfeed.core.model.ConstraintNode;
import com.testify.ecfeed.core.model.GlobalParameterNode;
import com.testify.ecfeed.core.model.IModelVisitor;
import com.testify.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.core.model.MethodParameterNode;
import com.testify.ecfeed.core.model.RootNode;
import com.testify.ecfeed.core.model.TestCaseNode;

public enum ENodeType {
	PROJECT, CLASS, METHOD, PARAMETER, METHOD_PARAMETER, GLOBAL_PARAMETER, CONSTRAINT, TEST_CASE, CHOICE;

	@SuppressWarnings("unused")
	private class DummyClassJustToRememberToUpdateTheEnumWhenNewNodeTypeIsAddedToModel implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return null;
		}
	}
}
