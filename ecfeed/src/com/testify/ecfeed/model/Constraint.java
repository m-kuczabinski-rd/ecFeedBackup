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
import com.testify.ecfeed.model.PartitionedParameterStatement.LabelCondition;
import com.testify.ecfeed.model.PartitionedParameterStatement.PartitionCondition;

public class Constraint implements IConstraint<ChoiceNode> {
	
	private final int ID;
	private static int fLastId = 0;

	private BasicStatement fPremise;
	private BasicStatement fConsequence; 

	//TODO resign from the visitor pattern for better readability of the code
	private class ReferencedPartitionsProvider implements IStatementVisitor{

		@Override
		public Object visit(StaticStatement statement) throws Exception {
			return new HashSet<ChoiceNode>();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object visit(StatementArray statement) throws Exception {
			Set<ChoiceNode> set = new HashSet<ChoiceNode>();
			for(BasicStatement s : statement.getStatements()){
				set.addAll((Set<ChoiceNode>)s.accept(this));
			}
			return set;
		}

		@Override
		public Object visit(ExpectedValueStatement statement) throws Exception {
			return new HashSet<ChoiceNode>();
		}

		@Override
		public Object visit(PartitionedParameterStatement statement)
				throws Exception {
			return statement.getCondition().accept(this);
		}

		@Override
		public Object visit(LabelCondition condition) throws Exception {
			return new HashSet<ChoiceNode>();
		}

		@Override
		public Object visit(PartitionCondition condition) throws Exception {
			Set<ChoiceNode> set = new HashSet<ChoiceNode>();
			set.add(condition.getPartition());
			return set;
		}
		
	}
	
	private class ReferencedParametersProvider implements IStatementVisitor{

		@Override
		public Object visit(StaticStatement statement) throws Exception {
			return new HashSet<ParameterNode>();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object visit(StatementArray statement) throws Exception {
			Set<ParameterNode> set = new HashSet<ParameterNode>();
			for(BasicStatement s : statement.getStatements()){
				set.addAll((Set<ParameterNode>)s.accept(this));
			}
			return set;
		}

		@Override
		public Object visit(ExpectedValueStatement statement) throws Exception {
			Set<ParameterNode> set = new HashSet<ParameterNode>();
			set.add(statement.getParameter());
			return set;
		}

		@Override
		public Object visit(PartitionedParameterStatement statement)
				throws Exception {
			return statement.getCondition().accept(this);
		}

		@Override
		public Object visit(LabelCondition condition) throws Exception {
			return new HashSet<ParameterNode>();
		}

		@Override
		public Object visit(PartitionCondition condition) throws Exception {
			Set<ParameterNode> set = new HashSet<ParameterNode>();
			ParameterNode parameter = condition.getPartition().getParameter();
			if(parameter != null){
				set.add(parameter);
			}
			return set;
		}
	}
	
	private class ReferencedLabelsPrivider implements IStatementVisitor{
		
		private ParameterNode fParameter;
		private Set<String> EMPTY_SET = new HashSet<String>();
		
		public ReferencedLabelsPrivider(ParameterNode parameter){
			fParameter = parameter;
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
		public Object visit(PartitionedParameterStatement statement)
				throws Exception {
			if(fParameter == statement.getParameter()){
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
	public boolean evaluate(List<ChoiceNode> values) {
		if(fPremise == null) return true;
		if(fPremise.evaluate(values) == true){
			if(fConsequence == null) return false;
			return fConsequence.evaluate(values);
		}
		return true;
	}

	@Override
	public boolean adapt(List<ChoiceNode> values){
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
	
	public boolean mentions(ParameterNode parameter) {
		return fPremise.mentions(parameter) || fConsequence.mentions(parameter);
	}

	public boolean mentions(ParameterNode parameter, String label) {
		return fPremise.mentions(parameter, label) || fConsequence.mentions(parameter, label);
	}

	public boolean mentions(ChoiceNode partition) {
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
	public Set<ChoiceNode> getReferencedPartitions() {
		try{
			Set<ChoiceNode> referenced = (Set<ChoiceNode>)fPremise.accept(new ReferencedPartitionsProvider());
			referenced.addAll((Set<ChoiceNode>)fConsequence.accept(new ReferencedPartitionsProvider()));
			return referenced;
		}
		catch(Exception e){
			return new HashSet<ChoiceNode>();
		}
	}

	@SuppressWarnings("unchecked")
	public Set<ParameterNode> getReferencedParameters() {
		try{
			Set<ParameterNode> referenced = (Set<ParameterNode>)fPremise.accept(new ReferencedParametersProvider());
			referenced.addAll((Set<ParameterNode>)fConsequence.accept(new ReferencedParametersProvider()));
			return referenced;
		}
		catch(Exception e){
			return new HashSet<ParameterNode>();
		}
	}

	@SuppressWarnings("unchecked")
	public Set<String> getReferencedLabels(ParameterNode parameter) {
		try{
			Set<String> referenced = (Set<String>)fPremise.accept(new ReferencedLabelsPrivider(parameter));
			referenced.addAll((Set<String>)fConsequence.accept(new ReferencedLabelsPrivider(parameter)));
			return referenced;
		}
		catch(Exception e){
			return new HashSet<String>();
		}
	}
}
