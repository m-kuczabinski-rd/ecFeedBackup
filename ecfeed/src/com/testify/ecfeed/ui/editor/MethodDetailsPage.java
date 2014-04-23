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

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.testify.ecfeed.model.AbstractCategoryNode;
import com.testify.ecfeed.model.ExpectedCategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionedCategoryNode;
import com.testify.ecfeed.ui.dialogs.TestMethodRenameDialog;

public class MethodDetailsPage extends BasicDetailsPage {

	private MethodNode fSelectedMethod;
	private ParametersViewer fParemetersSection;
	private ConstraintsListViewer fConstraintsSection;
	private TestCasesViewer fTestCasesSection;
	
	private class ReassignAdapter extends SelectionAdapter{

		@Override
		public void widgetSelected(SelectionEvent e){
			TestMethodRenameDialog dialog = new TestMethodRenameDialog(getActiveShell(), fSelectedMethod);
			if(dialog.open() == IDialogConstants.OK_ID){
				MethodNode selectedMethod = dialog.getSelectedMethod();
				fSelectedMethod.setName(selectedMethod.getName());
				updateParemeters(selectedMethod);
				modelUpdated(null);
			}
		}

		private void updateParemeters(MethodNode newMethod) {
			List<AbstractCategoryNode> srcParameters = newMethod.getCategories();
			for(int i = 0; i < srcParameters.size(); i++){
				updateParameter(i, srcParameters.get(i));
			}
		}
		
		private void updateParameter(int index, AbstractCategoryNode newCategory){
			boolean isOriginalCategoryExpected = fSelectedMethod.getCategories().get(index) 
					instanceof ExpectedCategoryNode;
			boolean isNewCategoryExpected = newCategory instanceof ExpectedCategoryNode;
			if(isOriginalCategoryExpected == isNewCategoryExpected){
				fSelectedMethod.getCategories().get(index).setName(newCategory.getName());
			}
			else{
				if(newCategory instanceof ExpectedCategoryNode)
					fSelectedMethod.replaceCategory(index, (ExpectedCategoryNode)newCategory);
				else if(newCategory instanceof PartitionedCategoryNode)
					fSelectedMethod.replaceCategory(index, (PartitionedCategoryNode)newCategory);					
			}
		}
	}
	
	public MethodDetailsPage(ModelMasterSection masterSection) {
		super(masterSection);
	}

	public MethodNode getSelectedMethod() {
		return fSelectedMethod;
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);

		createTextClient();
		addForm(fParemetersSection = new ParametersViewer(this, getToolkit()));
		addForm(fConstraintsSection = new ConstraintsListViewer(this, getToolkit()));
		addForm(fTestCasesSection = new TestCasesViewer(this, getToolkit()));
		
		getToolkit().paintBordersFor(getMainComposite());
	}

	private void createTextClient() {
		Composite buttonsComposite = getToolkit().createComposite(getMainSection());
		RowLayout rl = new RowLayout();
		rl.fill = true;
		buttonsComposite.setLayout(rl);
		getMainSection().setTextClient(buttonsComposite);
		Button reassignButton = getToolkit().createButton(buttonsComposite, "Reassign", SWT.NONE);
		reassignButton.addSelectionListener(new ReassignAdapter());
		Button testOnlineButton = getToolkit().createButton(buttonsComposite, "Test online", SWT.NONE);
		testOnlineButton.addSelectionListener(new ExecuteOnlineTestAdapter(this));
	}

	@Override
	public void refresh(){
		if(getSelectedElement() instanceof MethodNode){
			fSelectedMethod = (MethodNode)getSelectedElement();
		}
		if(fSelectedMethod != null){
			getMainSection().setText(fSelectedMethod.toString());
			fParemetersSection.setInput(fSelectedMethod);
			fConstraintsSection.setInput(fSelectedMethod);
			fTestCasesSection.setInput(fSelectedMethod);
		}
	}
}
