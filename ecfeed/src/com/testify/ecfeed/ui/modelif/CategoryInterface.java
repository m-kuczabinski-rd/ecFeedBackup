package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.category.CategoryOperationRename;
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

}
