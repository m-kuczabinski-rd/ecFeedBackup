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

import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.generators.api.IConstraint;
import com.testify.ecfeed.model.ChoicesParentStatement.ChoiceCondition;
import com.testify.ecfeed.model.ChoicesParentStatement.LabelCondition;

public class Constraint implements IConstraint<ChoiceNode> {

	private final int ID;
	private static int fLastId = 0;

	private AbstractStatement fPremise;
	private AbstractStatement fConsequence;

	//TODO resign from the visitor pattern for better readability of the code
	private class ReferencedChoicesProvider implements IStatementVisitor{

		@Override
		public Object visit(StaticStatement statement) throws Exception {
			return new HashSet<ChoiceNode>();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object visit(StatementArray statement) throws Exception {
			Set<ChoiceNode> set = new HashSet<ChoiceNode>();
			for(AbstractStatement s : statement.getStatements()){
				set.addAll((Set<ChoiceNode>)s.accept(this));
			}
			return set;
		}

		@Override
		public Object visit(ExpectedValueStatement statement) throws Exception {
			Set<ChoiceNode> result = new HashSet<>();
			String type = statement.getParameter().getType();
			if(JavaUtils.isUserType(type)){
				result.add(statement.getCondition());
			}
			return result;
		}

		@Override
		public Object visit(ChoicesParentStatement statement)
				throws Exception {
			return statement.getCondition().accept(this);
		}

		@Override
		public Object visit(LabelCondition condition) throws Exception {
			return new HashSet<ChoiceNode>();
		}

		@Override
		public Object visit(ChoiceCondition condition) throws Exception {
			Set<ChoiceNode> set = new HashSet<ChoiceNode>();
			set.add(condition.getChoice());
			return set;
		}

	}

	private class ReferencedParametersProvider implements IStatementVisitor{

		@Override
		public Object visit(StaticStatement statement) throws Exception {
			return new HashSet<MethodParameterNode>();
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object visit(StatementArray statement) throws Exception {
			Set<MethodParameterNode> set = new HashSet<MethodParameterNode>();
			for(AbstractStatement s : statement.getStatements()){
				set.addAll((Set<MethodParameterNode>)s.accept(this));
			}
			return set;
		}

		@Override
		public Object visit(ExpectedValueStatement statement) throws Exception {
			Set<AbstractParameterNode> set = new HashSet<AbstractParameterNode>();
			set.add(statement.getParameter());
			return set;
		}

		@Override
		public Object visit(ChoicesParentStatement statement)
				throws Exception {
			return statement.getCondition().accept(this);
		}

		@Override
		public Object visit(LabelCondition condition) throws Exception {
			return new HashSet<MethodParameterNode>();
		}

		@Override
		public Object visit(ChoiceCondition condition) throws Exception {
			Set<AbstractParameterNode> set = new HashSet<AbstractParameterNode>();
			AbstractParameterNode parameter = condition.getChoice().getParameter();
			if(parameter != null){
				set.add(parameter);
			}
			return set;
		}
	}

	private class ReferencedLabelsPrivider implements IStatementVisitor{

		private MethodParameterNode fParameter;
		private Set<String> EMPTY_SET = new HashSet<String>();

		public ReferencedLabelsPrivider(MethodParameterNode parameter){
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
			for(AbstractStatement s : statement.getStatements()){
				set.addAll((Set<String>)s.accept(this));
			}
			return set;
		}

		@Override
		public Object visit(ExpectedValueStatement statement) throws Exception {
			return EMPTY_SET;
		}

		@Override
		public Object visit(ChoicesParentStatement statement)
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
		public Object visit(ChoiceCondition condition) throws Exception {
			return EMPTY_SET;
		}

	}

	public Constraint(AbstractStatement premise, AbstractStatement consequence){
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

	public AbstractStatement getPremise(){
		return fPremise;
	}

	public AbstractStatement getConsequence(){
		return fConsequence;
	}

	public void setPremise(AbstractStatement statement){
		fPremise = statement;
	}

	public void setConsequence(AbstractStatement consequence){
		fConsequence = consequence;
	}

	public boolean mentions(MethodParameterNode parameter) {
		return fPremise.mentions(parameter) || fConsequence.mentions(parameter);
	}

	public boolean mentions(MethodParameterNode parameter, String label) {
		return fPremise.mentions(parameter, label) || fConsequence.mentions(parameter, label);
	}

	public boolean mentions(ChoiceNode choice) {
		return fPremise.mentions(choice) || fConsequence.mentions(choice);
	}

	public Constraint getCopy(){
		AbstractStatement premise = fPremise.getCopy();
		AbstractStatement consequence = fConsequence.getCopy();
		return new Constraint(premise, consequence);
	}

	public boolean updateRefrences(MethodNode method){
		if(fPremise.updateReferences(method) && fConsequence.updateReferences(method))
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public Set<ChoiceNode> getReferencedChoices() {
		try{
			Set<ChoiceNode> referenced = (Set<ChoiceNode>)fPremise.accept(new ReferencedChoicesProvider());
			referenced.addAll((Set<ChoiceNode>)fConsequence.accept(new ReferencedChoicesProvider()));
			return referenced;
		}
		catch(Exception e){
			return new HashSet<ChoiceNode>();
		}
	}

	@SuppressWarnings("unchecked")
	public Set<AbstractParameterNode> getReferencedParameters() {
		try{
			Set<AbstractParameterNode> referenced = (Set<AbstractParameterNode>)fPremise.accept(new ReferencedParametersProvider());
			referenced.addAll((Set<AbstractParameterNode>)fConsequence.accept(new ReferencedParametersProvider()));
			return referenced;
		}
		catch(Exception e){
			return new HashSet<AbstractParameterNode>();
		}
	}

	@SuppressWarnings("unchecked")
	public Set<String> getReferencedLabels(MethodParameterNode parameter) {
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
