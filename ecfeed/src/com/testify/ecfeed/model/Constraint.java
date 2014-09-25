/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.testify.ecfeed.generators.api.IConstraint;
import com.testify.ecfeed.model.PartitionedCategoryStatement.LabelCondition;
import com.testify.ecfeed.model.PartitionedCategoryStatement.PartitionCondition;

public class Constraint implements IConstraint<PartitionNode> {
	
	private final int ID;
	private static int fLastId = 0;

	private BasicStatement fPremise;
	private BasicStatement fConsequence; 

	//TODO resign from the visitor pattern for better readability of the code
	private class ReferencedPartitionsProvider implements IStatementVisitor{

		@Override
		public Object visit(StaticStatement statement) throws Exception {
			return new HashSet<PartitionNode>();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object visit(StatementArray statement) throws Exception {
			Set<PartitionNode> set = new HashSet<PartitionNode>();
			for(BasicStatement s : statement.getStatements()){
				set.addAll((Set<PartitionNode>)s.accept(this));
			}
			return set;
		}

		@Override
		public Object visit(ExpectedValueStatement statement) throws Exception {
			return new HashSet<PartitionNode>();
		}

		@Override
		public Object visit(PartitionedCategoryStatement statement)
				throws Exception {
			return statement.getCondition().accept(this);
		}

		@Override
		public Object visit(LabelCondition condition) throws Exception {
			return new HashSet<PartitionNode>();
		}

		@Override
		public Object visit(PartitionCondition condition) throws Exception {
			Set<PartitionNode> set = new HashSet<PartitionNode>();
			set.add(condition.getPartition());
			return set;
		}
		
	}
	
	private class ReferencedCategoriesProvider implements IStatementVisitor{

		@Override
		public Object visit(StaticStatement statement) throws Exception {
			return new HashSet<CategoryNode>();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object visit(StatementArray statement) throws Exception {
			Set<CategoryNode> set = new HashSet<CategoryNode>();
			for(BasicStatement s : statement.getStatements()){
				set.addAll((Set<CategoryNode>)s.accept(this));
			}
			return set;
		}

		@Override
		public Object visit(ExpectedValueStatement statement) throws Exception {
			Set<CategoryNode> set = new HashSet<CategoryNode>();
			set.add(statement.getCategory());
			return set;
		}

		@Override
		public Object visit(PartitionedCategoryStatement statement)
				throws Exception {
			return statement.getCondition().accept(this);
		}

		@Override
		public Object visit(LabelCondition condition) throws Exception {
			return new HashSet<CategoryNode>();
		}

		@Override
		public Object visit(PartitionCondition condition) throws Exception {
			Set<CategoryNode> set = new HashSet<CategoryNode>();
			CategoryNode category = condition.getPartition().getCategory();
			if(category != null){
				set.add(category);
			}
			return set;
		}
	}
	
	private class ReferencedLabelsPrivider implements IStatementVisitor{
		
		private CategoryNode fCategory;
		private Set<String> EMPTY_SET = new HashSet<String>();
		
		public ReferencedLabelsPrivider(CategoryNode category){
			fCategory = category;
		}
		
		@Override
		public Object visit(StaticStatement statement) throws Exception {
			return EMPTY_SET;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object visit(StatementArray statement) throws Exception {
			Set<String> set = new HashSet<String>();
			for(BasicStatement s : statement.getStatements()){
				set.addAll((Set<String>)s.accept(this));
			}
			return set;
		}

		@Override
		public Object visit(ExpectedValueStatement statement) throws Exception {
			return EMPTY_SET;
		}

		@Override
		public Object visit(PartitionedCategoryStatement statement)
				throws Exception {
			if(fCategory == statement.getCategory()){
				return statement.getCondition().accept(this);
			}
			return EMPTY_SET;
		}

		@Override
		public Object visit(LabelCondition condition) throws Exception {
			Set<String> result = new HashSet<String>();
			result.add(condition.getLabel());
			return result;
		}

		@Override
		public Object visit(PartitionCondition condition) throws Exception {
			return EMPTY_SET;
		}
		
	}
	
	public Constraint(BasicStatement premise, BasicStatement consequence){
		ID = fLastId++;
		fPremise = premise;
		fConsequence = consequence;
	}
	
	@Override
	public boolean evaluate(List<PartitionNode> values) {
		if(fPremise == null) return true;
		if(fPremise.evaluate(values) == true){
			if(fConsequence == null) return false;
			return fConsequence.evaluate(values);
		}
		return true;
	}

	@Override
	public boolean adapt(List<PartitionNode> values){
		if(fPremise == null) return true;
		if(fPremise.evaluate(values) == true){
			return fConsequence.adapt(values);
		}		
		return true;
	}

	@Override
	public String toString(){
		String premiseString = (fPremise != null)?fPremise.toString():"EMPTY";
		String consequenceString = (fConsequence != null)?fConsequence.toString():"EMPTY";
		return premiseString + " \u21d2 " + consequenceString;
	}

	@Override
	public boolean equals(Object obj){
		if(obj instanceof Constraint == false){
			return false;
		}
		return(ID == ((Constraint)obj).getId());
	}

	public int getId(){
		return ID;
	}
	
	public BasicStatement getPremise(){
		return fPremise;
	}
	
	public BasicStatement getConsequence(){
		return fConsequence;
	}

	public void setPremise(BasicStatement statement){
		fPremise = statement;
	}
	
	public void setConsequence(BasicStatement consequence){
		fConsequence = consequence;
	}
	
	public boolean mentions(CategoryNode category) {
		return fPremise.mentions(category) || fConsequence.mentions(category);
	}

	public boolean mentions(CategoryNode category, String label) {
		return fPremise.mentions(category, label) || fConsequence.mentions(category, label);
	}

	public boolean mentions(PartitionNode partition) {
		return fPremise.mentions(partition) || fConsequence.mentions(partition);
	}
	
	public Constraint getCopy(){
		BasicStatement premise = fPremise.getCopy();
		BasicStatement consequence = fConsequence.getCopy();
		return new Constraint(premise, consequence);
	}

	public boolean updateRefrences(MethodNode method){
		if(fPremise.updateReferences(method) && fConsequence.updateReferences(method))
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public Set<PartitionNode> getReferencedPartitions() {
		try{
			Set<PartitionNode> referenced = (Set<PartitionNode>)fPremise.accept(new ReferencedPartitionsProvider());
			referenced.addAll((Set<PartitionNode>)fConsequence.accept(new ReferencedPartitionsProvider()));
			return referenced;
		}
		catch(Exception e){
			return new HashSet<PartitionNode>();
		}
	}

	@SuppressWarnings("unchecked")
	public Set<CategoryNode> getReferencedCategories() {
		try{
			Set<CategoryNode> referenced = (Set<CategoryNode>)fPremise.accept(new ReferencedCategoriesProvider());
			referenced.addAll((Set<CategoryNode>)fConsequence.accept(new ReferencedCategoriesProvider()));
			return referenced;
		}
		catch(Exception e){
			return new HashSet<CategoryNode>();
		}
	}

	@SuppressWarnings("unchecked")
	public Set<String> getReferencedLabels(CategoryNode category) {
		try{
			Set<String> referenced = (Set<String>)fPremise.accept(new ReferencedLabelsPrivider(category));
			referenced.addAll((Set<String>)fConsequence.accept(new ReferencedLabelsPrivider(category)));
			return referenced;
		}
		catch(Exception e){
			return new HashSet<String>();
		}
	}
}
