package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class SingleTextCommentsSection extends AbstractCommentsSection {

	private final int TEXT_STYLE = SWT.WRAP | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.READ_ONLY;

	private class EditCommentsAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			getTargetIf().editComments();
		}
	}

	private Text fCommentsText;

	public SingleTextCommentsSection(ISectionContext sectionContext, IModelUpdateContext updateContext) {
		super(sectionContext, updateContext);
		addEditListener(new EditCommentsAdapter());
	}

	@Override
	protected Control createCommentsControl(Composite parent){
		fCommentsText = getToolkit().createText(parent, "", TEXT_STYLE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 150;
		fCommentsText.setLayoutData(gd);
		fCommentsText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		return fCommentsText;
	}

	@Override
	public void refresh(){
		super.refresh();
		if(getEditButton() != null){
			getEditButton().setText(fCommentsText.getText().length() > 0 ? "Edit comments" : "Add comments");
		}
		fCommentsText.setText(getTargetIf().getComments());
	}

	@Override
	public void setInput(AbstractNode input){
		super.setInput(input);
		refresh();
	}

	@Override
	protected boolean commentsExportable(){
		return false;
	}

}
