package com.testify.ecfeed.dialogs;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class TestClassSelectionDialog extends ElementTreeSelectionDialog {

    private static final IStatus OK = new Status(IStatus.OK, "com.testify.ecfeed", "");
    private static final IStatus ERROR = new Status(IStatus.ERROR, "com.testify.ecfeed", 
    		"Select class with methods annotated with @Test");
    
	public TestClassSelectionDialog(Shell parent) {
		super(parent, new WorkbenchLabelProvider(), new ClassSelectionContentProvider());
		setTitle("Test class selection");
		setMessage("Select test class to add to the model");
		setInput(JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()));
		
		setValidator(fTestClassSelectionValidator);
	}

    private ISelectionStatusValidator fTestClassSelectionValidator = new ISelectionStatusValidator() {
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
}
