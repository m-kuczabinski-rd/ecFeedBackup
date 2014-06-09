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

import java.util.Arrays;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.Constants;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.SimpleControlMenuListener;
import com.testify.ecfeed.utils.AdaptTypeSupport;
import com.testify.ecfeed.utils.ModelUtils;

public class CategoryDetailsPage extends BasicDetailsPage {

	private Text fDefaultValueText;
	private Text fNameText;
	private Combo fTypeText;
	private Button fExpectedCheckbox;
	private CategoryNode fSelectedCategory;
	private CategoryChildrenViewer fPartitionsViewer;
	private SimpleControlMenuListener	fBoolExpectedValueMenu;
	
	public CategoryDetailsPage(ModelMasterSection masterSection) {
		super(masterSection);
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);
		
		createCommonParametersEdit();
		createDefaultValueEdit();
		addForm(fPartitionsViewer = new CategoryChildrenViewer(this, getToolkit()));

		getToolkit().paintBordersFor(getMainComposite());
	}
	
	@Override
	public void refresh(){
		if(getSelectedElement() instanceof CategoryNode){
			fSelectedCategory = (CategoryNode)getSelectedElement();
			getMainSection().setText(fSelectedCategory.toString());
			fPartitionsViewer.setInput(fSelectedCategory);
			
			fNameText.setText(fSelectedCategory.getName());
			fNameText.setEnabled(true);
			fTypeText.setEnabled(true);
			fTypeText.setItems(AdaptTypeSupport.getSupportedTypes());
			fTypeText.setText(fSelectedCategory.getType());
			
			fExpectedCheckbox.setEnabled(true);
			fExpectedCheckbox.setSelection(fSelectedCategory.isExpected());
			
			if(fSelectedCategory.isExpected()){
				fDefaultValueText.setText(fSelectedCategory.getDefaultValueString());
				fDefaultValueText.setEnabled(true);
				fPartitionsViewer.setVisible(false);
				if(fSelectedCategory.getType().equals(Constants.TYPE_NAME_BOOLEAN)){
					fBoolExpectedValueMenu.setEnabled(true);
					fDefaultValueText.setEditable(false);
				} else{
					fBoolExpectedValueMenu.setEnabled(false);
					fDefaultValueText.setEditable(true);
				}
			} else{
				fDefaultValueText.setText("");
				fDefaultValueText.setEnabled(false);
				fPartitionsViewer.setVisible(true);
			}

		} else{
			fExpectedCheckbox.setEnabled(false);
			fDefaultValueText.setText("");
			fDefaultValueText.setEnabled(false);
			fNameText.setText("");
			fNameText.setEnabled(false);
			fTypeText.setText("");
			fTypeText.setEnabled(false);
			fPartitionsViewer.setVisible(false);
		}
	}
	
	public void createCommonParametersEdit(){
		Composite composite = getToolkit().createComposite(getMainComposite());
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		getToolkit().createLabel(composite, "Category name: ", SWT.NONE);
		fNameText = getToolkit().createText(composite, "",SWT.NONE);
		fNameText.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, true, false));
		fNameText.addListener(SWT.KeyDown, new Listener(){
			@Override
			public void handleEvent(Event event){
				if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
					if(applyNewCategoryName(fSelectedCategory, fNameText)){
						modelUpdated(null);
					}
				}
			}
		});
		getToolkit().createLabel(composite, "Category type: ", SWT.NONE);
		fTypeText = new Combo(composite,SWT.DROP_DOWN);
		fTypeText.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, false, false));
		fTypeText.addListener(SWT.KeyDown, new Listener(){
			@Override
			public void handleEvent(Event event){
				if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
					boolean validName = true;
					if(!Arrays.asList(fTypeText.getItems()).contains(fTypeText.getText())){
						validName = ModelUtils.isClassQualifiedNameValid(fTypeText.getText());
					}
					if(validName){
						if(applyNewCategoryType(fSelectedCategory, fTypeText)){
							modelUpdated(null);
						}
					} else{
						MessageDialog.openError(Display.getCurrent().getActiveShell(),
								Messages.DIALOG_PARAMETER_TYPE_PROBLEM_TITLE,
								Messages.DIALOG_PARAMETER_TYPE_PROBLEM_MESSAGE);
					}
				}
			}
		});
		fTypeText.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				if(applyNewCategoryType(fSelectedCategory, fTypeText)){
					modelUpdated(null);
				}
			}
		});
		getToolkit().paintBordersFor(composite);
	}
	
	public void createDefaultValueEdit(){
		Composite composite = getToolkit().createComposite(getMainComposite());
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		getToolkit().createLabel(composite, "Default value: ", SWT.NONE);
		fDefaultValueText = getToolkit().createText(composite, "",SWT.NONE);
		fDefaultValueText.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, true, false));
		fDefaultValueText.addListener(SWT.KeyDown, new Listener(){
			@Override
			public void handleEvent(Event event){
				if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
					if(applyNewDefaultValue(fSelectedCategory, fDefaultValueText)){
						modelUpdated(null);
					}
				}
			}
		});
		
		fBoolExpectedValueMenu = new SimpleControlMenuListener(composite, fDefaultValueText){
			@Override
			protected void menuItemSelected(SelectionEvent e){
				MenuItem item = (MenuItem)e.getSource();
				fDefaultValueText.setText(item.getText());
				if(applyNewDefaultValue(fSelectedCategory, fDefaultValueText)){
					modelUpdated(null);
				}
			}
		};
		fBoolExpectedValueMenu.addData("true");
		fBoolExpectedValueMenu.addData("false");
		fBoolExpectedValueMenu.createMenu();
		fDefaultValueText.addListener(SWT.MouseDown, fBoolExpectedValueMenu);
		
		fExpectedCheckbox = getToolkit().createButton(composite, "Expected", SWT.CHECK);
		fExpectedCheckbox.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, false, false));
		fExpectedCheckbox.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				fSelectedCategory.getMethod().changeCategoryExpectedStatus(fSelectedCategory, fExpectedCheckbox.getSelection());
				modelUpdated(null);
			}
		});
		
		getToolkit().paintBordersFor(composite);
	}
	
	protected boolean applyNewDefaultValue(CategoryNode category, Text valueText) {
		String newValue = valueText.getText();
		if(newValue.equals(fSelectedCategory.getDefaultValueString())) return false;
		if(ModelUtils.validatePartitionStringValue(newValue, category.getType())){
			category.setDefaultValueString(newValue);
			return true;
		}
		valueText.setText(category.getDefaultValuePartition().getValueString());
		return false;
	}
	
	protected boolean applyNewCategoryName(CategoryNode category, Text valueText) {
		String newValue = valueText.getText();
		if(newValue.equals(fSelectedCategory.getName()) || fSelectedCategory.getSibling(newValue) != null){
			return false;
		}
		if(ModelUtils.validateNodeName(newValue)){
			category.setName(newValue);
			return true;
		}
		valueText.setText(category.getName());
		return false;
	}
	
	protected boolean applyNewCategoryType(CategoryNode category, Combo valueText) {
		String newValue = valueText.getText();
		return AdaptTypeSupport.changeCategoryType(category, newValue);
	}
}
