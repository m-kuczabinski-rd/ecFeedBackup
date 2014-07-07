package com.testify.ecfeed.testutils;

import com.testify.ecfeed.model.PartitionNode;

public class ModelStringifier {
	public String stringify(PartitionNode p, int indent){
		String result = intendentString(indent);
		result += "Partition ";
		result += p.toString();
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
