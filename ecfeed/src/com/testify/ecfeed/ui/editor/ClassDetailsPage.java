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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.ui.common.IFileInfoProvider;
import com.testify.ecfeed.ui.modelif.ClassInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class ClassDetailsPage extends BasicDetailsPage {

	private MethodsViewer fMethodsSection;
	private OtherMethodsViewer fOtherMethodsSection;
	private Text fClassNameText;
	private Text fPackageNameText;
	private Text fAndroidRunner;
	private ClassInterface fClassIf;
	private GlobalParametersViewer fGlobalParametersSection;
	private JavaDocCommentsSection fCommentsSection;

	private class BrowseClassesAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			fClassIf.reassignClass();
		}
	}

	private class ClassNameTextAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			fClassIf.setLocalName(fClassNameText.getText());
			fClassNameText.setText(fClassIf.getLocalName());
		}
	}

	private class PackageNameTextAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			fClassIf.setPackageName(fPackageNameText.getText());
			fPackageNameText.setText(fClassIf.getPackageName());
		}
	}
	
	private class AndroidRunnerTextAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			String androidRunner = fAndroidRunner.getText();
			if (androidRunner != null && androidRunner.isEmpty()) {
				androidRunner = null;
			}
			
			fClassIf.setAndroidRunner(androidRunner);
		}
	}

	public ClassDetailsPage(ModelMasterSection masterSection, IModelUpdateContext updateContext, IFileInfoProvider fileInforProvider) {
		super(masterSection, updateContext, fileInforProvider);
		fClassIf = new ClassInterface(this);
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);

		createQualifiedNameComposite(getMainComposite());
		addForm(fCommentsSection = new JavaDocCommentsSection(this, this));
		addViewerSection(fMethodsSection = new MethodsViewer(this, this));
		addViewerSection(fGlobalParametersSection = new GlobalParametersViewer(this, this));
		addViewerSection(fOtherMethodsSection = new OtherMethodsViewer(this, this));

		getToolkit().paintBordersFor(getMainComposite());
	}

	@Override
	protected Composite createTextClientComposite(){
		Composite textClient = super.createTextClientComposite();
		return textClient;
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
		
		createAndroidRunnerText(textComposite);

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
	
	private void createAndroidRunnerText(Composite textComposite) {
		getToolkit().createLabel(textComposite, "Android runner");
		fAndroidRunner = getToolkit().createText(textComposite, null, SWT.NONE);
		fAndroidRunner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fAndroidRunner.addSelectionListener(new AndroidRunnerTextAdapter());
	}	

	private void createBrowseButton(Composite buttonsComposite) {
		Button browseButton = getToolkit().createButton(buttonsComposite, "Browse...", SWT.NONE);
		browseButton.addSelectionListener(new BrowseClassesAdapter());
		browseButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
	}

	@Override
	public void refresh(){
		super.refresh();
		if(getSelectedElement() instanceof ClassNode){
			ClassNode selectedClass = (ClassNode)getSelectedElement();
			fClassIf.setTarget(selectedClass);
			String title = fClassIf.getQualifiedName();
			//Remove implementation status for performance reasons
//			String title = fClassIf.getQualifiedName() + " [" + fClassIf.getImplementationStatus() + "]";
			getMainSection().setText(title);
			fClassNameText.setText(fClassIf.getLocalName());
			fPackageNameText.setText(fClassIf.getPackageName());
			
			String androidRunner = fClassIf.getAndroidRunner();
			if (androidRunner == null) {
				androidRunner = "";
			}
			fAndroidRunner.setText(androidRunner);

			fMethodsSection.setInput(selectedClass);
			fGlobalParametersSection.setInput(selectedClass);
			fOtherMethodsSection.setInput(selectedClass);
			fOtherMethodsSection.setVisible(fOtherMethodsSection.getItemsCount() > 0);
			fCommentsSection.setInput(selectedClass);

			getMainSection().layout();
		}
	}

	@Override
	protected Class<? extends AbstractNode> getNodeType() {
		return ClassNode.class;
	}
}
