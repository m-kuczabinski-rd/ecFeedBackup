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

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.ExpectedValueStatement;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement.LabelCondition;
import com.testify.ecfeed.model.constraint.PartitionedCategoryStatement.PartitionCondition;
import com.testify.ecfeed.model.constraint.StatementArray;
import com.testify.ecfeed.model.constraint.StaticStatement;

public class ModelStringifier {
	public String stringify(GenericNode node, int indent){
		if(node instanceof PartitionNode){
			return stringify((PartitionNode)node, indent);
		}
		if(node instanceof CategoryNode){
			return stringify((CategoryNode)node, indent);
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

	public String stringify(BasicStatement statement, int indent){
		if(statement instanceof StaticStatement){
			return stringify((StaticStatement)statement, indent);
		}
		if(statement instanceof PartitionedCategoryStatement){
			return stringify((PartitionedCategoryStatement)statement, indent);
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
		for(CategoryNode child : m.getCategories()){
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
	
	public String stringify(CategoryNode c, int indent){
		String result = intendentString(indent);
		result += "Category " + c.getName() + "[" + c.getType() + "], " + (c.isExpected() ? "expected" : "patitioned");
		result += " default value: " + c.getDefaultValueString(); 
		for(PartitionNode child : c.getPartitions()){
			result += "\n";
			result += stringify(child, indent + 2);
		}
		return result;
	}
	
	public String stringify(TestCaseNode tc, int indent){
		String result = intendentString(indent);
		result += "Test case " + tc.toString() + "[";
		for(PartitionNode p : tc.getTestData()){
			if(p.getCategory().isExpected()){
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
	
	public String stringify(PartitionNode p, int indent){
		String result = intendentString(indent);
		result += "Partition ";
		result += p.getName() + "[" + p.getValueString() + "]";
		result += ", Labels: ";
		for(String label : p.getLabels()){
			result += label + " ";
		}
		for(PartitionNode child : p.getPartitions()){
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
	
	public String stringify(PartitionedCategoryStatement s, int indent){
		String result = intendentString(indent);
		result += "Partitioned statement ";
		if(s.getCondition() instanceof LabelCondition){
			result += "[label] ";
		}
		else if(s.getCondition() instanceof PartitionCondition){
			result += "[partition] ";
		}
		result += s.toString();
		return result;
	}
	
	public String stringify(ExpectedValueStatement s, int indent){
		String result = intendentString(indent);
		result += "Expected value statement ";
		result += s.getCategory().getName() + "[" + s.getCategory().getType() + "] " + s.getRelation() + " " + s.getCondition().getValueString();
		return result;
	}

	public String stringify(StatementArray s, int indent){
		String result = intendentString(indent);
		result += "Statement array " + s.getOperator();
		for(BasicStatement child : s.getChildren()){
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
