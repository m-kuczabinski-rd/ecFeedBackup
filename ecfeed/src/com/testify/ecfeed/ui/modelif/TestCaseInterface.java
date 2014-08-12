package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.ui.editor.BasicSection;
import com.testify.ecfeed.ui.editor.IModelUpdateListener;

public class TestCaseInterface extends GenericNodeInterface {

	public TestCaseInterface(ModelOperationManager modelOperationManager) {
		super(modelOperationManager);
	}

	@Override
	public boolean setName(String newName, BasicSection source, IModelUpdateListener updateListener) {
		return false;
	}
	
}
