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

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.android.AndroidRunnerHelper;
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
	private Combo fAndroidRunnerCombo;	
	private ClassInterface fClassIf;
	private GlobalParametersViewer fGlobalParametersSection;
	private JavaDocCommentsSection fCommentsSection;

	private class BrowseClassesSelectionListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			fClassIf.reassignClass();
		}
	}

	private class AndroidRunnerComboSelectionListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){

			fClassIf.setBaseAndroidRunner(fAndroidRunnerCombo.getText());

			String androidRunner = fClassIf.getAndroidRunner();
			fAndroidRunnerCombo.setText(androidRunner);
		}
	}	

	private class AndroidRunnerComboFocusListener implements FocusListener{

		@Override
		public void focusGained(FocusEvent e) {
			refreshAndroidRunnerCombo();
		}

		@Override
		public void focusLost(FocusEvent e) {
		}
	}	

	private class ClassNameSelectionListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			fClassIf.setLocalName(fClassNameText.getText());
			fClassNameText.setText(fClassIf.getLocalName());
		}
	}

	private class PackageNameSelectionListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			fClassIf.setPackageName(fPackageNameText.getText());
			fPackageNameText.setText(fClassIf.getPackageName());
		}
	}

	private class RunOnAndroidCheckBoxAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {

			final String defaultBaseAndroidRunner  = AndroidRunnerHelper.getDefaultBaseAndroidRunnerName();

			boolean selection = fRunOnAndroidCheckbox.getSelection();
			fClassIf.setRunOnAndroid(selection);

			if (selection) {
				String androidRunner = fClassIf.getAndroidRunner();

				if (androidRunner == null || androidRunner.isEmpty()) {
					fClassIf.setBaseAndroidRunner(defaultBaseAndroidRunner);
				}
			}

			refresh();
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
		mainComposite.setLayout(new GridLayout(1, false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Composite packageAndClassComposite = getToolkit().createComposite(mainComposite);
		initAndFillClassComposite(packageAndClassComposite);

		Composite androidComposite = getToolkit().createComposite(mainComposite);
		initAndFillAndroidComposite(androidComposite);
	}

	private void initAndFillClassComposite(Composite composite) {

		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// row 1

		// col 1 label 
		getToolkit().createLabel(composite, "Package name");

		// col 2 packageName
		fPackageNameText = getToolkit().createText(composite, null, SWT.NONE);
		fPackageNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fPackageNameText.addSelectionListener(new PackageNameSelectionListener());

		// col 3 empty
		getToolkit().createLabel(composite, ""); // TODO


		// row 2

		// col 1 label
		getToolkit().createLabel(composite, "Class name");

		// col 2 className
		fClassNameText = getToolkit().createText(composite, null, SWT.NONE);
		fClassNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fClassNameText.addSelectionListener(new ClassNameSelectionListener());

		// col 3 browse button
		Button browseButton = getToolkit().createButton(composite, "Browse...", SWT.NONE);
		browseButton.addSelectionListener(new BrowseClassesSelectionListener());
		browseButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));		

		getToolkit().paintBordersFor(composite);
	}

	private void initAndFillAndroidComposite(Composite composite) {

		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		// row 1

		// col 1 and 2
		fRunOnAndroidCheckbox = getToolkit().createButton(composite, "Run on Android", SWT.CHECK);

		GridData checkboxGridData = new GridData(SWT.FILL,  SWT.CENTER, true, false);
		checkboxGridData.horizontalSpan = 2;
		fRunOnAndroidCheckbox.setLayoutData(checkboxGridData);
		fRunOnAndroidCheckbox.addSelectionListener(new RunOnAndroidCheckBoxAdapter());


		// row 2

		// col 1 - label
		fAndroidRunnerLabel = getToolkit().createLabel(composite, "Base runner");

		// col 2 - runner combo
		fAndroidRunnerCombo = new ComboViewer(composite, SWT.NONE).getCombo();
		fAndroidRunnerCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fAndroidRunnerCombo.addFocusListener(new AndroidRunnerComboFocusListener());
		fAndroidRunnerCombo.addSelectionListener(new AndroidRunnerComboSelectionListener());

		fillAndroidRunnerCombo();
	}

	private void refreshAndroidRunnerCombo() {

		String currentRunner = fAndroidRunnerCombo.getText();

		fAndroidRunnerCombo.removeAll();
		fillAndroidRunnerCombo();

		fAndroidRunnerCombo.setText(currentRunner);
	}

	private void fillAndroidRunnerCombo() {
		// TODO - different algorithm for base runners - not actual runners
	}

	//	private List<String> createRunnerList() {
	//		String projectPath = ProjectHelper.getProjectPath(fFileInfoProvider);
	//		return fClassIf.createRunnerList(projectPath);
	//	}

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
			fAndroidRunnerCombo.setEnabled(runOnAndroid);

			String androidRunner = fClassIf.getAndroidRunner();

			if (androidRunner == null) {
				androidRunner = "";
			}

			fAndroidRunnerCombo.setText(androidRunner);

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
