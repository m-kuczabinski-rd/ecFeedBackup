package com.testify.ecfeed.ui.modelif;

import java.util.List;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.JavaMethodUtils;
import com.testify.ecfeed.modelif.java.JavaUtils;
import com.testify.ecfeed.modelif.java.method.MethodOperationAddParameter;
import com.testify.ecfeed.modelif.java.method.MethodOperationConvertTo;
import com.testify.ecfeed.modelif.java.method.MethodOperationRename;
import com.testify.ecfeed.ui.editor.BasicSection;
import com.testify.ecfeed.ui.editor.IModelUpdateListener;

public class MethodInterface extends GenericNodeInterface {

	private MethodNode fTarget;

	public MethodInterface(ModelOperationManager modelOperationManager) {
		super(modelOperationManager);
	}

	public void setTarget(MethodNode target){
		super.setTarget(target);
		fTarget = target;
	}
	
	public MethodNode getTarget(){
		return fTarget;
	}

	public List<String> getArgTypes(MethodNode method) {
		return JavaMethodUtils.getArgTypes(method);
	}

	public List<String> getArgNames(MethodNode method) {
		return JavaMethodUtils.getArgNames(method);
	}

	public boolean setName(String newName, BasicSection source, IModelUpdateListener updateListener) {
		if(newName.equals(getName())){
			return false;
		}
		return execute(new MethodOperationRename(fTarget, newName), source, updateListener, Messages.DIALOG_RENAME_METHOD_PROBLEM_TITLE);
	}

	public boolean convertTo(MethodNode method, BasicSection source, IModelUpdateListener updateListener) {
		return execute(new MethodOperationConvertTo(fTarget, method), source, updateListener, Messages.DIALOG_CONVERT_METHOD_PROBLEM_TITLE);
	}
	
	public CategoryNode addNewParameter(BasicSection source, IModelUpdateListener updateListener) {
		String name = generateNewParameterName(fTarget);
		String type = generateNewParameterType(fTarget);
		CategoryNode parameter = new CategoryNode(name, type, false);
		parameter.setDefaultValueString(new EclipseModelBuilder().getDefaultExpectedValue(type));
		if(addNewParameter(parameter, source, updateListener)){
			return parameter;
		}
		return null;
	}
	
	public boolean addNewParameter(CategoryNode parameter, BasicSection source, IModelUpdateListener updateListener) {
		return execute(new MethodOperationAddParameter(fTarget, parameter), source, updateListener, Messages.DIALOG_CONVERT_METHOD_PROBLEM_TITLE);
	}

	private String generateNewParameterName(MethodNode method) {
		int i = 0;
		String name = Constants.DEFAULT_NEW_PARAMETER_NAME + i++;
		while(method.getCategory(name) != null){
			name = Constants.DEFAULT_NEW_PARAMETER_NAME + i++;
		}
		return name;
	}

	private String generateNewParameterType(MethodNode method) {
		for(String type : JavaUtils.supportedPrimitiveTypes()){
			List<String> newTypes = method.getCategoriesTypes();
			newTypes.add(type);
			if(method.getClassNode().getMethod(method.getName(), newTypes) == null){
				return type;
			}
		}
		String type = Constants.DEFAULT_USER_TYPE_NAME;
		int i = 0;
		while(true){
			List<String> newTypes = method.getCategoriesTypes();
			newTypes.add(type);
			if(method.getClassNode().getMethod(method.getName(), newTypes) == null){
				break;
			}
			else{
				type = Constants.DEFAULT_USER_TYPE_NAME + i;
			}
		}
		return type;
	}
	

	
}
