package com.testify.ecfeed.model;

public class GlobalParameterNode extends AbstractParameterNode {

	public GlobalParameterNode(String name, String type) {
		super(name, type);
	}

	@Override
	public MethodParameterNode getParameter() {
		return null;
	}

	@Override
	public AbstractNode getCopy() {
		GlobalParameterNode copy = new GlobalParameterNode(getName(), getType());
		for(ChoiceNode choice : getChoices()){
			copy.addChoice(choice.getCopy());
		}
		return copy;
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception {
		//adapt once the visitor is extended
		return visitor.visit((MethodParameterNode)null);
	}


}
