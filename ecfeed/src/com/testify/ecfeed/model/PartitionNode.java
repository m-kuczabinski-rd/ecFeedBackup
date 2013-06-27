package com.testify.ecfeed.model;

public class PartitionNode extends GenericNode {

	private Object fValue;

	public PartitionNode(String name, Object value) {
		super(name);
		fValue = value;
	}

	public Object getValue() {
		return fValue;
	}

	public void setValue(Object value) {
		this.fValue = value;
	}
	
	public String toString(){
		if(fValue instanceof Character){
			return new String(getName() + ": '\\" + (int)((char)fValue ) + "'");
		}
		return new String(getName() + ": " + String.valueOf(fValue));
	}
	
	@Override 
	public boolean equals(Object obj){
		if(obj instanceof PartitionNode != true){
			return false;
		}
		PartitionNode partition = (PartitionNode)obj;
		Object partitionValue = partition.getValue();
		if(fValue != null){
			if(!fValue.equals(partitionValue)){
				return false;
			}
		}
		else{
			if(partitionValue != null){
				return false;
			}
		}
		return super.equals(partition);
	}
}
