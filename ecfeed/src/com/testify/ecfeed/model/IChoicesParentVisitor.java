package com.testify.ecfeed.model;

public interface IChoicesParentVisitor extends IParameterVisitor{
	public Object visit(ChoiceNode node)throws Exception;
}
