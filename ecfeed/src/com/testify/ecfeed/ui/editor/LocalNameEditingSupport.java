package com.testify.ecfeed.ui.editor;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.classx.JavaClassUtils;
import com.testify.ecfeed.ui.modelif.ClassInterface;

public class LocalNameEditingSupport extends ClassNameEditingSupport {

	public LocalNameEditingSupport(ClassViewer viewer, ModelOperationManager operationManager) {
		super(viewer, operationManager);
	}

	@Override
	protected Object getValue(Object element) {
		return JavaClassUtils.getLocalName((ClassNode)element);
	}

	@Override
	protected void setValue(Object element, Object value) {
		ClassInterface classIf = new ClassInterface(fOperationManager);
		classIf.setTarget((ClassNode)element);
		classIf.setLocalName((String)value, fSection, fSection.getUpdateListener());
	}

}
