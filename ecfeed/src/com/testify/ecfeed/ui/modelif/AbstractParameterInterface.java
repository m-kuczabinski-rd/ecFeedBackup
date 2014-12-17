package com.testify.ecfeed.ui.modelif;

import java.util.Arrays;
import java.util.List;

import com.testify.ecfeed.adapter.ITypeAdapterProvider;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.ui.common.EclipseModelBuilder;
import com.testify.ecfeed.ui.common.EclipseTypeAdapterProvider;

public abstract class AbstractParameterInterface extends ChoicesParentInterface {

	private ITypeAdapterProvider fAdapterProvider;

	public AbstractParameterInterface(IModelUpdateContext updateContext) {
		super(updateContext);
		fAdapterProvider = new EclipseTypeAdapterProvider();
	}

	public abstract boolean setType(String newType);

	protected ITypeAdapterProvider getTypeAdapterProvider(){
		return fAdapterProvider;
	}

	public String getType() {
		return getTarget().getType();
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

	@Override
	public boolean goToImplementationEnabled(){
		if(JavaUtils.isUserType(getTarget().getType()) == false){
			return false;
		}
		return super.goToImplementationEnabled();
	}

	@Override
	protected AbstractParameterNode getTarget(){
		return (AbstractParameterNode)super.getTarget();
	}
}
