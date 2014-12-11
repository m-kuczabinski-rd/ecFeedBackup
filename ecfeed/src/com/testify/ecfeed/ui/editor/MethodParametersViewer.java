package com.testify.ecfeed.ui.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.ui.common.NodeViewerColumnLabelProvider;
import com.testify.ecfeed.ui.modelif.AbstractParameterInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.MethodInterface;
import com.testify.ecfeed.ui.modelif.MethodParameterInterface;
import com.testify.ecfeed.ui.modelif.ModelNodesTransfer;
import com.testify.ecfeed.ui.modelif.ParametersParentInterface;


public class MethodParametersViewer extends AbstractParametersViewer {

	private final static int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	private final String EMPTY_STRING = "";
	private final String NOT_LINKED = "NOT LINKED";

	private MethodNode fSelectedMethod;

	private TableViewerColumn fExpectedColumn;
	private TableViewerColumn fDefaultValueColumn;

	private TableViewerColumn fLinkColumn;
	private MethodParameterInterface fParameterIf;
	private MethodInterface fMethodIf;

	protected class MethodParameterTypeEditingSupport extends AbstractParametersViewer.ParameterTypeEditingSupport{
		@Override
		protected boolean canEdit(Object element) {
			return ((MethodParameterNode)element).isLinked() == false;
		}
	}

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
		private ComboBoxCellEditor fComboCellEditor;

		public DefaultValueEditingSupport() {
			super(getTableViewer());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			MethodParameterNode parameter = (MethodParameterNode)element;
			ArrayList<String> expectedValues = new ArrayList<String>(AbstractParameterInterface.getSpecialValues(parameter.getType()));
			if(expectedValues.contains(parameter.getDefaultValue()) == false){
				expectedValues.add(parameter.getDefaultValue());
			}
			if(JavaUtils.isUserType(parameter.getType())){
				for(ChoiceNode leaf : parameter.getLeafChoices()){
					if(!expectedValues.contains(leaf.getValueString())){
						expectedValues.add(leaf.getValueString());
					}
				}
			}

			fParameterIf.setTarget(parameter);
			if(fParameterIf.hasLimitedValuesSet()){
				fComboCellEditor = new ComboBoxCellEditor(getTable(), expectedValues.toArray(new String[]{}), SWT.READ_ONLY);
			}
			else{
				fComboCellEditor = new ComboBoxCellEditor(getTable(), expectedValues.toArray(new String[]{}), SWT.NONE);
			}

			return fComboCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return (element instanceof MethodParameterNode && ((MethodParameterNode)element).isExpected());
		}

		@Override
		protected Object getValue(Object element) {
			MethodParameterNode parameter = (MethodParameterNode)element;
			String defaultValue = parameter.getDefaultValue();
			String[] items = fComboCellEditor.getItems();
			return Arrays.asList(items).indexOf(defaultValue);

//			return ((MethodParameterNode)element).getDefaultValue();
		}

		@Override
		protected void setValue(Object element, Object value) {
			MethodParameterNode parameter = (MethodParameterNode)element;
			String valueString = null;
			if((int)value >= 0){
				valueString = fComboCellEditor.getItems()[(int)value];
			} else{
				valueString = ((CCombo)fComboCellEditor.getControl()).getText();
			}
			fParameterIf.setTarget(parameter);
			fParameterIf.setDefaultValue(valueString);
		}

	}

	private class LinkEditingSupport extends EditingSupport{

		private ComboBoxCellEditor fCellEditor;

		public LinkEditingSupport() {
			super(getTableViewer());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			MethodParameterNode parameter = (MethodParameterNode)element;
			if(fCellEditor == null){
				fCellEditor = new ComboBoxCellEditor(getTable(), getEditorItems(parameter), SWT.READ_ONLY);
				fCellEditor.setActivationStyle(ComboBoxCellEditor.DROP_DOWN_ON_MOUSE_ACTIVATION);
			}
			fCellEditor.setItems(getEditorItems(parameter));
			return fCellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			MethodParameterNode parameter = (MethodParameterNode)element;
			return fMethodIf.getAvailableGlobalParameters().size() > 0 && parameter.isExpected() == false;
		}

		@Override
		protected Object getValue(Object element) {
			MethodParameterNode parameter = (MethodParameterNode)element;
			GlobalParameterNode link = parameter.getLink();
			fParameterIf.setTarget(parameter);
			if(parameter.isLinked() == false){
				return 0;
			}
			if(link == null){
				fParameterIf.setLinked(false);
				return 0;
			}
			String name = link.getQualifiedName();
			String[] items = fCellEditor.getItems();
			for(int i = 1; i < items.length; ++i){
				if(items[i].equals(name)){
					return i;
				}
			}
			return 0;
		}

		@Override
		protected void setValue(Object element, Object value) {
			int index = (int)value;
			String path = fCellEditor.getItems()[index];
			fParameterIf.setTarget((MethodParameterNode)element);
			if(path.equals(NOT_LINKED)){
				fParameterIf.setLinked(false);
			}
			else{
				fParameterIf.setLinked(true);
				fParameterIf.setLink(fParameterIf.getGlobalParameter(path));
			}
		}

		private String[] getEditorItems(MethodParameterNode parameter) {
			List<String> result = new ArrayList<String>();
			result.add(NOT_LINKED);
			for(GlobalParameterNode globalParameter : fMethodIf.getAvailableGlobalParameters()){
				result.add(globalParameter.getQualifiedName());
			}
			return result.toArray(new String[]{});
		}

	}

	public MethodParametersViewer(ISectionContext sectionContext, IModelUpdateContext updateContext) {
		super(sectionContext, updateContext, STYLE);
		fParameterIf = (MethodParameterInterface)getParameterInterface();

		getSection().setText("Parameters");
		fExpectedColumn.setEditingSupport(new ExpectedValueEditingSupport());
		fDefaultValueColumn.setEditingSupport(new DefaultValueEditingSupport());
		fLinkColumn.setEditingSupport(new LinkEditingSupport());

		getViewer().addDragSupport(DND.DROP_COPY|DND.DROP_MOVE, new Transfer[]{ModelNodesTransfer.getInstance()}, new ModelNodeDragListener(getViewer()));
	}

	@Override
	protected void createTableColumns() {
		super.createTableColumns();
		fExpectedColumn = addColumn("Expected", 80, new NodeViewerColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				MethodParameterNode node = (MethodParameterNode)element;
				return (node.isExpected() ? "Yes" : "No");
			}
		});

		fDefaultValueColumn = addColumn("Default value", 100, new NodeViewerColumnLabelProvider(){
			@Override
			public String getText(Object element){
				if(element instanceof MethodParameterNode && ((MethodParameterNode)element).isExpected()){
					MethodParameterNode parameter = (MethodParameterNode)element;
					return parameter.getDefaultValue();
				}
				return EMPTY_STRING ;
			}
		});

		fLinkColumn = addColumn("Link", 150, new NodeViewerColumnLabelProvider(){
			@Override
			public String getText(Object element){
				MethodParameterNode parameter = (MethodParameterNode)element;
				if(parameter.isLinked() && parameter.getLink() != null){
					return parameter.getLink().getQualifiedName();
				}
				return "NOT LINKED";
			}
		});
	}

	public void setInput(MethodNode method){
		fSelectedMethod = method;
		showDefaultValueColumn(fSelectedMethod.getParametersNames(true).size() == 0);
		getMethodIf().setTarget(method);
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
		return getMethodIf();
	}

	protected MethodInterface getMethodIf() {
		if(fMethodIf == null){
			fMethodIf = new MethodInterface(this);
		}
		return fMethodIf;
	}

	@Override
	protected AbstractParameterInterface getParameterInterface() {
		if(fParameterIf == null){
			fParameterIf = new MethodParameterInterface(this);
		}
		return fParameterIf;
	}

	@Override
	protected EditingSupport getParameterTypeEditingSupport() {
		return new MethodParameterTypeEditingSupport();
	}
}
