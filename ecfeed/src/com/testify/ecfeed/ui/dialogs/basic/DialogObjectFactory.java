/*******************************************************************************
 * Copyright (c) 2016 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.dialogs.basic;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.core.utils.StringHelper;

public class DialogObjectFactory {
	private static DialogObjectFactory fInstance = null;

	protected DialogObjectFactory() {
	}

	public static DialogObjectFactory getInstance() {
		if (fInstance == null) {
			fInstance = new DialogObjectFactory();
		}
		return fInstance;
	}

	public Composite createGridContainer(Composite parent, int countOfColumns) {

		Composite container = new Composite(parent, SWT.NONE);

		container.setLayout(new GridLayout(countOfColumns, false));
		GridData gridData = new GridData(GridData.FILL_BOTH);
		container.setLayoutData(gridData);

		return container;
	}

	public Text createGridText(Composite parentGridComposite, int heightHint, String initialText) {
		Text templateText = new Text(parentGridComposite, SWT.WRAP|SWT.MULTI|SWT.BORDER|SWT.V_SCROLL);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = heightHint;
		templateText.setLayoutData(gridData);

		if (initialText != null) {
			templateText.setText(initialText);
		}

		return templateText;
	}

	public Label createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		return label;
	}

	public Label createSpacer(Composite parent, int size)
	{
		return createLabel(parent, StringHelper.createString(" ", size));
	}
	public Text createFileSelectionText(Composite targetFileContainer, ModifyListener modifyListener) {
		Text targetFileText = new Text(targetFileContainer, SWT.BORDER);
		targetFileText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		targetFileText.addModifyListener(modifyListener);
		return targetFileText;
	}

	public Button createButton(Composite parent, String buttonText, SelectionListener selectionListener) {
		Button browseButton = new Button(parent, SWT.NONE);
		browseButton.setText(buttonText);

		if (selectionListener != null) {
			browseButton.addSelectionListener(selectionListener);
		}

		return browseButton;
	}

	public Button createBrowseButton(Composite parent, SelectionListener selectionListener) {
		final String BROWSE_LABEL = "Browse...";
		return createButton(parent, BROWSE_LABEL, selectionListener);
	}

}