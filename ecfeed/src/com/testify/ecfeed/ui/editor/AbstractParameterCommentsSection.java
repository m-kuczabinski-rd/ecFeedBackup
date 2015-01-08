package com.testify.ecfeed.ui.editor;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TabItem;

import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.ui.editor.actions.NamedAction;
import com.testify.ecfeed.ui.javadoc.JavaDocAnalyser;
import com.testify.ecfeed.ui.modelif.AbstractParameterInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public abstract class AbstractParameterCommentsSection extends TabFolderCommentsSection {

	private TabItem fParameterCommentsTab;
	private TabItem fTypeCommentsTab;

	protected class ImportTypeJavadocAction extends NamedAction{

		public ImportTypeJavadocAction() {
			super(JAVADOC_EXPORT_TYPE_ACTION_ID, JAVADOC_EXPORT_TYPE_ACTION_NAME);
			setToolTipText("Import comments of parameter type from it's source's javadoc");
			setImageDescriptor(getIconDescription("root_node.png"));
		}

		@Override
		public void run(){
			String comments = JavaDocAnalyser.importTypeJavadoc(getTarget());
			if(comments != null){
				getTargetIf().setTypeComments(comments);
				getTabFolder().setSelection(getTabFolder().indexOf(fTypeCommentsTab));
			}
		}
	}

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
	public AbstractParameterNode getTarget(){
		return (AbstractParameterNode)super.getTarget();
	}

	@Override
	protected List<Action> toolBarActions(){
		return Arrays.asList(new Action[]{new ImportTypeJavadocAction()});
	}

	@Override
	protected abstract AbstractParameterInterface getTargetIf();

}
