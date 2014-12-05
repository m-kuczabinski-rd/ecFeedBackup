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

package com.testify.ecfeed.ui.common;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.adapter.ITypeAdapter;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;

public class TestDataValueEditingSupport extends EditingSupport {
	private final TableViewer fViewer;
	private ComboBoxViewerCellEditor fComboCellEditor;
	private ITestDataEditorListener fSetValueListener;
	private MethodNode fMethod;

	public TestDataValueEditingSupport(MethodNode method, TableViewer viewer, List<ChoiceNode> testData, ITestDataEditorListener setValueListener) {
		super(viewer);
		fViewer = viewer;
		fSetValueListener = setValueListener;
		fMethod = method;

		fComboCellEditor = new ComboBoxViewerCellEditor(fViewer.getTable(), SWT.TRAIL);
		fComboCellEditor.setLabelProvider(new LabelProvider());
		fComboCellEditor.setContentProvider(new ArrayContentProvider());
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		ChoiceNode choice = (ChoiceNode)element;
		return getComboCellEditor(choice);
	}

	private CellEditor getComboCellEditor(ChoiceNode choice) {
		String type = choice.getParameter().getType();
		EclipseModelBuilder builder = new EclipseModelBuilder();

		if (fMethod.getMethodParameter(choice).isExpected()) {
			Set<String> expectedValues = new HashSet<String>();
			for (String specialValue : builder.getSpecialValues(type)) {
				expectedValues.add(specialValue);
			}
			if (expectedValues.contains(choice.getValueString()) == false) {
				expectedValues.add(choice.getValueString());
			}
			fComboCellEditor.setInput(expectedValues);

			if (JavaUtils.hasLimitedValuesSet(type) == false) {
				fComboCellEditor.getViewer().getCCombo().setEditable(true);
			} else {
				fComboCellEditor.setActivationStyle(ComboBoxViewerCellEditor.DROP_DOWN_ON_KEY_ACTIVATION |
						ComboBoxViewerCellEditor.DROP_DOWN_ON_MOUSE_ACTIVATION);
				fComboCellEditor.getViewer().getCCombo().setEditable(false);
			}
		} else {
			fComboCellEditor.setActivationStyle(ComboBoxViewerCellEditor.DROP_DOWN_ON_KEY_ACTIVATION |
					ComboBoxViewerCellEditor.DROP_DOWN_ON_MOUSE_ACTIVATION);
			fComboCellEditor.setInput(choice.getParameter().getLeafChoices());
			fComboCellEditor.getViewer().getCCombo().setEditable(false);
			fComboCellEditor.setValue(choice);
		}
		return fComboCellEditor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		ChoiceNode choice = (ChoiceNode)element;
		if(fMethod.getMethodParameter(choice).isExpected()){
			return choice.getValueString();
		}
		return choice.toString();
	}

	@Override
	protected void setValue(Object element, Object value) {
		ChoiceNode current = (ChoiceNode)element;
		MethodParameterNode parameter = fMethod.getMethodParameter(current);
		int index = parameter.getIndex();
		ChoiceNode newValue = null;
		if(parameter.isExpected()){
			String valueString = fComboCellEditor.getViewer().getCCombo().getText();
			String type = parameter.getType();
			ITypeAdapter adapter = new EclipseTypeAdapterProvider().getAdapter(type);
			if(adapter.convert(valueString) == null){
				MessageDialog.openError(Display.getCurrent().getActiveShell(),
						Messages.DIALOG_CHOICE_VALUE_PROBLEM_TITLE,
						Messages.DIALOG_CHOICE_VALUE_PROBLEM_MESSAGE(valueString));
				return;
			}
			else if(valueString.equals(current.getValueString()) == false){
				newValue = current.getCopy();
				newValue.setValueString(valueString);
			}
		}
		else if(value instanceof ChoiceNode){
			if((ChoiceNode)value != current){
				newValue = (ChoiceNode)value;
			}
		}
		if(newValue != null){
			fSetValueListener.testDataChanged(index, newValue);
		}
	}

	public void setMethod(MethodNode method) {
		fMethod = method;
	}
}
