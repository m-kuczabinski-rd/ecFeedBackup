package com.testify.ecfeed.wizards;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
import org.eclipse.core.runtime.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

import com.testify.ecfeed.constants.Constants;
import com.testify.ecfeed.constants.DialogStrings;
import com.testify.ecfeed.editor.EcMultiPageEditor;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.parsers.EcWriter;

public class NewEcFileWizard extends Wizard implements INewWizard {
	
	private WizardNewFileCreationPage fPage;
	private IStructuredSelection fSelection;

	public NewEcFileWizard() {
		super();
		setNeedsProgressMonitor(false);
		setHelpAvailable(false);
	}
	
	public void addPages() {
		fPage = new WizardNewFileCreationPage(DialogStrings.WIZARD_NEW_ECT_FILE_TITLE, fSelection);
		fPage.setFileName(Constants.DEFAULT_NEW_ECT_FILE_NAME);
		fPage.setAllowExistingResources(true);
		fPage.setMessage(DialogStrings.WIZARD_NEW_ECT_FILE_MESSAGE);
		fPage.setFileExtension(Constants.EQUIVALENCE_CLASS_FILE_EXTENSION);

		addPage(fPage);
	}

	public boolean performFinish(){
		IFile file = fPage.createNewFile();
		try {

			if(file.getContents().read() != -1){
				MessageDialog dialog = new MessageDialog(getShell(), 
						DialogStrings.WIZARD_FILE_EXISTS_TITLE, 
						Display.getDefault().getSystemImage(SWT.ICON_QUESTION), 
						DialogStrings.WIZARD_FILE_EXISTS_MESSAGE,
						MessageDialog.QUESTION_WITH_CANCEL, 
						new String[] {IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL}, 
						IDialogConstants.OK_ID);
				if(dialog.open() != IDialogConstants.OK_ID){
					return false;
				}
			}


			final IPath newFileFullPath = fPage.getContainerFullPath().append(fPage.getFileName()); 
			String modelName = newFileFullPath.removeFileExtension().lastSegment();
			RootNode model = new RootNode(modelName != null ? modelName : Constants.DEFAULT_NEW_ECT_MODEL_NAME);
			ByteArrayOutputStream ostream = new ByteArrayOutputStream();
			EcWriter writer = new EcWriter(ostream);
			writer.writeXmlDocument(model);
			ByteArrayInputStream istream = new ByteArrayInputStream(ostream.toByteArray());
			file.setContents(istream, true, true, null);

			//open new file in an ect editor
			IWorkbenchPage page =  PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

			page.openEditor(new FileEditorInput(file), EcMultiPageEditor.ID);
		} catch (CoreException | IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.fSelection = selection;
	}
}