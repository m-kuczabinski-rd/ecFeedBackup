package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.ui.editor.actions.NamedAction;
import com.testify.ecfeed.ui.modelif.AbstractNodeInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public abstract class AbstractCommentsSection extends ButtonsCompositeSection {

	private final static int STYLE = Section.TITLE_BAR | Section.COMPACT | Section.TWISTIE;
	private final static String SECTION_TITLE = "Comments";

	protected class ImportAllCommentsAction extends NamedAction{

		public ImportAllCommentsAction() {
			super(JAVADOC_IMPORT_ACTION_ID, JAVADOC_IMPORT_ACTION_NAME);
		}

		@Override
		public void run(){
			getTargetIf().importAllJavadocComments();
		}
	}

	private Button fEditButton;

	private AbstractNode fTarget;
	private AbstractNodeInterface fTargetIf;

	public AbstractCommentsSection(ISectionContext sectionContext,
			IModelUpdateContext updateContext) {
		super(sectionContext, updateContext, STYLE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		getSection().setLayoutData(gd);
		getSection().setText(SECTION_TITLE);
		fTargetIf = new AbstractNodeInterface(getUpdateContext());
		getSection().layout();
	}

	@Override
	protected Composite createClientComposite() {
		Composite client = super.createClientComposite();
		ToolBar toolBar = createToolBar(getMainControlComposite());
		getToolBarManager().update(true);
		toolBar.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		createCommentsControl(getMainControlComposite());
		createCommentsButtons();
		return client;
	}

	protected void createCommentsButtons() {
		fEditButton = addButton("Edit comments", null);
	}

	protected void addEditListener(SelectionAdapter listener){
		fEditButton.addSelectionListener(listener);
	}

	protected void setInput(AbstractNode input){
		fTarget = input;
		fTargetIf.setTarget(input);
	}

	protected AbstractNode getTarget(){
		return fTarget;
	}

	protected Button getEditButton(){
		return fEditButton;
	}

	protected AbstractNodeInterface getTargetIf(){
		return fTargetIf;
	}

	protected abstract Control createCommentsControl(Composite parent);
}
