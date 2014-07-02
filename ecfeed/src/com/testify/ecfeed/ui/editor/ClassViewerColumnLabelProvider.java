package com.testify.ecfeed.ui.editor;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.utils.ModelUtils;

public class ClassViewerColumnLabelProvider extends ColumnLabelProvider {
	
	private ColorManager fColorManager = new ColorManager();
	
	@Override
	public Color getForeground(Object element) {
		if (element instanceof ClassNode) {
			if (ModelUtils.isClassImplemented((ClassNode)element)) {
				return fColorManager.getColor(ColorConstants.ITEM_IMPLEMENTED);
			}
		}
		return null;
	}
}
