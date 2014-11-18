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

import java.util.List;

public class ParameterNode extends PartitionedNode{
	
	private String fType;
	private boolean fExpected;
	private String fDefaultValue;
	
	public ParameterNode(String name, String type, String defaultValue, boolean expected) {
		super(name);
		fExpected = expected;
		fType = type;
		fDefaultValue = defaultValue;
	}
	
	@Override
	public int getIndex(){
		if(getMethod() == null){
			return -1;
		}
		return getMethod().getParameters().indexOf(this);
	}

	@Override
	public String toString(){
		if(fExpected){
			return super.toString() + "(" + getDefaultValue() + "): " + getType();
		}
		return new String(getName() + ": " + getType());
	}
	
	public void addPartitions(List<ChoiceNode> partitions) {
		for(ChoiceNode p : partitions){
			addPartition(p);
		}
	}

	@Override
	public ParameterNode getCopy(){
		ParameterNode parameter = new ParameterNode(getName(), getType(), getDefaultValue(), isExpected());
		parameter.setParent(this.getParent());
		if(getDefaultValue() != null)
			parameter.setDefaultValueString(getDefaultValue());
		for(ChoiceNode partition : getPartitions()){
			parameter.addPartition(partition.getCopy());
		}
		parameter.setParent(getParent());
		return parameter;
	}
	
	@Override
	public ParameterNode getParameter() {
		return this;
	}
	
	public String getType() {
		return fType;
	}

	public void setType(String type) {
		fType = type;
	}

	public MethodNode getMethod() {
		return (MethodNode)getParent();
	}

	public String getDefaultValue() {
		return fDefaultValue;
	}

	public void setDefaultValueString(String value) {
		fDefaultValue = value;
	}
	
	public boolean isExpected(){
		return fExpected;
	}
	
	public void setExpected(boolean isexpected){
		fExpected = isexpected;
	}
	
	public String toShortString(){
		if(fExpected){
			return toString();
		}

		return new String(getName() + ": " + getShortType());
	}
	
	public String getShortType(){
		String type = fType;
		int lastindex = type.lastIndexOf(".");
		if(!(lastindex == -1 || lastindex >= type.length())){
			type = type.substring(lastindex + 1);
		}
		return new String(type);
	}

	@Override
	public boolean compare(GenericNode node){
		if(node instanceof ParameterNode == false){
			return false;
		}
		ParameterNode comparedParameter = (ParameterNode)node;
		
		if(getType().equals(comparedParameter.getType()) == false){
			return false;
		}

		if(isExpected() != comparedParameter.isExpected()){
			return false;
		}
		
		if(fDefaultValue.equals(comparedParameter.getDefaultValue()) == false){
			return false;
		}

		int partitionsCount = getPartitions().size();
		if(partitionsCount != comparedParameter.getPartitions().size()){
			return false;
		}

		for(int i = 0; i < partitionsCount; i++){
			if(getPartitions().get(i).compare(comparedParameter.getPartitions().get(i)) == false){
				return false;
			}
		}

		return super.compare(node);
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override 
	public int getMaxIndex(){
		if(getMethod() != null){
			return getMethod().getParameters().size();
		}
		return -1;
	}
}
