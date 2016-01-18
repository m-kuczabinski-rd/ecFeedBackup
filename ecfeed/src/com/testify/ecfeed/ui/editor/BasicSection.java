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
import java.util.List;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.testify.ecfeed.core.adapter.ModelOperationManager;
import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.ui.common.ImageManager;
import com.testify.ecfeed.ui.common.utils.IFileInfoProvider;
import com.testify.ecfeed.ui.editor.actions.IActionProvider;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.IModelUpdateListener;

public abstract class BasicSection extends SectionPart implements IModelUpdateContext{
	private Composite fClientComposite;
	private Control fTextClient;
	private IActionProvider fActionProvider;
	private IModelUpdateContext fUpdateContext;
	private IFileInfoProvider fFileInfoProvider;
	private ISectionContext fSectionContext;
	private ToolBarManager fToolBarManager;

	protected class SelectNodeDoubleClickListener implements IDoubleClickListener {

		private ModelMasterSection fMasterSection;

		public SelectNodeDoubleClickListener(ModelMasterSection masterSection){
			fMasterSection = masterSection;
		}

		@Override
		public void doubleClick(DoubleClickEvent event) {
			if(event.getSelection() instanceof IStructuredSelection){
				IStructuredSelection selection = (IStructuredSelection)event.getSelection();
				if(selection.getFirstElement() instanceof AbstractNode){
					fMasterSection.selectElement(selection.getFirstElement());
				}
			}
		}
	}

	public BasicSection(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext,
			IFileInfoProvider fileInfoProvider,
			int style){
		super(sectionContext.getSectionComposite(), sectionContext.getToolkit(), style);
		fSectionContext = sectionContext;
		fUpdateContext = updateContext;
		fFileInfoProvider = fileInfoProvider;

		createContent();
	}

	@Override
	public void refresh(){
		if(fTextClient != null){
			updateTextClient();
		}
	}

	protected FormToolkit getToolkit(){
		return fSectionContext.getToolkit();
	}

	public void setText(String title){
		getSection().setText(title);
	}

	protected Composite getClientComposite(){
		return fClientComposite;
	}

	protected void createContent(){
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 150;
		getSection().setLayoutData(gd);
		fTextClient = createTextClient();
		fClientComposite = createClientComposite();
	}

	protected Composite createClientComposite() {
		Composite client = getToolkit().createComposite(getSection());
		client.setLayout(clientLayout());
		if(clientLayoutData() != null){
			client.setLayoutData(clientLayoutData());
		}
		getSection().setClient(client);
		getToolkit().adapt(client);
		getToolkit().paintBordersFor(client);
		return client;
	}

	protected Control createTextClient() {
		Composite textClient = getToolkit().createComposite(getSection());
		textClient.setLayout(new FillLayout());
		getSection().setTextClient(fTextClient);
		return textClient;
	}

	protected ToolBar createToolBar(Composite parent) {
		ToolBar toolbar = getToolBarManager().createControl(parent);
		toolbar.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_HAND));
		fToolBarManager.update(true);
		addToolbarActions();
		return toolbar;
	}

	protected void addToolbarActions(){
		for(Action action : toolBarActions()){
			getToolBarManager().add(action);
		}
	}

	protected List<Action> toolBarActions(){
		return new ArrayList<Action>();
	}

	protected Layout clientLayout() {
		GridLayout layout = new GridLayout(1, false);
		return layout;
	}

	protected Object clientLayoutData() {
		return new GridData(SWT.FILL, SWT.FILL, true, true);
	}

	protected void updateTextClient() {
	}

	protected Shell getActiveShell(){
		return Display.getCurrent().getActiveShell();
	}

	protected void modelUpdated(){
		for(IModelUpdateListener listener : fUpdateContext.getUpdateListeners()){
			listener.modelUpdated(this);
		}
	}

	protected ToolBarManager getToolBarManager(){
		if(fToolBarManager == null){
			fToolBarManager = new ToolBarManager(SWT.RIGHT);
		}
		return fToolBarManager;
	}

	public Action getAction(String actionId) {
		if(fActionProvider != null){
			return fActionProvider.getAction(actionId);
		}
		return null;
	}

	protected void setActionProvider(IActionProvider provider){
		fActionProvider = provider;
	}

	protected IActionProvider getActionProvider(){
		return fActionProvider;
	}

	protected IModelUpdateContext getUpdateContext(){
		return this;
	}

	protected ImageDescriptor getIconDescription(String fileName) {
		return ImageManager.getInstance().getImageDescriptor(fileName);
	}

	public void setVisible(boolean visible) {
		GridData gd = (GridData)getSection().getLayoutData();
		gd.exclude = !visible;
		getSection().setLayoutData(gd);
		getSection().setVisible(visible);
	}

	@Override
	public IUndoContext getUndoContext(){
		return fUpdateContext.getUndoContext();
	}

	@Override
	public ModelOperationManager getOperationManager(){
		return fUpdateContext.getOperationManager();
	}

	@Override
	public List<IModelUpdateListener> getUpdateListeners(){
		return fUpdateContext.getUpdateListeners();
	}

	@Override
	public AbstractFormPart getSourceForm(){
		return this;
	}

	protected IFileInfoProvider getFileInfoProvider() {
		return fFileInfoProvider;
	}
}
