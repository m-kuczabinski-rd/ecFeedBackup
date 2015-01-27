package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
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
	public Composite createMainControlComposite(Composite parent){
		//overriding tab folder creation
		fCommentsText = getToolkit().createText(parent, "", TEXT_STYLE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.heightHint = 150;
		fCommentsText.setLayoutData(gd);
		fCommentsText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));

		return parent;
	}

	@Override
	public void refresh(){
		super.refresh();
		fCommentsText.setText(getTargetIf().getComments());
	}
}
