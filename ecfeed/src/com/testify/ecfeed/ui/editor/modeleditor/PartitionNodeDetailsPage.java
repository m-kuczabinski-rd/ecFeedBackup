/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor.modeleditor;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.constants.DialogStrings;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.utils.EcModelUtils;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;

public class PartitionNodeDetailsPage extends GenericNodeDetailsPage{

	private PartitionNode fSelectedPartition;
	private Section fMainSection;
	private Text fPartitionNameText;
	private Text fPartitionValueText;
	
	/**
	 * Create the details page.
	 */
	public PartitionNodeDetailsPage(ModelMasterDetailsBlock parentBlock) {
		super(parentBlock);
	}

	/**
	 * Create contents of the details page.
	 * @param parent
	 */
	public void createContents(Composite parent) {
		parent.setLayout(new FillLayout());
		fMainSection = fToolkit.createSection(parent,
				ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR);

		Composite mainComposite = fToolkit.createComposite(fMainSection, SWT.NONE);
		mainComposite.setLayout(new GridLayout(3, false));
		fToolkit.paintBordersFor(mainComposite);
		fMainSection.setClient(mainComposite);

		fToolkit.createLabel(mainComposite, "Partition name");
		fPartitionNameText = fToolkit.createText(mainComposite, null);
		fPartitionNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fPartitionNameText.addListener(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
					renamePartition(fPartitionNameText.getText());
				}
			}
		});
		createButton(mainComposite, "Change", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				renamePartition(fPartitionNameText.getText());
			}
		});
		
		fToolkit.createLabel(mainComposite, "Partition value");
		fPartitionValueText = fToolkit.createText(mainComposite, null);
		fPartitionValueText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fPartitionValueText.addListener(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
					changePartitionValue(fPartitionValueText.getText());
				}
			}
		});
		createButton(mainComposite, "Change", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				changePartitionValue(fPartitionValueText.getText());
			}
		});
	}

	private void renamePartition(String name) {
		CategoryNode parent = (CategoryNode)fSelectedPartition.getParent();
		if(EcModelUtils.validatePartitionName(name, parent, fSelectedPartition)){
			fSelectedPartition.setName(name);
			updateModel(fSelectedPartition);
		}
		else{
			MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), 
					DialogStrings.DIALOG_PARTITION_NAME_PROBLEM_TITLE, 
					Display.getDefault().getSystemImage(SWT.ICON_ERROR), 
					DialogStrings.DIALOG_PARTITION_NAME_PROBLEM_MESSAGE,
					MessageDialog.ERROR, new String[] {"OK"}, 0);
			dialog.open();
			fPartitionNameText.setText(fSelectedPartition.getName());
		}
	}

	private void changePartitionValue(String valueString) {
		CategoryNode parent = (CategoryNode)fSelectedPartition.getParent();
		if(EcModelUtils.validatePartitionStringValue(valueString, parent)){
			fSelectedPartition.setValue(EcModelUtils.getPartitionValueFromString(valueString, parent.getType()));
			updateModel(fSelectedPartition);
		}
		else{
			MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), 
					DialogStrings.DIALOG_PARTITION_VALUE_PROBLEM_TITLE, 
					Display.getDefault().getSystemImage(SWT.ICON_ERROR), 
					DialogStrings.DIALOG_PARTITION_VALUE_PROBLEM_MESSAGE,
					MessageDialog.ERROR, new String[] {"OK"}, 0);
			dialog.open();
			fPartitionValueText.setText(fSelectedPartition.getValueString());
		}
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		super.selectionChanged(part, selection);
		fSelectedPartition = (PartitionNode)fSelectedNode;
		refresh();
	}
	
	public void refresh() {
		if(fSelectedPartition == null){
			return;
		}
		fMainSection.setText(fSelectedPartition.toString());
		fPartitionNameText.setText(fSelectedPartition.getName());
		fPartitionValueText.setText(fSelectedPartition.getValueString());
	}

}
