package com.testify.ecfeed.model.constraint;

import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement.LabelCondition;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement.PartitionCondition;

public interface IStatementVisitor {
	public Object visit(StaticStatement statement);
	public Object visit(StatementArray statement);
	public Object visit(ExpectedValueStatement statement);
	public Object visit(PartitionedCategoryStatement statement);
	public Object visit(LabelCondition condition);
	public Object visit(PartitionCondition condition);
}
