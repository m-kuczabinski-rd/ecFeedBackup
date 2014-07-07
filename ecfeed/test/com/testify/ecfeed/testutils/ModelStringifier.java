package com.testify.ecfeed.testutils;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.IGenericNode;
import com.testify.ecfeed.model.PartitionNode;

public class ModelStringifier {
	public String stringify(IGenericNode node, int indent){
		if(node instanceof PartitionNode){
			return stringify((PartitionNode)node, indent);
		}
		if(node instanceof CategoryNode){
			return stringify((CategoryNode)node, indent);
		}
		return null;
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
