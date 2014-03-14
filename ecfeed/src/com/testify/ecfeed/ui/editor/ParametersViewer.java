package com.testify.ecfeed.ui.editor;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ExpectedValueCategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.ui.common.DefaultValueEditingSupport;
import com.testify.ecfeed.ui.common.TestDataEditorListener;

public class ParametersViewer extends TableViewerSection implements TestDataEditorListener{

	private final static int STYLE = Section.TWISTIE | Section.TITLE_BAR;
	private final String EMPTY_STRING = "";
	private ColorManager fColorManager;
	private TableViewerColumn fDefaultValueColumn;
	private MethodNode fSelectedMethod;
	
	public ParametersViewer(BasicDetailsPage parent, FormToolkit toolkit) {
		super(parent.getMainComposite(), toolkit, STYLE, parent);
		getSection().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		fColorManager = new ColorManager();
		getSection().setText("Parameters");
		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
	}

	@Override
	protected void createTableColumns() {
		addColumn("Name", 150, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				String result = new String();
				if(element instanceof ExpectedValueCategoryNode){
					result += "[e]";
				}
				result += ((CategoryNode)element).getName();
				return result;
			}

			@Override
			public Color getForeground(Object element){
				return getColor(element);
			}
		});
		
		addColumn("Type", 150, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((CategoryNode)element).getType();
			}
			@Override
			public Color getForeground(Object element){
				return getColor(element);
			}
		});
		
		fDefaultValueColumn = addColumn("Default value", 150, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				if(element instanceof ExpectedValueCategoryNode){
					ExpectedValueCategoryNode category = (ExpectedValueCategoryNode)element;
					return category.getDefaultValuePartition().getValueString();
				}
				return EMPTY_STRING ;
			}
			@Override
			public Color getForeground(Object element){
				return getColor(element);
			}
		});
		fDefaultValueColumn.setEditingSupport(new DefaultValueEditingSupport(getTableViewer(), this));
	}
		
	public void setInput(MethodNode method){
		fSelectedMethod = method;
		showDefaultValueColumn(fSelectedMethod.getExpectedCategoriesNames().size() == 0);
		super.setInput(method.getCategories());
	}

	private Color getColor(Object element){
		if(element instanceof ExpectedValueCategoryNode){
			return fColorManager.getColor(ColorConstants.EXPECTED_VALUE_CATEGORY);
		}
		return null;
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
	public void testDataChanged() {
		modelUpdated();
	}

}
