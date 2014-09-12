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

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.ui.dialogs.TestClassSelectionDialog;
import com.testify.ecfeed.ui.modelif.ClassInterface;

public class ClassDetailsPage extends BasicDetailsPage {

	private MethodsViewer fMethodsSection;
	private OtherMethodsViewer fOtherMethodsSection;
	private Text fClassNameText;
	private Text fPackageNameText;
	private ClassInterface fClassIf;
	
	private class BrowseClassesAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			IType selectedClass = selectClass();

			if(selectedClass != null){
				String qualifiedName = selectedClass.getFullyQualifiedName();
				fClassIf.setQualifiedName(qualifiedName, ClassDetailsPage.this);
			}
		}
		
		private IType selectClass() {
			TestClassSelectionDialog dialog = new TestClassSelectionDialog(Display.getDefault().getActiveShell());
			
			if (dialog.open() == IDialogConstants.OK_ID) {
				return (IType)dialog.getFirstResult();
			}
			return null;
		}
	}
	
	private class ClassNameTextAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			fClassIf.setLocalName(fClassNameText.getText(), ClassDetailsPage.this);
			fClassNameText.setText(fClassIf.getLocalName());
		}
	}
	
	private class PackageNameTextAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			fClassIf.setPackageName(fPackageNameText.getText(), ClassDetailsPage.this);
			fPackageNameText.setText(fClassIf.getPackageName());
		}
	}
	
	public ClassDetailsPage(ModelMasterDetailsBlock masterDetailsBlock) {
		super(masterDetailsBlock);
		fClassIf = new ClassInterface();
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);

		createQualifiedNameComposite(getMainComposite());
		addForm(fMethodsSection = new MethodsViewer(this, getToolkit()));
		addForm(fOtherMethodsSection = new OtherMethodsViewer(this, getToolkit()));
		
		getToolkit().paintBordersFor(getMainComposite());
	}

	
	
	private void createQualifiedNameComposite(Composite parent) {
		Composite composite = getToolkit().createComposite(parent);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		Composite textComposite = getToolkit().createComposite(composite);
		
		textComposite.setLayout(new GridLayout(2, false));
		textComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		Composite buttonsComposite = getToolkit().createComposite(composite);
		buttonsComposite.setLayout(new GridLayout(1, false));
		buttonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

		createPackageNameText(textComposite);
		createClassNameText(textComposite);

		createBrowseButton(buttonsComposite);

		getToolkit().paintBordersFor(textComposite);
	}

	private void createPackageNameText(Composite textComposite) {
		getToolkit().createLabel(textComposite, "Package name");
		fPackageNameText = getToolkit().createText(textComposite, null, SWT.NONE);
		fPackageNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fPackageNameText.addSelectionListener(new PackageNameTextAdapter());
	}

	private void createClassNameText(Composite textComposite) {
		getToolkit().createLabel(textComposite, "Class name");
		fClassNameText = getToolkit().createText(textComposite, null, SWT.NONE);
		fClassNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fClassNameText.addSelectionListener(new ClassNameTextAdapter());
	}

	private void createBrowseButton(Composite buttonsComposite) {
		Button browseButton = getToolkit().createButton(buttonsComposite, "Browse...", SWT.NONE);
		browseButton.addSelectionListener(new BrowseClassesAdapter());
		browseButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
	}

	@Override
	public void refresh(){
		if(getSelectedElement() instanceof ClassNode){
			fClassIf.setTarget((ClassNode)getSelectedElement());
			String title = fClassIf.getQualifiedName() + " [" + fClassIf.implementationStatus() + "]";
			getMainSection().setText(title);
			fClassNameText.setText(fClassIf.getLocalName());
			fPackageNameText.setText(fClassIf.getPackageName());

			fMethodsSection.setInput(fClassIf.getTarget());
			fOtherMethodsSection.setInput(fClassIf.getTarget());
			fOtherMethodsSection.setVisible(fOtherMethodsSection.getItemsCount() > 0);

			getMainSection().layout();
		}
	}
}
