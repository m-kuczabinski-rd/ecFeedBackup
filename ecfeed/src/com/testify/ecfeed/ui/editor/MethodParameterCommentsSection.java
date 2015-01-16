package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TabItem;

import com.testify.ecfeed.ui.common.JavaDocSupport;
import com.testify.ecfeed.ui.modelif.AbstractParameterInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.MethodParameterInterface;

public class MethodParameterCommentsSection extends AbstractParameterCommentsSection {

	private MethodParameterInterface fTargetIf;

	private TabItem fParameterJavadocTab;
	private TabItem fTypeJavadocTab;

	protected class ImportParameterCommentsSelectionAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			getTargetIf().importJavadocComments();
			getTabFolder().setSelection(getTabFolder().indexOf(getParameterCommentsTab()));
		}
	}

	protected class ExportParameterCommentsSelectionAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			getTargetIf().exportCommentsToJavadoc(getTarget().getDescription());
			getTabFolder().setSelection(getTabFolder().indexOf(fParameterJavadocTab));
		}
	}

	public MethodParameterCommentsSection(ISectionContext sectionContext,
			IModelUpdateContext updateContext) {
		super(sectionContext, updateContext);

		fParameterJavadocTab = addTextTab("Parameter javadoc", false);
		fTypeJavadocTab = addTextTab("Type javadoc", false);
	}

	@Override
	protected AbstractParameterInterface getTargetIf() {
		if(fTargetIf == null){
			fTargetIf = new MethodParameterInterface(getUpdateContext());
		}
		return fTargetIf;
	}

	@Override
	public void refresh(){
		super.refresh();
		if(JavaDocSupport.getJavadoc(getTarget())!= null){
			getTextFromTabItem(fParameterJavadocTab).setText(JavaDocSupport.getJavadoc(getTarget()));
		}else{
			getTextFromTabItem(fParameterJavadocTab).setText("");
		}
		if(JavaDocSupport.getTypeJavadoc(getTarget()) != null){
			getTextFromTabItem(fTypeJavadocTab).setText(JavaDocSupport.getTypeJavadoc(getTarget()));
		}else{
			getTextFromTabItem(fTypeJavadocTab).setText("");
		}
	}

	@Override
	protected void createExportMenuItems() {
		MenuItem exportParameterAndTypeCommentsItem = new MenuItem(getExportButtonMenu(), SWT.NONE, 0);
		exportParameterAndTypeCommentsItem.setText("Export parameter and type comments with choices");
		MenuItem exportParameterCommentsItem = new MenuItem(getExportButtonMenu(), SWT.NONE, 1);
		exportParameterCommentsItem.setText("Export only parameter comments");
		exportParameterCommentsItem.addSelectionListener(new ExportParameterCommentsSelectionAdapter());
		MenuItem exportTypeCommentsItem = new MenuItem(getExportButtonMenu(), SWT.NONE, 1);
		exportTypeCommentsItem.setText("Export only type comments");
	}

	@Override
	protected void createImportMenuItems() {
		MenuItem importParameterAndTypeCommentsItem = new MenuItem(getImportButtonMenu(), SWT.NONE, 0);
		importParameterAndTypeCommentsItem.setText("Import parameter and type comments with choices");
		MenuItem importParameterCommentsItem = new MenuItem(getImportButtonMenu(), SWT.NONE, 1);
		importParameterCommentsItem.setText("Import only parameter comments");
		importParameterCommentsItem.addSelectionListener(new ImportParameterCommentsSelectionAdapter());
		MenuItem importTypeCommentsItem = new MenuItem(getImportButtonMenu(), SWT.NONE, 1);
		importTypeCommentsItem.setText("Import only type comments");
	}

}
