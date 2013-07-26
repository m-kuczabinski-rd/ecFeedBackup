package com.testify.ecfeed.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import com.testify.ecfeed.constants.Constants;
import com.testify.ecfeed.constants.DialogStrings;
/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one ({@link Constants.DEFAULT_FILE_EXTENSION}).
 */

public class NewEcFileWizardPage extends WizardPage {
	private Text fContainerText;
	private Text fFileNameText;
	private ISelection fSelection;
	private IContainer fInitialContainer;

	public NewEcFileWizardPage(ISelection selection) {
		super("NewEcFileWizardPage");
		setTitle(DialogStrings.WIZARD_NEW_ECT_FILE_TITLE);
		setDescription(DialogStrings.WIZARD_NEW_ECT_FILE_MESSAGE);
		this.fSelection = selection;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;

		Label containerLabel = new Label(container, SWT.NULL);
		containerLabel.setText("Container:");

		fContainerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		fContainerText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fContainerText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		Button browseButton = new Button(container, SWT.PUSH);
		browseButton.setText("Browse...");
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
		
		Label fileNameLabel = new Label(container, SWT.NULL);
		fileNameLabel.setText("File name:");

		fFileNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		fFileNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fFileNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		
		initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */
	private void initialize() {
		if (fSelection != null && fSelection.isEmpty() == false
				&& fSelection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) fSelection;
			fInitialContainer = null;
			if (ssel.size() > 1)
				return;
			Object obj = ssel.getFirstElement();
			if(obj instanceof IJavaElement){
				try {
					IResource resource = ((IJavaElement)obj).getCorrespondingResource();
					if (resource instanceof IContainer){
						fInitialContainer = (IContainer) resource;
					}
					else{
						fInitialContainer = (IContainer)resource.getParent();
					}
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
			}
		}
		if(fInitialContainer != null){
			fContainerText.setText(fInitialContainer.getFullPath().toString());
		}
		fFileNameText.setText(Constants.DEFAULT_NEW_ECT_MODEL_NAME + "." + Constants.EQUIVALENCE_CLASS_FILE_EXTENSION);
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */

	private void handleBrowse() {
		IContainer container = fInitialContainer != null ? fInitialContainer : ResourcesPlugin.getWorkspace().getRoot();
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				getShell(), container, false,
				DialogStrings.DIALOG_SELECT_CONTAINER_FOR_NEW_ECT_FILE_TITLE);
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				fContainerText.setText(((Path) result[0]).toString());
			}
		}
	}

	/**
	 * Ensures that both text fields are set correctly.
	 */

	private void dialogChanged() {
		IResource container = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(new Path(getContainerName()));
		String fileName = getFileName();

		if (getContainerName().length() == 0) {
			updateStatus(DialogStrings.WIZARD_UNSPECIFIED_CONTAINER_MESAGE);
			return;
		}
		if (container == null
				|| (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updateStatus(DialogStrings.WIZARD_CONTAINER_DOES_NOT_EXIST_MESAGE);
			return;
		}
		if (!container.isAccessible()) {
			updateStatus(DialogStrings.WIZARD_CONTAINER_NOT_ACCESSIBLE);
			return;
		}
		if (fileName.length() == 0) {
			updateStatus(DialogStrings.WIZARD_FILE_NAME_NOT_SPECIFIED);
			return;
		}
		if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
			updateStatus(DialogStrings.WIZARD_WRONG_ECT_FILE_NAME);
			return;
		}
		int dotLoc = fileName.lastIndexOf('.');
		if (dotLoc != -1) {
			String ext = fileName.substring(dotLoc + 1);
			if (ext.equalsIgnoreCase(Constants.EQUIVALENCE_CLASS_FILE_EXTENSION) == false) {
				updateStatus(DialogStrings.WIZARD_WRONG_ECT_FILE_EXTENSION);
				return;
			}
		}
		else{
			updateStatus(DialogStrings.WIZARD_WRONG_ECT_FILE_EXTENSION);
			return;
		}
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getContainerName() {
		return fContainerText.getText();
	}

	public String getFileName() {
		return fFileNameText.getText();
	}
}