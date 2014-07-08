package com.testify.ecfeed.model.constraint;

import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement.LabelCondition;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement.PartitionCondition;

public interface IStatementVisitor {
	public Object visit(StaticStatement statement) throws Exception;
	public Object visit(StatementArray statement) throws Exception;
	public Object visit(ExpectedValueStatement statement) throws Exception;
	public Object visit(PartitionedCategoryStatement statement) throws Exception;
	public Object visit(LabelCondition condition) throws Exception;
	public Object visit(PartitionCondition condition) throws Exception;
}
