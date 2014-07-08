package com.testify.ecfeed.testutils;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.IGenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;

public class ModelStringifier {
	public String stringify(IGenericNode node, int indent){
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
		return null;
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
	
	private String intendentString(int indent){
		String result = new String();
		for(int i = 0; i < indent; i++){
			result += " ";
		}
		return result;
	}
	
}
