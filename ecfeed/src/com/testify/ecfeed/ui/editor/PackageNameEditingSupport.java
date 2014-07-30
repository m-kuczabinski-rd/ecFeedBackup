package com.testify.ecfeed.ui.editor;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.modelif.ModelOperationManager;
import com.testify.ecfeed.modelif.java.classx.JavaClassUtils;

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
		ClassNode target = (ClassNode)element;
		String localName = JavaClassUtils.getLocalName(target);
		String packageName = (String)value;
		renameClass(target, JavaClassUtils.getQualifiedName(packageName, localName));
	}
}
