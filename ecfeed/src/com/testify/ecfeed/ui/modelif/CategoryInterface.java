package com.testify.ecfeed.ui.modelif;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.category.CategoryOperationRename;
import com.testify.ecfeed.modelif.java.category.CategoryOperationSetExpected;
import com.testify.ecfeed.modelif.java.category.CategoryOperationSetType;
import com.testify.ecfeed.modelif.java.category.ITypeAdapterProvider;
import com.testify.ecfeed.ui.editor.BasicSection;
import com.testify.ecfeed.ui.editor.IModelUpdateListener;

public class CategoryInterface extends GenericNodeInterface {
	
	private CategoryNode fTarget;
	
	public CategoryInterface(ModelOperationManager modelOperationManager) {
		super(modelOperationManager);
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
				boolean constraints = false;
				for(ConstraintNode c : method.getConstraintNodes()){
					if(c.mentions(fTarget)){
						constraints = true;
						break;
					}
				}
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
					if(testCases || constraints){
						if(MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), 
								Messages.DIALOG_SET_CATEGORY_EXPECTED_WARNING_TITLE, message) == false){
							return false;
						}
					}
				}
			}
			return execute(new CategoryOperationSetExpected(fTarget, expected), source, updateListener, Messages.DIALOG_SET_CATEGORY_EXPECTED_PROBLEM_TITLE);
		}
		return false;
	}
	

}
