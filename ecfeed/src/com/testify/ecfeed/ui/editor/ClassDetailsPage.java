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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.ui.common.IFileInfoProvider;
import com.testify.ecfeed.ui.modelif.ClassInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class ClassDetailsPage extends BasicDetailsPage {

	private IFileInfoProvider fFileInfoProvider;
	private MethodsViewer fMethodsSection;
	private OtherMethodsViewer fOtherMethodsSection;
	private Text fClassNameText;
	private Text fPackageNameText;
	private Button fRunOnAndroidCheckbox;
	private Label fAndroidRunnerLabel;
	private Text fAndroidRunnerText;
	private ClassInterface fClassIf;
	private GlobalParametersViewer fGlobalParametersSection;
	private JavaDocCommentsSection fCommentsSection;

	private class BrowseClassesAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			fClassIf.reassignClass();
		}
	}

	private class BrowseRunnersAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){

			String projectPath = fFileInfoProvider.getProject().getLocation().toOSString();
			String androidRunner = fClassIf.selectAndroidRunner(projectPath);

			if (androidRunner != null) {
				fAndroidRunnerText.setText(androidRunner);
				fClassIf.setAndroidRunner(androidRunner);
			}
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

	private class RunOnAndroidCheckBoxAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {

			final String defaultAndroidRunner  = "com.testify.ecfeed.android.junit.EcFeedTestRunner";

			boolean selection = fRunOnAndroidCheckbox.getSelection();
			fClassIf.setRunOnAndroid(selection);

			if (selection) {
				String androidRunner = fClassIf.getAndroidRunner();

				if (androidRunner == null || androidRunner.isEmpty()) {
					fClassIf.setAndroidRunner(defaultAndroidRunner);
				}
			}

			refresh();
		}
	}

	private class AndroidRunnerTextAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			String androidRunner = fAndroidRunnerText.getText();

			if (androidRunner != null && androidRunner.isEmpty()) {
				androidRunner = null;
			}

			fClassIf.setAndroidRunner(androidRunner);
		}
	}

	public ClassDetailsPage(ModelMasterSection masterSection, IModelUpdateContext updateContext, IFileInfoProvider fileInfoProvider) {
		super(masterSection, updateContext, fileInfoProvider);
		fFileInfoProvider = fileInfoProvider;
		fClassIf = new ClassInterface(this, fFileInfoProvider);
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);

		createBasicParametersComposite(getMainComposite());
		addForm(fCommentsSection = new JavaDocCommentsSection(this, this, fFileInfoProvider));
		addViewerSection(fMethodsSection = new MethodsViewer(this, this, fFileInfoProvider));
		addViewerSection(fGlobalParametersSection = new GlobalParametersViewer(this, this, fFileInfoProvider));
		addViewerSection(fOtherMethodsSection = new OtherMethodsViewer(this, this, fFileInfoProvider));

		getToolkit().paintBordersFor(getMainComposite());
	}

	@Override
	protected Composite createTextClientComposite(){
		Composite textClient = super.createTextClientComposite();
		return textClient;
	}

	private void createBasicParametersComposite(Composite parent) {

		Composite mainComposite = getToolkit().createComposite(parent);
		mainComposite.setLayout(new GridLayout(2, false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Composite textComposite = getToolkit().createComposite(mainComposite);
		createControlsOfTextComposite(textComposite);

		Composite buttonsComposite = getToolkit().createComposite(mainComposite);
		createControlsOfButtonsComposite(buttonsComposite);
	}

	private void createControlsOfTextComposite(Composite textComposite) {

		textComposite.setLayout(new GridLayout(2, false));
		textComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		createPackageNameText(textComposite);
		createClassNameText(textComposite);
		createRunOnAndroidCheckBox(textComposite);
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

	private void createRunOnAndroidCheckBox(Composite composite) {
		GridData checkboxGridData = new GridData(SWT.FILL,  SWT.CENTER, true, false);
		checkboxGridData.horizontalSpan = 2;

		fRunOnAndroidCheckbox = getToolkit().createButton(composite, "Run on Android", SWT.CHECK);
		fRunOnAndroidCheckbox.setLayoutData(checkboxGridData);
		fRunOnAndroidCheckbox.addSelectionListener(new RunOnAndroidCheckBoxAdapter());
	}

	private void createAndroidRunnerText(Composite textComposite) {
		fAndroidRunnerLabel = getToolkit().createLabel(textComposite, "Android runner");
		fAndroidRunnerText = getToolkit().createText(textComposite, null, SWT.NONE);
		fAndroidRunnerText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fAndroidRunnerText.addSelectionListener(new AndroidRunnerTextAdapter());
	}	

	private void createControlsOfButtonsComposite(Composite buttonsComposite) {

		buttonsComposite.setLayout(new GridLayout(1, false));
		GridData gridData = new GridData(SWT.FILL, SWT.TOP, false, false);
		buttonsComposite.setLayoutData(gridData);

		createEmptyLabel(buttonsComposite);
		createClassBrowseButton(buttonsComposite);
		createEmptyLabel(buttonsComposite);
		createRunnerBrowseButton(buttonsComposite);
	}

	private void createEmptyLabel(Composite buttonsComposite) {
		getToolkit().createLabel(buttonsComposite, "");
	}	

	private void createClassBrowseButton(Composite buttonsComposite) {
		Button browseButton = getToolkit().createButton(buttonsComposite, "Browse...", SWT.NONE);
		browseButton.addSelectionListener(new BrowseClassesAdapter());
		browseButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
	}

	private void createRunnerBrowseButton(Composite buttonsComposite) {
		Button browseButton = getToolkit().createButton(buttonsComposite, "Browse..", SWT.NONE);
		browseButton.addSelectionListener(new BrowseRunnersAdapter());
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

			boolean runOnAndroid = fClassIf.getRunOnAndroid(); 
			fRunOnAndroidCheckbox.setSelection(runOnAndroid);

			fAndroidRunnerLabel.setEnabled(runOnAndroid);
			fAndroidRunnerText.setEnabled(runOnAndroid);

			String androidRunner = fClassIf.getAndroidRunner();
			if (androidRunner == null) {
				androidRunner = "";
			}
			fAndroidRunnerText.setText(androidRunner);

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
