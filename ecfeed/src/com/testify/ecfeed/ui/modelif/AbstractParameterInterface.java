package com.testify.ecfeed.ui.modelif;

import java.util.Arrays;
import java.util.List;

import com.testify.ecfeed.adapter.ITypeAdapterProvider;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.adapter.operations.AbstractParameterOperationSetType;
import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.ui.common.EclipseModelBuilder;
import com.testify.ecfeed.ui.common.EclipseTypeAdapterProvider;
import com.testify.ecfeed.ui.common.Messages;

public class AbstractParameterInterface extends ChoicesParentInterface {

	private AbstractParameterNode fTarget;
	private ITypeAdapterProvider fAdapterProvider;

	public AbstractParameterInterface(IModelUpdateContext updateContext) {
		super(updateContext);
		fAdapterProvider = new EclipseTypeAdapterProvider();
	}

	public void setTarget(AbstractParameterNode target){
		super.setTarget(target);
		fTarget = target;
	}

	public boolean setType(String newType) {
		if(newType.equals(fTarget.getType())){
			return false;
		}
		return execute(new AbstractParameterOperationSetType(fTarget, newType, fAdapterProvider), Messages.DIALOG_RENAME_PAREMETER_PROBLEM_TITLE);
	}

	protected ITypeAdapterProvider getTypeAdapterProvider(){
		return fAdapterProvider;
	}

	public String getType() {
		return fTarget.getType();
	}

	public static boolean hasLimitedValuesSet(String type) {
		return !isPrimitive(type) || isBoolean(type);
	}

	public static boolean hasLimitedValuesSet(AbstractParameterNode parameter) {
		return hasLimitedValuesSet(parameter.getType());
	}

	public static boolean isPrimitive(String type) {
		return Arrays.asList(JavaUtils.supportedPrimitiveTypes()).contains(type);
	}

	public static boolean isUserType(String type) {
		return !isPrimitive(type);
	}

	public static boolean isBoolean(String type){
		return type.equals(JavaUtils.getBooleanTypeName());
	}

	public static List<String> getSpecialValues(String type) {
		return new EclipseModelBuilder().getSpecialValues(type);
	}

	public static String[] supportedPrimitiveTypes() {
		return JavaUtils.supportedPrimitiveTypes();
	}
}
