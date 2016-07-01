/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.ecfeed.core.adapter.IModelImplementer;
import com.ecfeed.core.adapter.ModelOperationManager;
import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.ui.common.utils.IFileInfoProvider;
import com.ecfeed.ui.editor.actions.GoToImplementationAction;
import com.ecfeed.ui.editor.actions.ImplementAction;
import com.ecfeed.ui.modelif.IModelUpdateContext;
import com.ecfeed.ui.modelif.IModelUpdateListener;
import com.testify.ecfeed.ui.common.EclipseModelImplementer;
import com.testify.ecfeed.ui.common.ImageManager;

public abstract class BasicDetailsPage implements IDetailsPage, IModelUpdateListener, ISectionContext, IModelUpdateContext{

	private static final int MAIN_SECTION_STYLE = Section.EXPANDED | Section.TITLE_BAR;

	protected class ImplementToolbarAction extends ImplementAction{

		public ImplementToolbarAction() {
			super(null, BasicDetailsPage.this, fImplementer);
			setToolTipText("Implement node");
			setImageDescriptor(getIconDescription("implement.png"));
		}

		@Override
		protected List<AbstractNode> getSelectedNodes(){
			return Arrays.asList(new AbstractNode[]{fSelectedNode});
		}
	}

	protected class GoToImplementationToolbarAction extends GoToImplementationAction{

		public GoToImplementationToolbarAction() {
			super(null, fFileInfoProvider);
			setToolTipText("Go to node's implementation");
			setImageDescriptor(getIconDescription("goto_impl.png"));
		}

		@Override
		protected List<AbstractNode> getSelectedNodes(){
			return Arrays.asList(new AbstractNode[]{fSelectedNode});
		}
	}

	private Section fMainSection;
	private Composite fMainComposite;
	private IManagedForm fManagedForm;

	private List<IFormPart> fForms;
	private List<ViewerSection> fViewerSections;
	private ModelMasterSection fMasterSection;
	private IModelUpdateContext fModelUpdateContext;
	private AbstractNode fSelectedNode;
	private IModelImplementer fImplementer;
	private IFileInfoProvider fFileInfoProvider;
	private Button fImplementButton;
	private ToolBarManager fToolBarManager;

	public BasicDetailsPage(
			ModelMasterSection masterSection, 
			IModelUpdateContext updateContext, 
			IFileInfoProvider fileInfoProvider){

		fMasterSection = masterSection;
		fForms = new ArrayList<IFormPart>();
		fViewerSections = new ArrayList<ViewerSection>();
		fModelUpdateContext = updateContext;
		fImplementer = new EclipseModelImplementer(fileInfoProvider);
		fFileInfoProvider = fileInfoProvider;
	}

	@Override
	public void initialize(IManagedForm form) {
		fManagedForm = form;
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		fSelectedNode = (AbstractNode)fMasterSection.getSelectedElement();
	}

	@Override
	public void createContents(Composite parent) {
		parent.setLayout(new FillLayout());
		fMainSection = getToolkit().createSection(parent, MAIN_SECTION_STYLE);
		Composite textClient = createTextClientComposite();
		fMainSection.setTextClient(textClient);

		getToolkit().adapt(getMainSection());

		fMainComposite = getToolkit().createComposite(getMainSection(), SWT.NONE);
		fMainComposite.setLayout(new GridLayout(1, false));
		getToolkit().adapt(fMainComposite);
		getMainSection().setClient(fMainComposite);
	}

	protected IFileInfoProvider getFileInfoProvider() {
		return fFileInfoProvider;
	}

	protected ToolBar createToolBar(Section section) {
		fToolBarManager = new ToolBarManager(SWT.FLAT);
		ToolBar toolbar = fToolBarManager.createControl(section);
		final Cursor handCursor = Display.getCurrent().getSystemCursor(SWT.CURSOR_HAND);
		toolbar.setCursor(handCursor);
		fToolBarManager.update(true);
		return toolbar;
	}

	protected ToolBarManager getToolBarManager(){
		return fToolBarManager;
	}

	protected Composite createTextClientComposite() {
		ToolBar toolbar = createToolBar(fMainSection);
		addToolbarActions();
		return toolbar;
	}

	protected void addToolbarActions(){
		if (!fFileInfoProvider.isProjectAvailable()) {
			return;
		}
		if(fImplementer.implementable(getNodeType())){
			getToolBarManager().add(new GoToImplementationToolbarAction());
			getToolBarManager().add(new ImplementToolbarAction());
		}
	}

	@Override
	public void refresh(){
		fSelectedNode = (AbstractNode)fMasterSection.getSelectedElement();
		refreshTextClient();
	}

	protected void refreshTextClient(){
		if(fSelectedNode == null && fImplementButton != null){
			fImplementButton.setEnabled(false);
		}
		else if(fToolBarManager != null){
			fToolBarManager.update(true);
			for(IContributionItem item : fToolBarManager.getItems()){
				item.update();
			}
		}
	}

	@Override
	public void dispose() {
		for(IFormPart form : fForms){
			form.dispose();
		}
	}

	@Override
	public boolean isDirty() {
		for(IFormPart form : fForms){
			if(form.isDirty()){
				return true;
			};
		}
		return false;
	}

	@Override
	public boolean setFormInput(Object input) {
		return false;
	}

	@Override
	public void commit(boolean onSave) {
		for(IFormPart form : fForms){
			form.commit(onSave);
		}
	}

	@Override
	public void setFocus() {
	}

	@Override
	public boolean isStale() {
		for(IFormPart form : fForms){
			if(form.isStale()){
				return true;
			};
		}
		return false;
	}

	@Override
	public FormToolkit getToolkit(){
		return fManagedForm.getToolkit();
	}
	
	public FormObjectToolkit getFormObjectFactory() {
		return FormObjectToolkit.getInstance(getToolkit());
	}

	protected Section getMainSection(){
		return fMainSection;
	}

	@Override
	public ModelMasterSection getMasterSection(){
		return fMasterSection;
	}

	protected void addForm(IFormPart form){
		fForms.add(form);
		form.initialize(getManagedForm());
	}

	protected void addViewerSection(ViewerSection section){
		addForm(section);
		fViewerSections.add(section);
	}

	protected Object getSelectedElement(){
		return getMasterSection().getSelectedElement();
	}

	protected IManagedForm getManagedForm(){
		return fManagedForm;
	}

	protected Composite getMainComposite(){
		return fMainComposite;
	}

	@Override
	public void modelUpdated(AbstractFormPart source){
		if(source != null){
			source.markDirty();
		}
		if(getMasterSection() != null){
			getMasterSection().markDirty();
		}
		if(getMasterSection() != null){
			getMasterSection().refresh();
		}
		refresh();
	}

	protected Shell getActiveShell(){
		return Display.getCurrent().getActiveShell();
	}

	public BasicSection getFocusedViewerSection(){
		for(ViewerSection section : fViewerSections){
			if(section.getViewer().getControl().isFocusControl()){
				return section;
			}
		}
		return null;
	}

	@Override
	public Composite getSectionComposite(){
		return fMainComposite;
	}

	@Override
	public ModelOperationManager getOperationManager(){
		return fModelUpdateContext.getOperationManager();
	}

	@Override
	public AbstractFormPart getSourceForm(){
		return null;
	}

	@Override
	public List<IModelUpdateListener> getUpdateListeners(){
		return Arrays.asList(new IModelUpdateListener[]{this});
	}

	@Override
	public IUndoContext getUndoContext(){
		return fModelUpdateContext.getUndoContext();
	}

	protected ImageDescriptor getIconDescription(String fileName) {
		return ImageManager.getInstance().getImageDescriptor(fileName);
	}

	abstract protected Class<? extends AbstractNode> getNodeType();
}
