package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.ui.editor.actions.NamedAction;
import com.testify.ecfeed.ui.javadoc.JavaDocAnalyser;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public abstract class JavaDocCommentsSection extends TabFolderCommentsSection {

	private class TabFolderEditButtonListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			getTargetIf().editComments();
		}
	}

	protected class ExportJavaDocAction extends NamedAction{

		public ExportJavaDocAction() {
			super("javadoc.export", "Export Javadoc");
			setToolTipText("Export element's comments to source's javadoc");
			setImageDescriptor(getIconDescription("class_node.png"));
		}

		@Override
		public void run(){
			String comments = getTarget().getDescription();
			if(comments != null){
				JavaDocAnalyser.exportJavadoc(getTarget());
				refresh();
				getTabFolder().setSelection(getTabFolder().indexOf(getJavaDocItem()));
			}
		}
	}

	protected class ImportJavaDocAction extends NamedAction{

		public ImportJavaDocAction() {
			super("javadoc.import", "Import Javadoc");
			setToolTipText("Import element's comments from source's javadoc");
			setImageDescriptor(getIconDescription("implement.png"));
		}

		@Override
		public void run(){
			String comments = JavaDocAnalyser.importJavadoc(getTarget());
			if(comments != null){
				getTargetIf().setComments(comments);
				getTabFolder().setSelection(getTabFolder().indexOf(getCommentsItem()));
			}
		}
	}



	private TabItem fCommentsTab;
	private TabItem fJavadocTab;

	public JavaDocCommentsSection(ISectionContext sectionContext, IModelUpdateContext updateContext) {
		super(sectionContext, updateContext);

		fCommentsTab = addTextTab("Comments", true);
		fJavadocTab = addTextTab("JavaDoc", false);

		addEditListener(new TabFolderEditButtonListener());
	}

	@Override
	public void refresh(){
		super.refresh();
		getCommentsText().setText(getTargetIf().getComments());
		getJavaDocText().setText(JavaDocAnalyser.getJavadoc(getTarget()));
		getEditButton().setText(getCommentsText().getText().length() > 0 ? "Edit comment" : "Add comment");
	}

	@Override
	protected void setInput(AbstractNode input) {
		super.setInput(input);
		refresh();
	}

	protected TabItem getCommentsItem(){
		return fCommentsTab;
	}

	protected TabItem getJavaDocItem(){
		return fJavadocTab;
	}

	protected Text getJavaDocText(){
		return getTextFromTabItem(fJavadocTab);
	}

	protected Text getCommentsText(){
		return getTextFromTabItem(fCommentsTab);
	}
}
