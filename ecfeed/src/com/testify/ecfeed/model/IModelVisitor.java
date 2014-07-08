package com.testify.ecfeed.model;

public interface IModelVisitor {
	public Object visit(RootNode node) throws Exception;
	public Object visit(ClassNode node) throws Exception;
	public Object visit(MethodNode node) throws Exception;
	public Object visit(CategoryNode node) throws Exception;
	public Object visit(TestCaseNode node) throws Exception;
	public Object visit(ConstraintNode node) throws Exception;
	public Object visit(PartitionNode node) throws Exception;
}
