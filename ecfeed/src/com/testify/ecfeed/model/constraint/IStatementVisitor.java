package com.testify.ecfeed.model.constraint;

public interface IStatementVisitor {
	public Object visit(StaticStatement statement);
	public Object visit(StatementArray statement);
	public Object visit(ExpectedValueStatement statement);
	public Object visit(PartitionedCategoryStatement statement);
}
