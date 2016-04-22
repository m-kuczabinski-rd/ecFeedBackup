/*******************************************************************************
 * Copyright (c) 2016 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.core.resources.ResourceHelper;
import com.testify.ecfeed.core.serialization.export.TestCasesExportParser;
import com.testify.ecfeed.ui.common.CompositeFactory;
import com.testify.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;

public class TestCasesExportDialog extends TitleAreaDialog {

	private int fMethodParametersCount;
	private Text fTemplateText;
	private TestCasesExportParser fExportParser;
	private Text fTargetFileText;
	private String fTargetFile;
	private CompositeFactory fCompositeFactory;

	public TestCasesExportDialog(Shell parentShell, int methodParametersCount) {
		super(parentShell);
		fMethodParametersCount = methodParametersCount;
		fExportParser = new TestCasesExportParser();
		fCompositeFactory = CompositeFactory.getInstance();
	}

	public boolean isAdvancedMode() {
		return false;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setDialogTitle(this);
		setDialogMessage(this);

		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = fCompositeFactory.createGridContainer(area, 1);

		if (isAdvancedMode()) {
			createTemplateDefinitionContainer(container);
		}

		createTargetFileContainer(container);
		return area;
	}

	public void setDialogTitle(TitleAreaDialog dialog) {
		final String EXPORT_TEST_DATA_TITLE = "Export test data";
		setTitle(EXPORT_TEST_DATA_TITLE);
	}

	public void setDialogMessage(TitleAreaDialog dialog)	{
		if (isAdvancedMode()) {
			final String EXPORT_TEST_DATA_MESSAGE = "Define template for data export and select target file";
			setMessage(EXPORT_TEST_DATA_MESSAGE);
		} else {
			final String SELECT_TARGET = "Select target export file";
			setMessage(SELECT_TARGET);
		}
	}

	private void createTemplateDefinitionContainer(Composite parent) {
		Composite container = fCompositeFactory.createGridContainer(parent, 1);
		createTemplateComposite(container);
	}

	private void createTemplateComposite(Composite parent) {
		final String DEFINE_TEMPLATE = "Define template for export data.";
		fCompositeFactory.createLabel(parent, DEFINE_TEMPLATE);		

		fTemplateText = fCompositeFactory.createText(parent, 300, readTemplateFromResource());		
	}

	private String readTemplateFromResource() {
		final String DEFAULT_TEMPLATE_TEXT_FILE = "res/TestCasesExportTemplate.txt";
		String templateText = null;

		try {
			templateText = ResourceHelper.readTextFromResource(this.getClass(), DEFAULT_TEMPLATE_TEXT_FILE);
		} catch (Exception e) {
			ExceptionCatchDialog.display("Can not read template", e.getMessage());
		}

		return templateText;
	}

	private void createTargetFileContainer(Composite parent) {
		final String TARGET_FILE = "Target file";
		fCompositeFactory.createLabel(parent, TARGET_FILE);		

		Composite targetFileContainer = fCompositeFactory.createGridContainer(parent, 2);
		fTargetFileText = fCompositeFactory.createFileSelectionText(targetFileContainer);
		fCompositeFactory.createBrowseButton(targetFileContainer, new BrowseSelectionAdapter()); 
	}

	@Override
	protected void okPressed(){
		String template = null;

		if (fTemplateText != null) {
			template = fTemplateText.getText();
		}

		fExportParser.createSubTemplates(isAdvancedMode(), template, fMethodParametersCount);
		fTargetFile = fTargetFileText.getText();

		super.okPressed();
	}

	public String getHeaderTemplate(){
		return fExportParser.getHeaderTemplate();
	}

	public String getTestCaseTemplate(){
		return fExportParser.getTestCaseTemplate();
	}

	public String getFooterTemplate(){
		return fExportParser.getFooterTemplate();
	}

	public String getTargetFile(){
		return fTargetFile;
	}

	class BrowseSelectionAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			FileDialog dialog = new FileDialog(getParentShell());
			fTargetFileText.setText(dialog.open());
		}
	}
}
