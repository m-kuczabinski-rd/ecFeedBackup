package com.testify.ecfeed.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import java.io.*;

import com.testify.ecfeed.constants.Constants;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.parsers.EcWriter;

public class NewEcFileWizard extends Wizard implements INewWizard {
	//TODO New File Wizard - get default container from selection
	
	private NewEcFileWizardPage page;
	private ISelection selection;

	public NewEcFileWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	public void addPages() {
		page = new NewEcFileWizardPage(selection);
		addPage(page);
	}

	public boolean performFinish() {
		final String containerName = page.getContainerName();
		final String fileName = page.getFileName();
		String modelName = fileName.substring(0, fileName.lastIndexOf("." + Constants.EQUIVALENCE_CLASS_FILE_EXTENSION));
		RootNode modelRoot = new RootNode(modelName);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		try {
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + containerName + "\" does not exist.");
		}
		IContainer container = (IContainer) resource;
		
		final IFile file = container.getFile(new Path(fileName));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			EcWriter writer = new EcWriter();
			writer.getStartDocumentStream(out);
			writer.getXmlStream(modelRoot, out);
			InputStream stream = new ByteArrayInputStream(out.toByteArray());
			if (file.exists()) {
				file.setContents(stream, true, true, null);
			} else {
				file.create(stream, true, null);
			}
			stream.close();
		} catch (IOException|CoreException e) {
		}
		return true;
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, "com.testify.ecfeed", IStatus.OK, message, null);
		throw new CoreException(status);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}