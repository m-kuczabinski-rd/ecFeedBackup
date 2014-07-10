/*******************************************************************************
 * Copyright (c) 2014 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

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
