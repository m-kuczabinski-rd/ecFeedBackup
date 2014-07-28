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

import com.testify.ecfeed.gal.ModelOperationManager;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.ui.common.PartitionNodeAbstractLayer;
import com.testify.ecfeed.utils.Constants;
import com.testify.ecfeed.utils.ModelUtils;

public class CategoryChildrenViewer extends CheckboxTableViewerSection {

	private final static int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	
	private CategoryNode fSelectedCategory;

	private ModelOperationManager fOperationManager;

	private TableViewerColumn fNameColumn;

	private class AddPartitionAdapter extends SelectionAdapter{
		
		@Override
		public void widgetSelected(SelectionEvent e){
			String newPartitionName = Constants.DEFAULT_NEW_PARTITION_NAME;
			String value = ModelUtils.getDefaultExpectedValueString(fSelectedCategory.getType());
			PartitionNode newPartition = new PartitionNode(newPartitionName, value);
			ModelUtils.setUniqueNodeName(newPartition, fSelectedCategory);
			fSelectedCategory.addPartition(newPartition);
			getTable().setSelection(fSelectedCategory.getPartitions().size() - 1);
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
	
	private class MoveUpAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			moveSelectedItem(true);
		}
	}

	private class MoveDownAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			moveSelectedItem(false);
		}
	}
	
	@Override
	protected void createTableColumns() {
		fNameColumn = addColumn("Name", 150, new PartitionNameLabelProvider());

		TableViewerColumn valueColumn = addColumn("Value", 150, new PartitionValueLabelProvider());
		valueColumn.setEditingSupport(new PartitionValueEditingSupport(this));

	}
	
	public CategoryChildrenViewer(BasicDetailsPage parent, FormToolkit toolkit, ModelOperationManager operationManager) {
		super(parent.getMainComposite(), toolkit, STYLE, parent);
		
		fOperationManager = operationManager;
		fNameColumn.setEditingSupport(new PartitionNameEditingSupport(this, fOperationManager));
		
		getSection().setText("Partitions");
		addButton("Add partition", new AddPartitionAdapter());
		addButton("Remove selected", new RemovePartitionsAdapter());
		addButton("Move Up", new MoveUpAdapter());
		addButton("Move Down", new MoveDownAdapter());
		
		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
	}

	public CategoryNode getSelectedCategory(){
		return fSelectedCategory;
	}

	public void setInput(CategoryNode category){
		fSelectedCategory = category;
		super.setInput(category.getPartitions());
	}
	
	public void setVisible(boolean visible){
		this.getSection().setVisible(visible);
	}
	
	private void moveSelectedItem(boolean moveUp) {
		if (getSelectedElement() != null) {
			PartitionNode partitionNode = (PartitionNode)getSelectedElement();
			int index = fSelectedCategory.getPartitions().indexOf(partitionNode);			
			if(index > -1){
				if(moveUp && index > 0){
					PartitionNode swap = fSelectedCategory.getPartitions().get(index-1);
					fSelectedCategory.getPartitions().set(index-1, fSelectedCategory.getPartitions().get(index));
					fSelectedCategory.getPartitions().set(index, swap);
					modelUpdated();
				} else if(!moveUp && index < fSelectedCategory.getPartitions().size() -1){
					PartitionNode swap = fSelectedCategory.getPartitions().get(index+1);
					fSelectedCategory.getPartitions().set(index+1, fSelectedCategory.getPartitions().get(index));
					fSelectedCategory.getPartitions().set(index, swap);
					modelUpdated();
				}	
			}		
		}
	}
}
