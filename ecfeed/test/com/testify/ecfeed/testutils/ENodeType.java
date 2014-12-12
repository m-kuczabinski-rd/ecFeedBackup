package com.testify.ecfeed.testutils;

import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;

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
