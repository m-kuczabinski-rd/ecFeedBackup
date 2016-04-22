/*******************************************************************************
 * Copyright (c) 2016 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class CompositeFactory {
	private static CompositeFactory instance = null;

	protected CompositeFactory() {
	}
	
	public static CompositeFactory getInstance() {
		if (instance == null) {
			instance = new CompositeFactory();
		}
		return instance;
	}

	public Composite createGridContainer(Composite parent, int countOfColumns) {

		Composite container = new Composite(parent, SWT.NONE);

		container.setLayout(new GridLayout(countOfColumns, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		return container;
	}

	public Text createText(Composite parent, int minimumHeight, String initialText) {
		Text templateText = new Text(parent, SWT.WRAP|SWT.MULTI|SWT.BORDER|SWT.V_SCROLL);

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.minimumHeight = minimumHeight;
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

	public Text createFileSelectionText(Composite targetFileContainer) {
		Text targetFileText = new Text(targetFileContainer, SWT.BORDER);
		targetFileText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		return targetFileText;
	}

	public Button createButton(Composite parent, String buttonText,SelectionListener selectionListener) {
		Button browseButton = new Button(parent, SWT.NONE);
		browseButton.setText(buttonText);
		browseButton.addSelectionListener(selectionListener);

		return browseButton;
	}

	public Button createBrowseButton(Composite parent, SelectionListener selectionListener) {
		final String BROWSE_LABEL = "Browse...";
		return createButton(parent, BROWSE_LABEL, selectionListener);
	}

}