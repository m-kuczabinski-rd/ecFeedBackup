package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.ui.modelif.AbstractParameterInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public abstract class AbstractParameterCommentsSection extends TabFolderCommentsSection {

	private Text fParameterCommentsText;
	private Text fTypeCommentsText;

	protected class EditButtonListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(getActiveItem().getControl() == fParameterCommentsText){
				getTargetIf().editComments();
			}
			else if(getActiveItem().getControl() == fTypeCommentsText){
				getTargetIf().editTypeComments();
			}
		}
	}

	public AbstractParameterCommentsSection(ISectionContext sectionContext, IModelUpdateContext updateContext) {
		super(sectionContext, updateContext);

		fParameterCommentsText = addTextTab("Parameter", true);
		fTypeCommentsText = addTextTab("Type", true);

		addEditListener(new EditButtonListener());
	}

	@Override
	public void refresh(){
		super.refresh();
		fParameterCommentsText.setText(getTargetIf().getComments());
		fTypeCommentsText.setText(getTargetIf().getTypeComments());
	}

	public void setInput(AbstractParameterNode input){
		super.setInput(input);
		getTargetIf().setTarget(input);
		refresh();
	}

	@Override
	protected abstract AbstractParameterInterface getTargetIf();

}
