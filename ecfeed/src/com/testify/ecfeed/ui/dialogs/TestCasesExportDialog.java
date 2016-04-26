/*******************************************************************************
 * Copyright (c) 2016 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.core.resources.ResourceHelper;
import com.testify.ecfeed.core.serialization.export.TestCasesExportParser;
import com.testify.ecfeed.ui.common.CompositeFactory;
import com.testify.ecfeed.ui.dialogs.basic.ErrorDialog;
import com.testify.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.testify.ecfeed.ui.dialogs.basic.InfoDialog;

public class TestCasesExportDialog extends TitleAreaDialog {

	private Text fTemplateText;
	private TestCasesExportParser fExportParser;
	private Text fTargetFileText;
	private String fTargetFile;
	private CompositeFactory fCompositeFactory;

	public TestCasesExportDialog(Shell parentShell, int methodParametersCount) {
		super(parentShell);
		fExportParser = new TestCasesExportParser(methodParametersCount);
		fCompositeFactory = CompositeFactory.getInstance();
	}

	@Override
	public void create() {
		super.create();
		setOkEnabled(false);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setDialogTitle(this);
		setDialogMessage(this);
		
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = fCompositeFactory.createGridContainer(area, 1);
		
		createTemplateTextWidgets(container);
		createTargetFileWidgets(container);
		
		fTargetFileText.setFocus();
		
		return area;
	}

	public void setDialogTitle(TitleAreaDialog dialog) {
		final String EXPORT_TEST_DATA_TITLE = "Export test data";
		setTitle(EXPORT_TEST_DATA_TITLE);
	}

	public void setDialogMessage(TitleAreaDialog dialog)	{
		final String EXPORT_TEST_DATA_MESSAGE = "Define template for data export and select target file";
		setMessage(EXPORT_TEST_DATA_MESSAGE);
	}

	private void setDialogMessageSelectFile() {
		final String SELECT_TARGET = "Select target export file";
		setMessage(SELECT_TARGET);
	}

	private void createTemplateTextWidgets(Composite parent) {
		Composite container = fCompositeFactory.createGridContainer(parent, 1);
		
		createLabelWithHelpButton(container);

		String initialText = fExportParser.createInitialText();
		fTemplateText = fCompositeFactory.createText(container, 150, initialText);		
	}

	private void createLabelWithHelpButton(Composite parent) {
		Composite container = fCompositeFactory.createGridContainer(parent, 2);
		
		final String DEFINE_TEMPLATE = "Template for data export   ";
		fCompositeFactory.createLabel(container, DEFINE_TEMPLATE);		
		fCompositeFactory.createButton(container, "Help", new TestButtonSelectionAdapter());
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

	private void createTargetFileWidgets(Composite parent) {
		final String TARGET_FILE = "Target file";
		fCompositeFactory.createLabel(parent, TARGET_FILE);		

		Composite targetFileContainer = fCompositeFactory.createGridContainer(parent, 2);
		fTargetFileText = 
				fCompositeFactory.createFileSelectionText(targetFileContainer, new FileTextModifyListener());

		fCompositeFactory.createBrowseButton(targetFileContainer, new BrowseSelectionAdapter());
	}

	@Override
	protected void okPressed(){
		String template = null;

		if (fTemplateText != null) {
			template = fTemplateText.getText();
		}

		fExportParser.createSubTemplates(template);
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

	private void updateStatus() {
		if (fTargetFileText.getText().isEmpty()) {
			setDialogMessageSelectFile();
			setOkEnabled(false);
		} else {
			setMessage(null);
			setOkEnabled(true);
		}
	}

	private void setOkEnabled(boolean enabled) {
		Button okButton = getButton(IDialogConstants.OK_ID);

		if (okButton == null) {
			ErrorDialog.display("Can not find OK button.");
			return;
		}

		okButton.setEnabled(enabled);
	}
	
	class TestButtonSelectionAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			InfoDialog.open(readTemplateFromResource());
		}
	}	

	class FileTextModifyListener implements ModifyListener {
		@Override
		public void modifyText(ModifyEvent e) {
			updateStatus();
		}
	}

	class BrowseSelectionAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			FileDialog dialog = new FileDialog(getParentShell());
			fTargetFileText.setText(dialog.open());
		}
	}
}
