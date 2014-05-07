package com.testify.ecfeed.model.constraint;


public interface IRelationalStatement {
	public Relation getRelation();
	public void setRelation(Relation relation);
	public Relation[] getAvailableRelations();
}
