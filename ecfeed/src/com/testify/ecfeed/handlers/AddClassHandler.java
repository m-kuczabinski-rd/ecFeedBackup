package com.testify.ecfeed.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.FileEditorInput;

import com.testify.ecfeed.constants.Constants;
import com.testify.ecfeed.editors.EcMultiPageEditor;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.utils.EcModelUtils;

public class AddClassHandler extends AbstractHandler implements IHandler {

    private static final IStatus OK = new Status(IStatus.OK, "com.testify.ecfeed", "");
    private static final IStatus ERROR = new Status(IStatus.ERROR, "com.testify.ecfeed", "Select file with .ect extension");

    private ISelectionStatusValidator fFileSelectionValidator = new ISelectionStatusValidator() {
        public IStatus validate(Object[] selection) {
            return selection.length == 1 && selection[0] instanceof IFile
                    && checkExtension(((IFile) selection[0]).getFileExtension()) ? OK : ERROR;
        }

		private boolean checkExtension(String fileExtension) {
			return fileExtension.equals(Constants.EQUIVALENCE_CLASS_FILE_EXTENSION);
		}
    };

    public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		IFile selectedFile = selectTargetFile(selection);
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		try {
			FileEditorInput inputFile = new FileEditorInput(selectedFile);
			EcMultiPageEditor editor = (EcMultiPageEditor)page.openEditor(inputFile, Constants.DEFAULT_ECT_EDITOR_ID);
			RootNode model = editor.getModel(); 
			if(model == null){
				throw new ExecutionException("Cannot get document model");
			}
			
			ClassNode classNode = EcModelUtils.generateClassModel((IType) selection.getFirstElement());
			if(EcModelUtils.classExists(model, classNode.getQualifiedName())){
				return null;
			}
			model.addClass(classNode);
			editor.updateModel(model);
		} catch (CoreException e) {
			System.out.println("Exception: " + e.getMessage());
		}

		return null;
	}

	private IFile selectTargetFile(IStructuredSelection selection){
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(Display.getDefault().getActiveShell(),
				new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
		dialog.setTitle("Add class to Equivalence Class model");
		dialog.setMessage("Select file with ECT model");
		dialog.setInput(getProjectRoot(selection)	);
		dialog.setValidator(fFileSelectionValidator);
		dialog.open();
		
		Object selectedFile = dialog.getFirstResult();
		return (IFile)selectedFile;
	}

	private Object getProjectRoot(IStructuredSelection selection) {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

}
