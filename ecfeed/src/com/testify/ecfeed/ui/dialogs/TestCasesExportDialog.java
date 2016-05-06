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
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.core.resources.ResourceHelper;
import com.testify.ecfeed.ui.dialogs.basic.DialogObjectToolkit;
import com.testify.ecfeed.ui.dialogs.basic.ErrorDialog;
import com.testify.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.testify.ecfeed.ui.dialogs.basic.FileOpenAndReadDialog;
import com.testify.ecfeed.ui.dialogs.basic.FileSaveDialog;
import com.testify.ecfeed.ui.dialogs.basic.InfoDialog;
import com.testify.ecfeed.utils.EclipseHelper;

public class TestCasesExportDialog extends TitleAreaDialog {

	private String fTemplate;
	private Text fTemplateText;
	private Text fTargetFileText;
	private String fTargetFile;
	private DialogObjectToolkit fDialogObjectToolkit;
	private FileCompositeVisibility fFileCompositeVisibility;

	public enum FileCompositeVisibility {
		VISIBLE, NOT_VISIBLE
	}

	public TestCasesExportDialog(
			FileCompositeVisibility fileCompositeVisibility,
			String initialTemplate) {
		super(EclipseHelper.getActiveShell());

		fFileCompositeVisibility = fileCompositeVisibility;
		fTemplate = initialTemplate;

		fDialogObjectToolkit = DialogObjectToolkit.getInstance();
	}

	@Override
	public void create() {
		super.create();

		if (fFileCompositeVisibility == FileCompositeVisibility.VISIBLE) {
			setOkEnabled(false);
		}
	}

	@Override
	protected Control createDialogArea(Composite parentComposite) {
		setDialogTitle(this);
		setDialogMessage(this);

		Composite dialogAreaComposite = (Composite) super
				.createDialogArea(parentComposite);
		Composite childComposite = fDialogObjectToolkit.createGridComposite(
				dialogAreaComposite, 1);

		createTemplateTextComposite(childComposite);

		if (fFileCompositeVisibility == FileCompositeVisibility.VISIBLE) {
			createTargetFileComposite(childComposite);
			fTargetFileText.setFocus();
		}

		return dialogAreaComposite;
	}

	public void setDialogTitle(TitleAreaDialog dialog) {
		final String EXPORT_TEST_DATA_TITLE = "Export test data";
		setTitle(EXPORT_TEST_DATA_TITLE);
	}

	public void setDialogMessage(TitleAreaDialog dialog) {
		final String EXPORT_TEST_DATA_MESSAGE = "Define template for data export and select target file";
		setMessage(EXPORT_TEST_DATA_MESSAGE);
	}

	private void setDialogMessageSelectFile() {
		final String SELECT_TARGET = "Select target export file";
		setMessage(SELECT_TARGET);
	}

	private void createTemplateTextComposite(Composite parentComposite) {
		Composite childComposite = fDialogObjectToolkit.createGridComposite(
				parentComposite, 1);

		createTemplateLabelAndButtonsComposite(childComposite);
		fTemplateText = fDialogObjectToolkit.createGridText(childComposite,
				150, fTemplate);
	}

	private void createTemplateLabelAndButtonsComposite(
			Composite parentComposite) {
		Composite gridComposite = fDialogObjectToolkit.createGridComposite(
				parentComposite, 3);

		final String DEFINE_TEMPLATE = "Template for data export";
		fDialogObjectToolkit.createLabel(gridComposite, DEFINE_TEMPLATE);
		fDialogObjectToolkit.createSpacer(gridComposite, 40);
		createButtonsComposite(gridComposite);
	}

	private void createButtonsComposite(Composite parentComposite) {
		Composite buttonComposite = fDialogObjectToolkit
				.createFillComposite(parentComposite);

		fDialogObjectToolkit.createButton(buttonComposite, "Help...",
				new TestButtonSelectionAdapter());
		fDialogObjectToolkit.createButton(buttonComposite, "Load...",
				new OpenButtonSelectionAdapter());
		fDialogObjectToolkit.createButton(buttonComposite, "Save As...",
				new SaveAsButtonSelectionAdapter());
	}

	private String readTemplateFromResource() {
		final String DEFAULT_TEMPLATE_TEXT_FILE = "res/TestCasesExportTemplate.txt";
		String templateText = null;

		try {
			templateText = ResourceHelper.readTextFromResource(this.getClass(),
					DEFAULT_TEMPLATE_TEXT_FILE);
		} catch (Exception e) {
			ExceptionCatchDialog.open("Can not read template", e.getMessage());
		}

		return templateText;
	}

	private void createTargetFileComposite(Composite parent) {
		final String TARGET_FILE = "Target file";
		fTargetFileText = fDialogObjectToolkit.createFileSelectionComposite(
				parent, TARGET_FILE, new FileTextModifyListener());
	}

	@Override
	protected void okPressed() {
		createTemplate();
		if (fFileCompositeVisibility == FileCompositeVisibility.VISIBLE) {
			fTargetFile = fTargetFileText.getText();
		} else {
			fTargetFile = null;
		}

		super.okPressed();
	}

	private void createTemplate() {
		if (fTemplateText != null) {
			fTemplate = fTemplateText.getText();
		} else {
			fTemplate = null;
		}
	}

	@Override
	protected void cancelPressed() {
		super.cancelPressed();
	}

	public String getTemplate() {
		return fTemplate;
	}

	public String getTargetFile() {
		return fTargetFile;
	}

	private void updateStatus() {
		if (fTargetFileText == null || fTargetFileText.getText().isEmpty()) {
			setDialogMessageSelectFile();

			if (fFileCompositeVisibility == FileCompositeVisibility.VISIBLE) {
				setOkEnabled(false);
			}
		} else {
			setMessage(null);
			setOkEnabled(true);
		}
	}

	private void setOkEnabled(boolean enabled) {
		Button okButton = getButton(IDialogConstants.OK_ID);

		if (okButton == null) {
			ErrorDialog.open("Can not find OK button.");
			return;
		}

		okButton.setEnabled(enabled);
	}

	class TestButtonSelectionAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			InfoDialog.open(readTemplateFromResource());
		}
	}

	class OpenButtonSelectionAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			fTemplateText.setText(FileOpenAndReadDialog.open());
		}
	}

	class SaveAsButtonSelectionAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			FileSaveDialog.open(fTemplateText.getText());
		}
	}

	class FileTextModifyListener implements ModifyListener {
		@Override
		public void modifyText(ModifyEvent e) {
			updateStatus();
		}
	}

	class BrowseSelectionAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			FileDialog dialog = new FileDialog(getParentShell());
			fTargetFileText.setText(dialog.open());
		}
	}
}
