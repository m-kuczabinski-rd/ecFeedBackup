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

import java.util.List;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;

import com.ecfeed.core.model.AbstractNode;
import com.ecfeed.core.model.ChoiceNode;
import com.ecfeed.core.model.ClassNode;
import com.ecfeed.core.model.ConstraintNode;
import com.ecfeed.core.model.GlobalParameterNode;
import com.ecfeed.core.model.MethodNode;
import com.ecfeed.core.model.MethodParameterNode;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.model.TestCaseNode;
import com.ecfeed.core.utils.SystemLogger;
import com.testify.ecfeed.core.adapter.ModelOperationException;
import com.testify.ecfeed.core.adapter.ModelOperationManager;
import com.testify.ecfeed.ui.common.utils.IFileInfoProvider;
import com.testify.ecfeed.ui.dialogs.basic.ExceptionCatchDialog;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.IModelUpdateListener;

public class ModelMasterDetailsBlock extends MasterDetailsBlock implements ISelectionChangedListener, ISectionContext{

	private ModelMasterSection fMasterSection;
	private ISectionContext fMasterSectionContext;
	private ModelPage fPage;
	private FormToolkit fToolkit;
	private ModelUpdateContext fUpdateContext;
	private IFileInfoProvider fFileInfoProvider;
	private UndoActionHandler fUndoActionHandler;
	private RedoActionHandler fRedoActionHandler;
	private RootNode fModel; 

	private class ModelUpdateContext implements IModelUpdateContext{

		@Override
		public ModelOperationManager getOperationManager() {
			return getPage().getEditor().getModelOperationManager();
		}

		@Override
		public AbstractFormPart getSourceForm() {
			return null;
		}

		@Override
		public List<IModelUpdateListener> getUpdateListeners() {
			return null;
		}

		@Override
		public IUndoContext getUndoContext() {
			return getPage().getEditor().getUndoContext();
		}
	}

	private class MasterSectionContext implements ISectionContext{

		@Override
		public ModelMasterSection getMasterSection() {
			return null;
		}

		@Override
		public Composite getSectionComposite() {
			return sashForm;
		}

		@Override
		public FormToolkit getToolkit() {
			return fToolkit;
		}

	}

	private class GenericToolbarAction extends Action{
		private final String fActionId;

		public GenericToolbarAction(String id){
			fActionId = id;
		}

		@Override
		public boolean isEnabled(){
			if (fActionId == null) {
				return false;
			}

			Action action = getFocusedSection().getAction(fActionId);
			if(action  != null){
				return action.isEnabled();
			}
			return false;
		}

		@Override
		public void run(){
			Action action = getFocusedSection().getAction(fActionId);
			if(action != null){
				action.run();
			}
		}
	}

	public ModelMasterDetailsBlock(ModelPage modelPage, IFileInfoProvider fileInfoProvider) {
		fPage = modelPage;
		fUpdateContext = new ModelUpdateContext();
		fFileInfoProvider = fileInfoProvider;
		fModel = null;
	}

	public void selectNode(AbstractNode node){
		fMasterSection.selectElement(node);
	}

	@Override
	public ModelMasterSection getMasterSection(){
		return fMasterSection;
	}

	public BasicDetailsPage getCurrentPage(){
		if(detailsPart != null){
			try {
				return (BasicDetailsPage)detailsPart.getCurrentPage();
			} catch(SWTException e)	{
				SystemLogger.logCatch(e.getMessage());
			}
		}
		return null;
	}

	public ModelPage getPage(){
		return fPage;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		detailsPart.selectionChanged(fMasterSection, event.getSelection());
	}

	public ISectionContext getMasterSectionContext(){
		if(fMasterSectionContext == null){
			fMasterSectionContext = new MasterSectionContext();
		}
		return fMasterSectionContext;
	}

	@Override
	protected void createMasterPart(IManagedForm managedForm, Composite parent) {
		try {
			fModel = getModel();
		} catch (ModelOperationException e) {
			ExceptionCatchDialog.open(null, e.getMessage());
			return;
		}

		fToolkit = managedForm.getToolkit();

		fMasterSection = new ModelMasterSection(this, fFileInfoProvider);
		fMasterSection.initialize(managedForm);
		fMasterSection.addSelectionChangedListener(this);
		fMasterSection.setInput(fModel);

		if (isInMemFile(fFileInfoProvider)) {
			fMasterSection.markDirty();
		}
	}

	private boolean isInMemFile(IFileInfoProvider fileInfoProvider) {
		if (!(fileInfoProvider instanceof ModelEditor)) {
			return false;
		}
		ModelEditor modelEditor = (ModelEditor)fileInfoProvider;
		IEditorInput input = modelEditor.getEditorInput();

		return ModelEditorHelper.isInMemFileInput(input);
	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		if (fModel == null) {
			return;
		}
		detailsPart.registerPage(RootNode.class, new ModelDetailsPage(fMasterSection, fUpdateContext, fPage.getEditor()));
		detailsPart.registerPage(ClassNode.class, new ClassDetailsPage(fMasterSection, fUpdateContext, fPage.getEditor()));
		detailsPart.registerPage(MethodNode.class, new MethodDetailsPage(fMasterSection, fUpdateContext, fPage.getEditor()));
		detailsPart.registerPage(MethodParameterNode.class, new MethodParameterDetailsPage(fMasterSection, fUpdateContext, fPage.getEditor()));
		detailsPart.registerPage(GlobalParameterNode.class, new GlobalParameterDetailsPage(fMasterSection, fUpdateContext, fPage.getEditor()));
		detailsPart.registerPage(TestCaseNode.class, new TestCaseDetailsPage(fMasterSection, fUpdateContext, fPage.getEditor()));
		detailsPart.registerPage(ConstraintNode.class, new ConstraintDetailsPage(fMasterSection, fUpdateContext, fPage.getEditor()));
		detailsPart.registerPage(ChoiceNode.class, new ChoiceDetailsPage(fMasterSection, fUpdateContext, fPage.getEditor()));

		selectNode(fModel);
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		IActionBars actionBars = getActionBars();
		IEditorSite editorSite = fPage.getEditorSite();
		IUndoContext undoContext = fUpdateContext.getUndoContext();

		fUndoActionHandler = new UndoActionHandler(editorSite, undoContext);
		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), fUndoActionHandler);

		fRedoActionHandler = new RedoActionHandler(editorSite, undoContext);
		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), fRedoActionHandler);

		actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), new GenericToolbarAction(ActionFactory.COPY.getId()));
		actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), new GenericToolbarAction(ActionFactory.CUT.getId()));
		actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), new GenericToolbarAction(ActionFactory.PASTE.getId()));
		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), new GenericToolbarAction(ActionFactory.DELETE.getId()));
		actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), new GenericToolbarAction(ActionFactory.SELECT_ALL.getId()));

		actionBars.updateActionBars();
	}

	public void refreshToolBarActions() {
		IActionBars actionBars = getActionBars();

		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), fUndoActionHandler);
		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), fRedoActionHandler);

		actionBars.updateActionBars();
	}

	private IActionBars getActionBars() {
		return fPage.getEditorSite().getActionBars();
	}

	protected BasicSection getFocusedSection(){
		if(fMasterSection.getViewer().getControl().isFocusControl()){
			return fMasterSection;
		}
		else{
			return getCurrentPage().getFocusedViewerSection();
		}
	}

	private RootNode getModel() throws ModelOperationException {
		return fPage.getModel();
	}

	@Override
	public Composite getSectionComposite() {
		return sashForm;
	}

	@Override
	public FormToolkit getToolkit() {
		return fToolkit;
	}

	public IModelUpdateContext getModelUpdateContext() {
		return fUpdateContext;
	}
}
