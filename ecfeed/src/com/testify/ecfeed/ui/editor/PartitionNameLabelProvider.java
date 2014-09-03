/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                   
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

import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.modelif.ImplementationStatus;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.ui.modelif.GenericNodeInterface;

public class PartitionNameLabelProvider extends ColumnLabelProvider {
	private ColorManager fColorManager;
	private GenericNodeInterface fNodeIf;

	public PartitionNameLabelProvider() {
		fColorManager = new ColorManager();
		fNodeIf = new GenericNodeInterface(null);
	}
	
	@Override
	public String getText(Object element){
		if(element instanceof PartitionNode){
			return ((PartitionNode)element).getName();
		}
		return "";
	}

	@Override
	public Color getForeground(Object element){
		if(element instanceof PartitionNode){
			PartitionNode partition = (PartitionNode)element;
			if(partition.isAbstract()){
				return fColorManager.getColor(ColorConstants.ABSTRACT_PARTITION);
			} else if (fNodeIf.implementationStatus(partition) == ImplementationStatus.IMPLEMENTED) {
				return fColorManager.getColor(ColorConstants.ITEM_IMPLEMENTED);
			}
		}
		return null;
	}
}
