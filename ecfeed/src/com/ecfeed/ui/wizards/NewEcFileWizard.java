/*******************************************************************************
 *
 * Copyright (c) 2016 ecFeed AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html 
 *  
 *******************************************************************************/

package com.ecfeed.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.part.FileEditorInput;

import com.ecfeed.core.model.ModelVersionDistributor;
import com.ecfeed.core.model.RootNode;
import com.ecfeed.core.serialization.ect.EctSerializer;
import com.ecfeed.core.utils.DiskFileHelper;
import com.ecfeed.core.utils.StringHelper;
import com.ecfeed.ui.common.Constants;
import com.ecfeed.ui.common.Messages;

class NewEctFileCreationPage extends WizardNewFileCreationPage {

	public NewEctFileCreationPage(String pageName,
			IStructuredSelection selection) {
		super(pageName, selection);
	}

	@Override
	protected boolean validatePage() {
		if (!super.validatePage()) {
			return false;
		}

		String errorMessage = DiskFileHelper.checkEctFileName(getFileName());
		if (!StringHelper.isNullOrEmpty(errorMessage)) {
			return false;
		}
		return true;
	}
}

public class NewEcFileWizard extends Wizard implements INewWizard {

	private NewEctFileCreationPage fPage;
	private IStructuredSelection fSelection;

	public NewEcFileWizard() {
		super();
		setNeedsProgressMonitor(false);
		setHelpAvailable(false);
	}

	public void addPages() {
		fPage = new NewEctFileCreationPage(Messages.WIZARD_NEW_ECT_FILE_TITLE, fSelection);
		fPage.setFileName(Constants.DEFAULT_NEW_ECT_FILE_NAME);
		fPage.setAllowExistingResources(true);
		fPage.setFileExtension(Constants.EQUIVALENCE_CLASS_FILE_EXTENSION);
		fPage.setTitle(Messages.WIZARD_NEW_ECT_FILE_TITLE);
		fPage.setDescription(Messages.WIZARD_NEW_ECT_FILE_MESSAGE);

		addPage(fPage);
	}

	public boolean performFinish(){
		IFile file = fPage.createNewFile();
		try {
			if(file.getContents().read() != -1){
				MessageDialog dialog = new MessageDialog(getShell(), 
						Messages.WIZARD_FILE_EXISTS_TITLE, 
						Display.getDefault().getSystemImage(SWT.ICON_QUESTION), 
						Messages.WIZARD_FILE_EXISTS_MESSAGE,
						MessageDialog.QUESTION_WITH_CANCEL, 
						new String[] {IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL}, 
						IDialogConstants.OK_ID);
				if(dialog.open() != IDialogConstants.OK_ID){
					return false;
				}
			}


			final IPath newFileFullPath = fPage.getContainerFullPath().append(fPage.getFileName()); 
			String modelName = newFileFullPath.removeFileExtension().lastSegment();
			RootNode model = new RootNode(
					modelName != null ? modelName : Constants.DEFAULT_NEW_ECT_MODEL_NAME, 
							ModelVersionDistributor.getCurrentVersion());

			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			new EctSerializer(ostream, ModelVersionDistributor.getCurrentVersion()).serialize(model);
			ByteArrayInputStream istream = new ByteArrayInputStream(ostream.toByteArray());
			file.setContents(istream, true, true, null);

			//open new file in an ect editor
			IWorkbenchPage page =  PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

			page.openEditor(new FileEditorInput(file), Constants.ECT_EDITOR_ID);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.fSelection = selection;
	}
}
