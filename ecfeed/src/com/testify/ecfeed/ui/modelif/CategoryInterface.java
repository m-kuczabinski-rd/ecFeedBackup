package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.modelif.ModelOperationManager;

public class CategoryInterface extends GenericNodeInterface {

	public CategoryInterface(ModelOperationManager modelOperationManager) {
		super(modelOperationManager);
	}

	public String getDefaultValue(String type) {
		return new EclipseModelBuilder().getDefaultExpectedValue(type);
	}

}
