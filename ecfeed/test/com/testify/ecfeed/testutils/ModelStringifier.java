/*******************************************************************************
 * Copyright (c) 2014 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.testutils;

import com.testify.ecfeed.model.AbstractStatement;
import com.testify.ecfeed.model.ParameterNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.ExpectedValueStatement;
import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.DecomposedParameterStatement;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.StatementArray;
import com.testify.ecfeed.model.StaticStatement;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.model.DecomposedParameterStatement.LabelCondition;
import com.testify.ecfeed.model.DecomposedParameterStatement.ChoiceCondition;

public class ModelStringifier {
	public String stringify(AbstractNode node, int indent){
		if(node instanceof ChoiceNode){
			return stringify((ChoiceNode)node, indent);
		}
		if(node instanceof ParameterNode){
			return stringify((ParameterNode)node, indent);
		}
		if(node instanceof MethodNode){
			return stringify((MethodNode)node, indent);
		}
		if(node instanceof TestCaseNode){
			return stringify((TestCaseNode)node, indent);
		}
		if(node instanceof ConstraintNode){
			return stringify((ConstraintNode)node, indent);
		}
		if(node instanceof ClassNode){
			return stringify((ClassNode)node, indent);
		}
		if(node instanceof RootNode){
			return stringify((RootNode)node, indent);
		}
		return null;
	}

	public String stringify(AbstractStatement statement, int indent){
		if(statement instanceof StaticStatement){
			return stringify((StaticStatement)statement, indent);
		}
		if(statement instanceof DecomposedParameterStatement){
			return stringify((DecomposedParameterStatement)statement, indent);
		}
		if(statement instanceof ExpectedValueStatement){
			return stringify((ExpectedValueStatement)statement, indent);
		}
		if(statement instanceof StatementArray){
			return stringify((StatementArray)statement, indent);
		}
		
		return null;
	}

	public String stringify(RootNode r, int indent){
		String result = intendentString(indent);
		result += "Model " + r.getName();
		
		for(ClassNode c : r.getClasses()){
			result += "\n" + stringify(c, indent + 2);
		}
		
		return result;
	}
	
	public String stringify(ClassNode c, int indent){
		String result = intendentString(indent);
		result += "Class " + c.getQualifiedName();
		
		for(MethodNode m : c.getMethods()){
			result += "\n" + stringify(m, indent + 2);
		}
		
		return result;
	}
	
	public String stringify(MethodNode m, int indent){
		String result = intendentString(indent);
		result += "Method " + m.toString();
		for(ParameterNode child : m.getParameters()){
			result += "\n";
			result += stringify(child, indent + 2);
		}
		for(ConstraintNode child : m.getConstraintNodes()){
			result += "\n";
			result += stringify(child, indent + 2);
		}
		for(TestCaseNode child : m.getTestCases()){
			result += "\n";
			result += stringify(child, indent + 2);
		}

		return result;
	}
	
	public String stringify(ParameterNode c, int indent){
		String result = intendentString(indent);
		result += "Parameter " + c.getName() + "[" + c.getType() + "], " + (c.isExpected() ? "expected" : "patitioned");
		result += " default value: " + c.getDefaultValue(); 
		for(ChoiceNode child : c.getChoices()){
			result += "\n";
			result += stringify(child, indent + 2);
		}
		return result;
	}
	
	public String stringify(TestCaseNode tc, int indent){
		String result = intendentString(indent);
		result += "Test case " + tc.toString() + "[";
		for(ChoiceNode p : tc.getTestData()){
			if(p.getParameter().isExpected()){
				result += "[e]" + p.getValueString();
			}
			else{
				result += p.getQualifiedName();
			}
			result += " ";
		}
		
		return result + "]";
	}

	public String stringify(ConstraintNode node, int indent){
		String result = intendentString(indent);
		result += "Constraint " + node.getName() + "\n"; 
		for(int i = 0; i < indent + 2; i++){
			result += " ";
		}
		result += "Premise:\n";
		result += stringify(node.getConstraint().getPremise(), indent + 4);
		result += "\n";
		for(int i = 0; i < indent + 2; i++){
			result += " ";
		}
		result += "Consequence:\n";
		result += stringify(node.getConstraint().getConsequence(), indent + 4);
		return result;
	}
	
	public String stringify(ChoiceNode p, int indent){
		String result = intendentString(indent);
		result += "Choice ";
		result += p.getName() + "[" + p.getValueString() + "]";
		result += ", Labels: ";
		for(String label : p.getLabels()){
			result += label + " ";
		}
		for(ChoiceNode child : p.getChoices()){
			result += "\n";
			result += stringify(child, indent + 2);
		}
		return result;
	}
	
	public String stringify(StaticStatement s, int indent){
		String result = intendentString(indent);
		result += "Static statement " + s.getValue();
		return result;
	}
	
	public String stringify(DecomposedParameterStatement s, int indent){
		String result = intendentString(indent);
		result += "Decomposed statement ";
		if(s.getCondition() instanceof LabelCondition){
			result += "[label] ";
		}
		else if(s.getCondition() instanceof ChoiceCondition){
			result += "[choice] ";
		}
		result += s.toString();
		return result;
	}
	
	public String stringify(ExpectedValueStatement s, int indent){
		String result = intendentString(indent);
		result += "Expected value statement ";
		result += s.getParameter().getName() + "[" + s.getParameter().getType() + "] " + s.getRelation() + " " + s.getCondition().getValueString();
		return result;
	}

	public String stringify(StatementArray s, int indent){
		String result = intendentString(indent);
		result += "Statement array " + s.getOperator();
		for(AbstractStatement child : s.getChildren()){
			result += "\n" + stringify(child, indent + 2);
		}
		return result;
	}

	private String intendentString(int indent){
		String result = new String();
		for(int i = 0; i < indent; i++){
			result += " ";
		}
		return result;
	}
	
}
