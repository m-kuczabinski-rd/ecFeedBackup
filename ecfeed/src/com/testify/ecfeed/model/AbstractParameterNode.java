package com.testify.ecfeed.model;

import java.util.List;
import java.util.Set;

public abstract class AbstractParameterNode extends ChoicesParentNode {

	private String fType;
	private String fTypeComments;

	public AbstractParameterNode(String name, String type) {
		super(name);
		fType = type;
	}

	@Override
	public AbstractParameterNode getParameter() {
		return this;
	}

	public ParametersParentNode getParametersParent(){
		return (ParametersParentNode)getParent();
	}

	@Override
	public int getIndex(){
		if(getParametersParent() == null){
			return -1;
		}
		return getParametersParent().getParameters().indexOf(this);
	}

	@Override
	public int getMaxIndex(){
		if(getParametersParent() != null){
			return getParametersParent().getParameters().size();
		}
		return -1;
	}

	public String getType() {
		return fType;
	}

	public void setType(String type) {
		fType = type;
	}

	public String getTypeComments() {
		return fTypeComments;
	}

	public void setTypeComments(String comments){
		fTypeComments = comments;
	}

	@Override
	public boolean compare(AbstractNode compared){
		if(compared instanceof AbstractParameterNode == false){
			return false;
		}
		AbstractParameterNode comparedParameter = (AbstractParameterNode)compared;
		if(comparedParameter.getType().equals(fType) == false){
			return false;
		}
		return super.compare(compared);
	}

	public abstract List<MethodNode> getMethods();
	public abstract Object accept(IParameterVisitor visitor) throws Exception;

	public abstract Set<ConstraintNode> mentioningConstraints();
	public abstract Set<ConstraintNode> mentioningConstraints(String label);

}
