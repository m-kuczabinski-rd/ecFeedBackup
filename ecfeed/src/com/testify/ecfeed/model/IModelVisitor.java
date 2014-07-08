package com.testify.ecfeed.model;

public interface IModelVisitor {
	public Object visit(RootNode node);
	public Object visit(ClassNode node);
	public Object visit(MethodNode node);
	public Object visit(CategoryNode node);
	public Object visit(TestCaseNode node);
	public Object visit(ConstraintNode node);
	public Object visit(PartitionNode node);
}
