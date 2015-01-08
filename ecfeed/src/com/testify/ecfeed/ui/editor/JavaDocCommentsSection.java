package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public abstract class JavaDocCommentsSection extends TabFolderCommentsSection {

	private class TabFolderEditButtonListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			getTargetIf().editComments();
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
		getEditButton().setText(getCommentsText().getText().length() > 0 ? "Edit comment" : "Add comment");
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
