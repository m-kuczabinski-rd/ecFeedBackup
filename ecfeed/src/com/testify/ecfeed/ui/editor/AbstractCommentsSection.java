package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.modelif.AbstractNodeInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public abstract class AbstractCommentsSection extends ButtonsCompositeSection {

	private final static int STYLE = Section.TITLE_BAR | Section.COMPACT | Section.TWISTIE;
	private final static String SECTION_TITLE = "Comments";

	private class EditCommentsAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			getTargetIf().editComments();
		}
	}


	private Button fEditButton;

	private AbstractNode fTarget;
	private AbstractNodeInterface fTargetIf;

	public AbstractCommentsSection(ISectionContext sectionContext, IModelUpdateContext updateContext) {
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
		createCommentsControl(getMainControlComposite());
		createCommentsButtons();
		return client;
	}

	protected void createCommentsButtons() {
		fEditButton = addButton("Edit comments", null);
		fEditButton.setToolTipText(Messages.TOOLTIP_EDIT_COMMENTS);
		fEditButton.addSelectionListener(createEditButtonSelectionAdapter());
	}

	protected void addEditListener(SelectionAdapter listener){
		if(fEditButton != null){
			fEditButton.addSelectionListener(listener);
		}
	}

	protected void setInput(AbstractNode input){
		fTarget = input;
		getTargetIf().setTarget(input);
		refresh();
	}

	@Override
	public void refresh(){
		if(getTargetIf() != null && getTargetIf().getComments() != null && getTargetIf().getComments().length() > 0){
			getEditButton().setText("Edit comments");
		}else{
			getEditButton().setText("Add comments");
		}
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

	@Override
	protected int buttonsPosition() {
		return BUTTONS_BELOW;
	}

	protected SelectionListener createEditButtonSelectionAdapter(){
		return new EditCommentsAdapter();
	}

	protected abstract Control createCommentsControl(Composite parent);
}
