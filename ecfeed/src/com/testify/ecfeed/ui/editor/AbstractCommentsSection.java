package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.modelif.AbstractNodeInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public abstract class AbstractCommentsSection extends ButtonsCompositeSection {

	private final static int STYLE = Section.TITLE_BAR | Section.COMPACT | Section.TWISTIE;
	private final static String SECTION_TITLE = "Comments";

	protected class ExportAllSelectionAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			getTargetIf().exportAllComments();
		}
	}

	protected class ImportAllSelectionAdapter extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			getTargetIf().importAllJavadocComments();
		}
	}

	private Button fEditButton;

	private AbstractNode fTarget;
	private AbstractNodeInterface fTargetIf;
	private Button fExportButton;
	private Button fImportButton;
	private Menu fExportButtonMenu;
	private Menu fImportButtonMenu;

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
		ToolBar toolBar = createToolBar(getMainControlComposite());
		getToolBarManager().update(true);
		toolBar.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		createCommentsControl(getMainControlComposite());
		createCommentsButtons(commentsExportable());
		return client;
	}

	protected void createCommentsButtons(boolean createExportAndImport) {
		fEditButton = addButton("Edit comments", null);
		fEditButton.setToolTipText(Messages.TOOLTIP_EDIT_COMMENTS);

		if(createExportAndImport){
			fExportButton = addButton("Export...", null);
			fImportButton = addButton("Import...", null);

			fExportButtonMenu = new Menu(fExportButton);
			fExportButton.addSelectionListener(new AbstractSelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					fExportButtonMenu.setVisible(true);
				}
			});
			fImportButtonMenu = new Menu(fImportButton);
			fImportButton.addSelectionListener(new AbstractSelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					fImportButtonMenu.setVisible(true);
				}
			});
			createExportMenuItems();
			createImportMenuItems();
		}
	}

	protected void createImportMenuItems() {
	}

	protected void createExportMenuItems() {
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
		boolean importExportEnabled = importExportEnabled();
		if(getExportButton() != null && getExportButton().isDisposed() == false){
			getExportButton().setEnabled(importExportEnabled);
		}
		if(getImportButton() != null && getImportButton().isDisposed() == false){
			getImportButton().setEnabled(importExportEnabled);
		}
	}

	protected AbstractNode getTarget(){
		return fTarget;
	}

	protected Button getEditButton(){
		return fEditButton;
	}

	protected Button getExportButton(){
		return fExportButton;
	}

	protected Button getImportButton(){
		return fImportButton;
	}

	protected AbstractNodeInterface getTargetIf(){
		return fTargetIf;
	}

	@Override
	protected int buttonsPosition() {
		return BUTTONS_BELOW;
	}

	protected Menu getExportButtonMenu(){
		return fExportButtonMenu;
	}

	protected Menu getImportButtonMenu(){
		return fImportButtonMenu;
	}

	protected boolean importExportEnabled(){
		return getTargetIf().commentsImportExportEnabled();
	}

	protected abstract boolean commentsExportable();

	protected abstract Control createCommentsControl(Composite parent);
}
