package com.testify.ecfeed.ui.editor;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.classx.JavaClassUtils;

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
		ClassNode target = (ClassNode)element;
		String packageName = JavaClassUtils.getPackageName(target);
		String localName = (String)value;
		renameClass(target, JavaClassUtils.getQualifiedName(packageName, localName));
	}

}
