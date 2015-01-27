package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class SingleTextCommentsSection extends AbstractCommentsSection {

	private final int TEXT_STYLE = SWT.WRAP | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.READ_ONLY;

	private Text fCommentsText;

	public SingleTextCommentsSection(ISectionContext sectionContext, IModelUpdateContext updateContext) {
		super(sectionContext, updateContext);
	}

	@Override
	protected Composite createClientComposite() {
		Composite composite = super.createClientComposite();

		fCommentsText = getToolkit().createText(getTabFolder(), "", TEXT_STYLE);
		fCommentsText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		addTabItem(fCommentsText, "Comments");

		return composite;
//
//		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
//		gd.heightHint = 150;
//		fCommentsText.setLayoutData(gd);
//		return fCommentsText;
	}

	@Override
	public void refresh(){
		super.refresh();
		fCommentsText.setText(getTargetIf().getComments());
	}
}
