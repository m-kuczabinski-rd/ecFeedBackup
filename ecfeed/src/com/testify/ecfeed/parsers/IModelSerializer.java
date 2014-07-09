package com.testify.ecfeed.parsers;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;

public interface IModelSerializer {
	public Object serialize(RootNode node) throws Exception;
	public Object serialize(ClassNode node) throws Exception;
	public Object serialize(MethodNode node) throws Exception;
	public Object serialize(CategoryNode node) throws Exception;
	public Object serialize(TestCaseNode node) throws Exception;
	public Object serialize(ConstraintNode node) throws Exception;
	public Object serialize(PartitionNode node) throws Exception;
}
