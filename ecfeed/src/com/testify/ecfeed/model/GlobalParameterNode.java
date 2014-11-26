package com.testify.ecfeed.model;

import java.util.ArrayList;
import java.util.List;

public class GlobalParameterNode extends AbstractParameterNode {

	public GlobalParameterNode(String name, String type) {
		super(name, type);
	}

	@Override
	public GlobalParameterNode getCopy() {
		GlobalParameterNode copy = new GlobalParameterNode(getName(), getType());
		for(ChoiceNode choice : getChoices()){
			copy.addChoice(choice.getCopy());
		}
		return copy;
	}

	@Override
	public List<MethodNode> getMethods() {
		return getParametersParent().getMethods(getParameter());
	}

	public List<MethodParameterNode> getLinkers(){
		List<MethodParameterNode> result = new ArrayList<>();
		for(MethodNode method : getMethods()){
			result.addAll(method.getLinkers(this));
		}
		return result;
	}

	@Override
	public Object accept(IParameterVisitor visitor) throws Exception {
		return visitor.visit(this);
	}

	@Override
	public Object accept(IModelVisitor visitor) throws Exception {
		return visitor.visit(this);
	}
}
