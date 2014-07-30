package com.testify.ecfeed.ui.editor;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.classx.JavaClassUtils;
import com.testify.ecfeed.ui.modelif.ClassInterface;

public class PackageNameEditingSupport extends ClassNameEditingSupport{

	public PackageNameEditingSupport(ClassViewer viewer, ModelOperationManager operationManager) {
		super(viewer, operationManager);
	}

	@Override
	protected Object getValue(Object element) {
		return JavaClassUtils.getPackageName((ClassNode)element);
	}

	@Override
	protected void setValue(Object element, Object value) {
		ClassInterface classIf = new ClassInterface(fOperationManager);
		classIf.setTarget((ClassNode)element);
		classIf.setPackageName((String)value, fSection, fSection.getUpdateListener());
	}
}
