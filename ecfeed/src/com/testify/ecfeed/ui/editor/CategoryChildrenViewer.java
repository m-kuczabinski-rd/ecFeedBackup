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

import java.util.ArrayList;

import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.ui.common.PartitionNodeAbstractLayer;
import com.testify.ecfeed.utils.Constants;
import com.testify.ecfeed.utils.ModelUtils;

public class CategoryChildrenViewer extends CheckboxTableViewerSection {

	private final static int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	
	private CategoryNode fSelectedCategory;

	private class AddPartitionAdapter extends SelectionAdapter{
		
		@Override
		public void widgetSelected(SelectionEvent e){
			String newPartitionName = Constants.DEFAULT_NEW_PARTITION_NAME;
			String value = ModelUtils.getDefaultExpectedValueString(fSelectedCategory.getType());
			PartitionNode newPartition = new PartitionNode(newPartitionName, value);
			ModelUtils.setUniqueNodeName(newPartition, fSelectedCategory);
			fSelectedCategory.addPartition(newPartition);
			getTable().setSelection(fSelectedCategory.getOrdinaryPartitions().size() - 1);
			modelUpdated();
		}
	}
	
	private class RemovePartitionsAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			ArrayList<PartitionNode> nodes = new ArrayList<>();
			for(Object partition : getCheckedElements()){
				nodes.add((PartitionNode)partition);
			}			
			if(PartitionNodeAbstractLayer.removePartitions(nodes, fSelectedCategory)){
				modelUpdated();
			}
		}
	}
	public CategoryChildrenViewer(BasicDetailsPage parent, FormToolkit toolkit) {
		super(parent.getMainComposite(), toolkit, STYLE, parent);
		
		getSection().setText("Partitions");
		addButton("Add partition", new AddPartitionAdapter());
		addButton("Remove selected", new RemovePartitionsAdapter());
		
		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
	}

	@Override
	protected void createTableColumns() {
		TableViewerColumn nameColumn = addColumn("Name", 150, new PartitionNameLabelProvider());
		nameColumn.setEditingSupport(new PartitionNameEditingSupport(this));

		TableViewerColumn valueColumn = addColumn("Value", 150, new PartitionValueLabelProvider());
		valueColumn.setEditingSupport(new PartitionValueEditingSupport(this));

	}

	public CategoryNode getSelectedCategory(){
		return fSelectedCategory;
	}

	public void setInput(CategoryNode category){
		fSelectedCategory = category;
		super.setInput(category.getOrdinaryPartitions());
	}
	
	public void setVisible(boolean visible){
		this.getSection().setVisible(visible);
	}
}
