package com.testify.ecfeed.ui.modelif;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.JavaUtils;
import com.testify.ecfeed.modelif.java.category.CategoryOperationRename;
import com.testify.ecfeed.modelif.java.category.CategoryOperationSetDefaultValue;
import com.testify.ecfeed.modelif.java.category.CategoryOperationSetExpected;
import com.testify.ecfeed.modelif.java.category.CategoryOperationSetType;
import com.testify.ecfeed.modelif.java.category.ITypeAdapterProvider;
import com.testify.ecfeed.ui.editor.BasicSection;
import com.testify.ecfeed.ui.editor.IModelUpdateListener;
import com.testify.ecfeed.ui.editor.TableViewerSection;

public class CategoryInterface extends GenericNodeInterface {
	
	private CategoryNode fTarget;
	private ITypeAdapterProvider fAdapterProvider;
	
	public CategoryInterface(ModelOperationManager modelOperationManager) {
		super(modelOperationManager);
		fAdapterProvider = new TypeAdaptationSupport().getAdapterProvider();
	}

	public void setTarget(CategoryNode target){
		super.setTarget(target);
		fTarget = target;
	}

	public String getDefaultValue(String type) {
		return new EclipseModelBuilder().getDefaultExpectedValue(type);
	}

	public boolean setName(String newName, BasicSection source, IModelUpdateListener updateListener) {
		if(newName.equals(getName())){
			return false;
		}
		return execute(new CategoryOperationRename(fTarget, newName), source, updateListener, Messages.DIALOG_RENAME_METHOD_PROBLEM_TITLE);
	}

	public boolean setType(String newType, BasicSection source, IModelUpdateListener updateListener) {
		ITypeAdapterProvider adapterProvider = new TypeAdaptationSupport().getAdapterProvider();
		if(newType.equals(fTarget.getType())){
			return false;
		}
		return execute(new CategoryOperationSetType(fTarget, newType, adapterProvider), source, updateListener, Messages.DIALOG_RENAME_METHOD_PROBLEM_TITLE);
	}
	
	public boolean setExpected(boolean expected, BasicSection source, IModelUpdateListener updateListener){
		if(expected != fTarget.isExpected()){
			MethodNode method = fTarget.getMethod();
			if(method != null){
				boolean testCases = method.getTestCases().size() > 0;
				boolean constraints = method.mentioningConstraints(fTarget).size() > 0;
				if(testCases || constraints){
					String message = "";
					if(testCases){
						if(expected){
							message += Messages.DIALOG_SET_CATEGORY_EXPECTED_TEST_CASES_ALTERED + "\n";
						}
						else{
							message += Messages.DIALOG_SET_CATEGORY_EXPECTED_TEST_CASES_REMOVED + "\n";
						}
					}
					if(constraints){
						message += Messages.DIALOG_SET_CATEGORY_EXPECTED_CONSTRAINTS_REMOVED;
					}
					if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), 
							Messages.DIALOG_SET_CATEGORY_EXPECTED_WARNING_TITLE, message) == false){
						return false;
					}
				}
			}
			return execute(new CategoryOperationSetExpected(fTarget, expected), source, updateListener, Messages.DIALOG_SET_CATEGORY_EXPECTED_PROBLEM_TITLE);
		}
		return false;
	}

	public boolean setDefaultValue(String valueString, TableViewerSection source, IModelUpdateListener updateListener) {
		if(fTarget.getDefaultValueString().equals(valueString) == false){
			return execute(new CategoryOperationSetDefaultValue(fTarget, valueString, fAdapterProvider.getAdapter(fTarget.getType())), source, updateListener, Messages.DIALOG_SET_DEFAULT_VALUE_PROBLEM_TITLE);
		}
		return false;
	}

	public boolean hasLimitedValuesSet() {
		return !isPrimitive(fTarget.getType()) || isBoolean(fTarget.getType());
	}

	public static boolean hasLimitedValuesSet(CategoryNode category) {
		String type = category.getType();
		return !isPrimitive(type) || isBoolean(type);
	}

	public static boolean isPrimitive(String type) {
		return Arrays.asList(JavaUtils.supportedPrimitiveTypes()).contains(type);
	}
	
	public boolean isPrimitive() {
		return isPrimitive(fTarget.getType());
	}
	
	private static boolean isBoolean(String type){
		return type.equals(JavaUtils.getBooleanTypeName());
	}

	public List<String> getSpecialValues() {
		return new EclipseModelBuilder().getSpecialValues(fTarget.getType());
	}

	public static List<String> getSpecialValues(String type) {
		return new EclipseModelBuilder().getSpecialValues(type);
	}

	public String getType() {
		return fTarget.getType();
	}

	public static String[] supportedPrimitiveTypes() {
		return JavaUtils.supportedPrimitiveTypes();
	}

	public String getDefaultValue() {
		return fTarget.getDefaultValueString();
	}

	public boolean isExpected() {
		return fTarget.isExpected();
	}
}
