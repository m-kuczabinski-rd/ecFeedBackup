/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
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
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.ui.common.utils.IFileInfoProvider;
import com.testify.ecfeed.ui.modelif.AbstractParameterInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public abstract class AbstractParameterDetailsPage extends BasicDetailsPage {

	private IFileInfoProvider fFileInfoProvider;
	private Composite fAttributesComposite;
	private Text fNameText;
	private Combo fTypeCombo;
	private ChoicesViewer fChoicesViewer;
	private Button fBrowseUserTypeButton;
	private AbstractParameterCommentsSection fCommentsSection;

	private class SetNameListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			getParameterIf().setName(fNameText.getText());
			fNameText.setText(getParameterIf().getName());
		}
	}

	private class SetTypeListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			getParameterIf().setType(fTypeCombo.getText());
			fTypeCombo.setText(getParameterIf().getType());
		}
	}

	private class BrowseTypeSelectionListener extends AbstractSelectionAdapter{

		@Override
		public void widgetSelected(SelectionEvent e) {
			getParameterIf().importType();
			fTypeCombo.setText(getParameterIf().getType());
		}

	}

	public AbstractParameterDetailsPage(ModelMasterSection masterSection,
			IModelUpdateContext updateContext, IFileInfoProvider fileInfoProvider) {
		super(masterSection, updateContext, fileInfoProvider);
		fFileInfoProvider = fileInfoProvider;
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);

		createAttributesComposite();
		addForm(fCommentsSection = getParameterCommentsSection(this, this));
		addForm(fChoicesViewer = new ChoicesViewer(this, this, fFileInfoProvider));

		getToolkit().paintBordersFor(getMainComposite());
	}

	@Override
	protected Composite createTextClientComposite(){
		Composite textClient = super.createTextClientComposite();
		return textClient;
	}

	@Override
	public void refresh(){
		super.refresh();
		if(getSelectedElement() instanceof AbstractParameterNode){
			AbstractParameterNode parameter = (AbstractParameterNode)getSelectedElement();
			getParameterIf().setTarget(parameter);

			getMainSection().setText(parameter.toString());
			fNameText.setText(parameter.getName());
			fTypeCombo.setItems(AbstractParameterInterface.supportedPrimitiveTypes());
			fTypeCombo.setText(parameter.getType());

			fCommentsSection.setInput(parameter);
			fChoicesViewer.setInput(parameter);
		}
	}

	protected Composite createAttributesComposite(){
		fAttributesComposite = getToolkit().createComposite(getMainComposite());
		fAttributesComposite.setLayout(new GridLayout(3, false));
		fAttributesComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		getToolkit().createLabel(fAttributesComposite, "Parameter name: ", SWT.NONE);
		fNameText = getToolkit().createText(fAttributesComposite, "",SWT.NONE);
		fNameText.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, true, false, 2, 1));
		SelectionListener nameListener = new SetNameListener();
		fNameText.addSelectionListener(nameListener);

		getToolkit().createLabel(fAttributesComposite, "Parameter type: ", SWT.NONE);
		fTypeCombo = new Combo(fAttributesComposite,SWT.DROP_DOWN);
		fTypeCombo.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, true, false));
		fTypeCombo.addSelectionListener(new SetTypeListener());
		fBrowseUserTypeButton = getToolkit().createButton(fAttributesComposite, "Import...", SWT.NONE);
		fBrowseUserTypeButton.addSelectionListener(new BrowseTypeSelectionListener());

		getToolkit().paintBordersFor(fAttributesComposite);
		return fAttributesComposite;
	}

	protected Composite getAttributesComposite(){
		return fAttributesComposite;
	}

	protected ChoicesViewer getChoicesViewer(){
		return fChoicesViewer;
	}

	protected Combo getTypeCombo(){
		return fTypeCombo;
	}

	protected abstract AbstractParameterInterface getParameterIf();

	protected abstract AbstractParameterCommentsSection getParameterCommentsSection(ISectionContext sectionContext, IModelUpdateContext updateContext);

	protected Button getBrowseUserTypeButton() {
		return fBrowseUserTypeButton;
	}
}
