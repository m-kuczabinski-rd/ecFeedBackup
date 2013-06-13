package com.testify.ecfeed.handlers;

import java.util.Vector;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
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
import com.testify.ecfeed.editor.EcEditor;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;

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
			EcEditor editor = (EcEditor)page.openEditor(inputFile, "com.testify.ecfeed.editors.eceditor");
			RootNode model = editor.getModel(); 
			if(model == null){
				throw new ExecutionException("Cannot get document model");
			}
			
			ClassNode classNode = generateClassModel((IType) selection.getFirstElement());
			if(classExists(model, classNode)){
				return null;
			}
			model.addClass(classNode);
			editor.updateModel(model);
		} catch (CoreException e) {
			System.out.println("Exception: " + e.getMessage());
		}

		return null;
	}

	private boolean classExists(RootNode model, ClassNode classNode) {
		for(ClassNode node : model.getClasses()){
			if (node.getQualifiedName().equals(classNode.getQualifiedName())){
				return true;
			}
		}
		return false;
	}

	private ClassNode generateClassModel(IType type) throws JavaModelException {
		ClassNode classNode = new ClassNode(type.getFullyQualifiedName());
		for(IMethod method : type.getMethods()){
			IAnnotation annotation = method.getAnnotation("Test");
			if(annotation.getElementName().equals("Test")){
				classNode.addMethod(generateMethodModel(method));
			}
		}
		
		return classNode;
	}

	private MethodNode generateMethodModel(IMethod method) throws JavaModelException {
		MethodNode methodNode = new MethodNode(method.getElementName());
		for(ILocalVariable parameter : method.getParameters()){
			methodNode.addCategory(generateCategoryModel(parameter));
		}
		return methodNode;
	}

	private CategoryNode generateCategoryModel(ILocalVariable parameter) {
		String type = getTypeName(parameter.getTypeSignature());
		CategoryNode category = new CategoryNode(parameter.getElementName(), type);
		Vector<PartitionNode> defaultPartitions = generateDefaultPartitions(type);
		for(PartitionNode partition : defaultPartitions){
			category.addPartition(partition);
		}
		return category;
	}

	private String getTypeName(String typeSignature) {
		switch(typeSignature){
		case Signature.SIG_BOOLEAN:
			return "boolean";
		case Signature.SIG_BYTE:
			return "byte";
		case Signature.SIG_CHAR:
			return "char";
		case Signature.SIG_DOUBLE:
			return "double";
		case Signature.SIG_FLOAT:
			return "float";
		case Signature.SIG_INT:
			return "int";
		case Signature.SIG_LONG:
			return "long";
		case Signature.SIG_SHORT:
			return "short";
		case "QString;":
			return "String";
		default:
			return "unsupported";
		}
	}

	private Vector<PartitionNode> generateDefaultPartitions(String typeSignature) {
		switch(typeSignature){
		case "boolean":
			return defaultBooleanPartitions();
		case "byte":
			return defaultBytePartitions();
		case "char":
			return defaultCharacterPartitions();
		case "double":
			return defaultDoublePartitions();
		case "float":
			return defaultFloatPartitions();
		case "int":
			return defaultIntegerPartitions();
		case "long":
			return defaultLongPartitions();
		case "short":
			return defaultShortPartitions();
		case "String":
			return defaultStringPartitions();
		default:
			return new Vector<PartitionNode>();
		}
	}

	private Vector<PartitionNode> defaultBooleanPartitions() {
		Vector<PartitionNode> partitions = new Vector<PartitionNode>();
		partitions.add(new PartitionNode("true", true));
		partitions.add(new PartitionNode("false", false));	
		return partitions;
	}

	private Vector<PartitionNode> defaultBytePartitions() {
		Vector<PartitionNode> partitions = new Vector<PartitionNode>();
		partitions.add(new PartitionNode("min", Byte.MIN_VALUE));
		partitions.add(new PartitionNode("negative", (byte)-1));	
		partitions.add(new PartitionNode("zero", (byte)0));
		partitions.add(new PartitionNode("positive", (byte)1));	
		partitions.add(new PartitionNode("max", Byte.MAX_VALUE));
		return partitions;
	}

	private Vector<PartitionNode> defaultCharacterPartitions() {
		Vector<PartitionNode> partitions = new Vector<PartitionNode>();
		partitions.add(new PartitionNode("zero", '\u0000'));
		partitions.add(new PartitionNode("a", 'a'));
		partitions.add(new PartitionNode("z", 'z'));
		partitions.add(new PartitionNode("A", 'A'));
		partitions.add(new PartitionNode("Z", 'Z'));
		partitions.add(new PartitionNode("non ASCII", '\u00A7'));
		partitions.add(new PartitionNode("max", '\uffff'));
		return partitions;
	}

	private Vector<PartitionNode> defaultDoublePartitions() {
		Vector<PartitionNode> partitions = new Vector<PartitionNode>();
		partitions.add(new PartitionNode("min", Double.MIN_VALUE));
		partitions.add(new PartitionNode("negative", (double)-1));	
		partitions.add(new PartitionNode("zero", (double)0));
		partitions.add(new PartitionNode("positive", (double)1));	
		partitions.add(new PartitionNode("max", Double.MAX_VALUE));
		return partitions;
	}

	private Vector<PartitionNode> defaultFloatPartitions() {
		Vector<PartitionNode> partitions = new Vector<PartitionNode>();
		partitions.add(new PartitionNode("min", Float.MIN_VALUE));
		partitions.add(new PartitionNode("negative", (float)-1));	
		partitions.add(new PartitionNode("zero", (float)0));
		partitions.add(new PartitionNode("positive", (float)1));	
		partitions.add(new PartitionNode("max", Float.MAX_VALUE));
		return partitions;
	}

	private Vector<PartitionNode> defaultIntegerPartitions() {
		Vector<PartitionNode> partitions = new Vector<PartitionNode>();
		partitions.add(new PartitionNode("min", Integer.MIN_VALUE));
		partitions.add(new PartitionNode("negative", (int)-1));	
		partitions.add(new PartitionNode("zero", (int)0));
		partitions.add(new PartitionNode("positive", (int)1));	
		partitions.add(new PartitionNode("max", Integer.MAX_VALUE));
		return partitions;
	}

	private Vector<PartitionNode> defaultLongPartitions() {
		Vector<PartitionNode> partitions = new Vector<PartitionNode>();
		partitions.add(new PartitionNode("min", Long.MIN_VALUE));
		partitions.add(new PartitionNode("negative", (long)-1));	
		partitions.add(new PartitionNode("zero", (long)0));
		partitions.add(new PartitionNode("positive", (long)1));	
		partitions.add(new PartitionNode("max", Long.MAX_VALUE));
		return partitions;
	}

	private Vector<PartitionNode> defaultShortPartitions() {
		Vector<PartitionNode> partitions = new Vector<PartitionNode>();
		partitions.add(new PartitionNode("min", Short.MIN_VALUE));
		partitions.add(new PartitionNode("negative", (short)-1));	
		partitions.add(new PartitionNode("zero", (short)0));
		partitions.add(new PartitionNode("positive", (short)1));	
		partitions.add(new PartitionNode("max", Short.MAX_VALUE));
		return partitions;
	}

	private Vector<PartitionNode> defaultStringPartitions() {
		Vector<PartitionNode> partitions = new Vector<PartitionNode>();
		partitions.add(new PartitionNode("null", null));
		partitions.add(new PartitionNode("empty", ""));
		partitions.add(new PartitionNode("lower case", "a"));
		partitions.add(new PartitionNode("upper case", "A"));
		partitions.add(new PartitionNode("mixed cases", "aA"));
		partitions.add(new PartitionNode("all latin", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
		return partitions;
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
