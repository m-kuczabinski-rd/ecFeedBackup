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

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.ui.common.utils.IFileInfoProvider;
import com.testify.ecfeed.ui.modelif.AbstractParameterInterface;
import com.testify.ecfeed.ui.modelif.ChoiceInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class ChoiceDetailsPage extends BasicDetailsPage {

	private IFileInfoProvider fFileInfoProvider;	
	private ChoicesViewer fChildrenViewer;
	private ChoiceLabelsViewer fLabelsViewer;
	private Composite fAttributesComposite;
	private Text fNameText;
	private Combo fValueCombo;

	private ChoiceInterface fChoiceIf;
	private AbstractCommentsSection fCommentsSection;

	private class NameTextListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			fChoiceIf.setName(fNameText.getText());
			fNameText.setText(fChoiceIf.getName());
		}
	}

	private class ValueComboListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			fChoiceIf.setValue(fValueCombo.getText());

			ChoiceNode choiceNode = getSelectedChoice();
			if (choiceNode != null) {
				setValueComboText(choiceNode);
			}
		}
	}

	public ChoiceDetailsPage(
			ModelMasterSection masterSection, 
			IModelUpdateContext updateContext, 
			IFileInfoProvider fileInfoProvider) {
		super(masterSection, updateContext, fileInfoProvider);
		fFileInfoProvider = fileInfoProvider;
		fChoiceIf = new ChoiceInterface(this, fFileInfoProvider);
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);

		createNameValueEditor(getMainComposite());
		addForm(fCommentsSection = new ChoiceCommentsSection(this, this, fFileInfoProvider));
		addViewerSection(fChildrenViewer = new ChoicesViewer(this, this, fFileInfoProvider));
		addViewerSection(fLabelsViewer = new ChoiceLabelsViewer(this, this, fFileInfoProvider));

		getToolkit().paintBordersFor(getMainComposite());
	}

	@Override
	protected Composite createTextClientComposite(){
		Composite client = super.createTextClientComposite();
		return client;
	}

	@Override
	public void refresh(){
		super.refresh();
		ChoiceNode selectedChoice = getSelectedChoice();
		if(selectedChoice != null){
			fChoiceIf.setTarget(selectedChoice);

			String title = getSelectedChoice().toString();
			getMainSection().setText(title);

			fCommentsSection.setInput(selectedChoice);
			fChildrenViewer.setInput(selectedChoice);
			fLabelsViewer.setInput(selectedChoice);
			fNameText.setText(selectedChoice.getName());
			refreshValueEditor(selectedChoice);
		}
	}

	private void refreshValueEditor(ChoiceNode choiceNode) {
		String type = fChoiceIf.getParameter().getType();
		if(fValueCombo != null && fValueCombo.isDisposed() == false){
			fValueCombo.dispose();
		}
		int style = SWT.DROP_DOWN;
		if(AbstractParameterInterface.isBoolean(type)){
			style |= SWT.READ_ONLY;
		}
		fValueCombo = new ComboViewer(fAttributesComposite, style).getCombo();
		fValueCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		Set<String> items = new LinkedHashSet<String>(AbstractParameterInterface.getSpecialValues(type));
		if(JavaUtils.isUserType(type)){
			Set<String> usedValues = fChoiceIf.getParameter().getLeafChoiceValues();
			usedValues.removeAll(items);
			items.addAll(usedValues);
		}
		items.add(fChoiceIf.getValue());
		fValueCombo.setItems(items.toArray(new String[]{}));
		setValueComboText(choiceNode);
		fValueCombo.addSelectionListener(new ValueComboListener());

		if (choiceNode.isAbstract()) {
			fValueCombo.setEnabled(false);
		} else {
			fValueCombo.setEnabled(true);
		}

		fAttributesComposite.layout();
	}

	private void setValueComboText(ChoiceNode choiceNode) {
		if (choiceNode.isAbstract()) {
			fValueCombo.setText(ChoiceNode.ABSTRACT_CHOICE_MARKER);
		} else {
			fValueCombo.setText(fChoiceIf.getValue());
		}
	}

	private ChoiceNode getSelectedChoice(){
		if(getSelectedElement() != null && getSelectedElement() instanceof ChoiceNode) {
			return (ChoiceNode)getSelectedElement();
		}
		return null;
	}

	private void createNameValueEditor(Composite parent) {
		fAttributesComposite = getToolkit().createComposite(parent);
		fAttributesComposite.setLayout(new GridLayout(2, false));
		fAttributesComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		getToolkit().createLabel(fAttributesComposite, "Name");
		fNameText = getToolkit().createText(fAttributesComposite, "", SWT.NONE);
		fNameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		fNameText.addSelectionListener(new NameTextListener());

		getToolkit().createLabel(fAttributesComposite, "Value");
		getToolkit().paintBordersFor(fAttributesComposite);
	}

	@Override
	protected Class<? extends AbstractNode> getNodeType() {
		return ChoiceNode.class;
	}
}
