package com.testify.ecfeed.testutils;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;

public enum ENodeType {
	PROJECT, CLASS, METHOD, PARAMETER, CONSTRAINT, TEST_CASE, CHOICE;
	
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
		public Object visit(CategoryNode node) throws Exception {
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
		public Object visit(PartitionNode node) throws Exception {
			return null;
		}
	}
}
