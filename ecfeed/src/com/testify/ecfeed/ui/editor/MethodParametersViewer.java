package com.testify.ecfeed.ui.editor;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.ui.common.NodeViewerColumnLabelProvider;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.MethodInterface;
import com.testify.ecfeed.ui.modelif.ParameterInterface;
import com.testify.ecfeed.ui.modelif.ParametersParentInterface;


public class MethodParametersViewer extends AbstractParametersViewer {

	private final static int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	private final String EMPTY_STRING = "";

	private MethodNode fSelectedMethod;

	private TableViewerColumn fExpectedColumn;
	private TableViewerColumn fDefaultValueColumn;

	private ParameterInterface fParameterIf;
	private MethodInterface fMethodIf;

	private class ExpectedValueEditingSupport extends EditingSupport {

		private final String[] EDITOR_ITEMS = {"Yes", "No"};
		private ComboBoxCellEditor fCellEditor;

		public ExpectedValueEditingSupport() {
			super(getTableViewer());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			if(fCellEditor == null){
				fCellEditor = new ComboBoxCellEditor(getTable(), EDITOR_ITEMS, SWT.READ_ONLY);
				fCellEditor.setActivationStyle(ComboBoxCellEditor.DROP_DOWN_ON_MOUSE_ACTIVATION);
			}
			return fCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			MethodParameterNode parameter = (MethodParameterNode)element;
			return parameter.isLinked() == false || JavaUtils.isUserType(parameter.getType());
		}

		@Override
		protected Object getValue(Object element) {
			MethodParameterNode node = (MethodParameterNode)element;
			return (node.isExpected() ? 0 : 1);
		}

		@Override
		protected void setValue(Object element, Object value) {
			MethodParameterNode node = (MethodParameterNode)element;
			boolean expected = ((int)value == 0) ? true : false;
			fParameterIf.setTarget(node);
			fParameterIf.setExpected(expected);
			fCellEditor.setFocus();
		}
	}

	private class DefaultValueEditingSupport extends EditingSupport {
		private ComboBoxViewerCellEditor fComboCellEditor;

		public DefaultValueEditingSupport() {
			super(getTableViewer());
			fComboCellEditor = new ComboBoxViewerCellEditor(getTable(), SWT.TRAIL);
			fComboCellEditor.setLabelProvider(new LabelProvider());
			fComboCellEditor.setContentProvider(new ArrayContentProvider());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			MethodParameterNode parameter = (MethodParameterNode)element;
			ArrayList<String> expectedValues = new ArrayList<String>();
			for(String value : ParameterInterface.getSpecialValues(parameter.getType())){
				expectedValues.add(value);
			}
			if(expectedValues.contains(parameter.getDefaultValue()) == false){
				expectedValues.add(parameter.getDefaultValue());
			}
			for(ChoiceNode leaf : parameter.getLeafChoices()){
				if(!expectedValues.contains(leaf.getValueString())){
					expectedValues.add(leaf.getValueString());
				}
			}

			fComboCellEditor.setInput(expectedValues);
			fComboCellEditor.setValue(parameter.getDefaultValue());

			fParameterIf.setTarget(parameter);
			if(fParameterIf.hasLimitedValuesSet()){
				fComboCellEditor.getViewer().getCCombo().setEditable(false);
			}
			else{
				fComboCellEditor.setActivationStyle(ComboBoxViewerCellEditor.DROP_DOWN_ON_KEY_ACTIVATION
						| ComboBoxViewerCellEditor.DROP_DOWN_ON_MOUSE_ACTIVATION);
				fComboCellEditor.getViewer().getCCombo().setEditable(true);
			}
			return fComboCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return (element instanceof MethodParameterNode && ((MethodParameterNode)element).isExpected());
		}

		@Override
		protected Object getValue(Object element) {
			return ((MethodParameterNode)element).getDefaultValue();
		}

		@Override
		protected void setValue(Object element, Object value) {
			MethodParameterNode parameter = (MethodParameterNode)element;
			String valueString = null;
			if(value instanceof String){
				valueString = (String)value;
			} else if(value == null){
				valueString = fComboCellEditor.getViewer().getCCombo().getText();
			}
			fParameterIf.setTarget(parameter);
			fParameterIf.setDefaultValue(valueString);
		}

	}

	public MethodParametersViewer(ISectionContext sectionContext, IModelUpdateContext updateContext) {
		super(sectionContext, updateContext, STYLE);
		fParameterIf = new ParameterInterface(this);

		getSection().setText("Parameters");
		fExpectedColumn.setEditingSupport(new ExpectedValueEditingSupport());
		fDefaultValueColumn.setEditingSupport(new DefaultValueEditingSupport());
	}

	private MethodInterface getMethodInterface() {
		if(fMethodIf == null){
			fMethodIf = new MethodInterface(this);
		}
		return fMethodIf;
	}

	@Override
	protected void createTableColumns() {
		super.createTableColumns();
		fExpectedColumn = addColumn("Expected", 150, new NodeViewerColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				MethodParameterNode node = (MethodParameterNode)element;
				return (node.isExpected() ? "Yes" : "No");
			}
		});

		fDefaultValueColumn = addColumn("Default value", 150, new NodeViewerColumnLabelProvider(){
			@Override
			public String getText(Object element){
				if(element instanceof MethodParameterNode && ((MethodParameterNode)element).isExpected()){
					MethodParameterNode parameter = (MethodParameterNode)element;
					return parameter.getDefaultValue();
				}
				return EMPTY_STRING ;
			}
		});
	}

	public void setInput(MethodNode method){
		getMethodInterface().setTarget(method);
		fSelectedMethod = method;
		showDefaultValueColumn(fSelectedMethod.getParametersNames(true).size() == 0);
		super.setInput(method);
	}

	private void showDefaultValueColumn(boolean show) {
		if(show){
			fDefaultValueColumn.getColumn().setWidth(0);
			fDefaultValueColumn.getColumn().setResizable(false);
		}
		else{
			fDefaultValueColumn.getColumn().setWidth(150);
			fDefaultValueColumn.getColumn().setResizable(true);
		}
	}

	@Override
	protected ParametersParentInterface getParametersParentInterface() {
		return getMethodInterface();
	}
}
