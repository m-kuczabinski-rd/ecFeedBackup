package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TabItem;

import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.ui.modelif.AbstractParameterInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public abstract class AbstractParameterCommentsSection extends TabFolderCommentsSection {

	private TabItem fParameterCommentsTab;
	private TabItem fTypeCommentsTab;

	protected class EditButtonListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(getActiveItem() == fParameterCommentsTab){
				getTargetIf().editComments();
			}
			else if(getActiveItem() == fTypeCommentsTab){
				getTargetIf().editTypeComments();
			}
		}
	}

	public AbstractParameterCommentsSection(ISectionContext sectionContext, IModelUpdateContext updateContext) {
		super(sectionContext, updateContext);

		fParameterCommentsTab = addTextTab("Parameter", true);
		fTypeCommentsTab = addTextTab("Type", true);

		addEditListener(new EditButtonListener());
	}

	@Override
	public void refresh(){
		super.refresh();
		getTextFromTabItem(fParameterCommentsTab).setText(getTargetIf().getComments());
		getTextFromTabItem(fTypeCommentsTab).setText(getTargetIf().getTypeComments());
	}

	public void setInput(AbstractParameterNode input){
		super.setInput(input);
		getTargetIf().setTarget(input);
		refresh();
	}

	@Override
	protected abstract AbstractParameterInterface getTargetIf();

}
