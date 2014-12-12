package com.testify.ecfeed.model;

public interface IParameterVisitor {
	public Object visit(MethodParameterNode node) throws Exception;
	public Object visit(GlobalParameterNode node) throws Exception;
}
