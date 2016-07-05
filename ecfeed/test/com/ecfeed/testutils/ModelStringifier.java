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

package com.ecfeed.testutils;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.AbstractStatement;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ChoicesParentStatement;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.ExpectedValueStatement;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.StatementArray;
import com.ecfeed.core.model.StaticStatement;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.model.ChoicesParentStatement.ChoiceCondition;
import com.ecfeed.core.model.ChoicesParentStatement.LabelCondition;

public class ModelStringifier {
	public String stringify(AbstractNode node, int indent){
		if(node instanceof ChoiceNode){
			return stringify((ChoiceNode)node, indent);
		}
		if(node instanceof MethodParameterNode){
			return stringify((MethodParameterNode)node, indent);
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
		if(statement instanceof ChoicesParentStatement){
			return stringify((ChoicesParentStatement)statement, indent);
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
		result += "Class " + c.getName();

		for(MethodNode m : c.getMethods()){
			result += "\n" + stringify(m, indent + 2);
		}

		return result;
	}

	public String stringify(MethodNode m, int indent){
		String result = intendentString(indent);
		result += "Method " + m.toString();
		for(MethodParameterNode child : m.getMethodParameters()){
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

	public String stringify(MethodParameterNode c, int indent){
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
		for(ChoiceNode choice : tc.getTestData()){
			MethodParameterNode parameter = tc.getMethodParameter(choice);
			if(parameter.isExpected()){
				result += "[e]" + choice.getValueString();
			}
			else{
				result += choice.getQualifiedName();
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

	public String stringify(ChoicesParentStatement s, int indent){
		String result = intendentString(indent);
		result += "Choices parent statement ";
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
