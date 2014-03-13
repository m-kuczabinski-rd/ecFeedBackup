package com.testify.ecfeed.ui.editor.modeleditor;

import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ExpectedValueCategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.common.ColorConstants;
import com.testify.ecfeed.ui.common.ColorManager;
import com.testify.ecfeed.ui.common.TestDataEditorListener;
import com.testify.ecfeed.ui.common.TestDataValueEditingSupport;

public class TestDataViewer extends TableViewerSection implements TestDataEditorListener{

	private static final int STYLE = Section.EXPANDED | Section.TITLE_BAR;
	private ColorManager fColorManager;
	private TableViewerColumn fValueColumn; 
	
	public TestDataViewer(BasicDetailsPage page, FormToolkit toolkit) {
		super(page.getMainComposite(), toolkit, STYLE, page);
		fColorManager = new ColorManager();
		getSection().setText("Test data");
	}

	@Override
	protected void createTableColumns() {
		addColumn("Parameter", 150, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				PartitionNode testValue = (PartitionNode)element;
				CategoryNode parent = testValue.getCategory();
				return parent.toString();
			}
			@Override
			public Color getForeground(Object element){
				return getColor(element);
			}
		});
		
		fValueColumn = addColumn("Value", 150, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				PartitionNode testValue = (PartitionNode)element;
				if(testValue.getCategory() instanceof ExpectedValueCategoryNode){
					return testValue.getValueString();
				}
				return testValue.toString();
			}
			@Override
			public Color getForeground(Object element){
				return getColor(element);
			}
		});
	}
	
	private Color getColor(Object element){
		PartitionNode partition = (PartitionNode)element;
		if(partition.getCategory() instanceof ExpectedValueCategoryNode){
			fColorManager.getColor(ColorConstants.EXPECTED_VALUE_CATEGORY);
		}
		return null;
	}

	public void setInput(TestCaseNode testCase){
		List<PartitionNode> testData = testCase.getTestData();
		super.setInput(testData);
		fValueColumn.setEditingSupport(new TestDataValueEditingSupport(getTableViewer(), testData, this));
	}

	@Override
	public void testDataChanged() {
		modelUpdated();
	}
	
}
