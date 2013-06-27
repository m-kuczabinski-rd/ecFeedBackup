package com.testify.ecfeed.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.contentoutline.ContentOutline;

import com.testify.ecfeed.editor.EcEditor;
import com.testify.ecfeed.editor.outline.EcContentOutlinePage;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.utils.EcModelUtils;

public class AddTestClassHandler extends AbstractHandler implements IHandler {

    private static final IStatus OK = new Status(IStatus.OK, "com.testify.ecfeed", "");
    private static final IStatus ERROR = new Status(IStatus.ERROR, "com.testify.ecfeed", 
    		"Select class with methods annotated with @Test");

	
    private ISelectionStatusValidator fClassSelectionValidator = new ISelectionStatusValidator() {
        public IStatus validate(Object[] selection) {
    		if(selection.length != 1){
    			return ERROR;
    		}
    		if(selection[0] instanceof IType == false){
    			return ERROR;
    		}
    		
    		IType type = (IType)selection[0];

    		try{
    			for(IMethod method : type.getMethods()){
    				for(IAnnotation annotation : method.getAnnotations()){
    					if(annotation.getElementName().equals("Test")){
    						return OK;
    					}
    				}
    			}
    		}catch(JavaModelException e){
    			System.out.println("Class parsing error" + e.getMessage());
    		}
    		return ERROR;
        }
    };

	@Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
		RootNode model = (RootNode)selection.getFirstElement();
		IType selectedClass = selectClass(selection);

		ClassNode classNode = EcModelUtils.generateClassModel(selectedClass);
		if(EcModelUtils.classExists(model, classNode)){
			return null;
		}
		model.addClass(classNode);

		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		IPage page = ((ContentOutline)part).getCurrentPage();
		EcEditor editor = ((EcContentOutlinePage)page).getEditor();
		editor.updateModel(model);

		return null;
	}

	private IType selectClass(IStructuredSelection selection) {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(Display.getDefault().getActiveShell(),
				new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
		dialog.setTitle("Test class selection");
		dialog.setMessage("Select test class to add to the model");
		dialog.setInput(JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()));
		dialog.setValidator(fClassSelectionValidator);
		dialog.open();
		
		Object selectedType = dialog.getFirstResult();
		return (IType)selectedType;
	}

	

}
