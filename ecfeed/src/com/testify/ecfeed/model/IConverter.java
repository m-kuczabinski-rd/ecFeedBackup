package com.testify.ecfeed.model;

public interface IConverter {
	public Object convert(RootNode node);
	public Object convert(ClassNode node);
	public Object convert(MethodNode node);
	public Object convert(CategoryNode node);
	public Object convert(PartitionNode node);
}
