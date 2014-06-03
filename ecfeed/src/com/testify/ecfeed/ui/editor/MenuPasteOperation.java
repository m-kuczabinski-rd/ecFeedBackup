package com.testify.ecfeed.ui.editor;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.IGenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;

public class MenuPasteOperation extends MenuOperation{
	protected IGenericNode fSource;
	protected IGenericNode fTarget;
	protected ModelMasterSection fModel;
	protected final String DIALOG_OPERATION_FAILED_TITLE = "Paste failed";
	protected final String DIALOG_OPERATION_FAILED_MESSAGE = "Clipboard content doesn't match here.";

	public MenuPasteOperation(IGenericNode target, IGenericNode source, ModelMasterSection model){
		super("Paste");
		fSource = source;
		fTarget = target;
		fModel = model;
	}

	@Override
	public void execute(){
		if(!paste()){
			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), DIALOG_OPERATION_FAILED_TITLE,
					DIALOG_OPERATION_FAILED_MESSAGE);
		} else{
			fModel.markDirty();
			fModel.refresh();
		}
	}

	public void createUniqueName(GenericNode source, GenericNode target){
		/*
		 * TODO change for one generic approach. If I recall correctly getChildren search only through first level descendants.
		 */
//		if(target instanceof IPartitionedNode && source instanceof IPartitionedNode){
//			IPartitionedNode partitionedTarget = (IPartitionedNode)target;
//			while(partitionedTarget.getPartition(source.getName()) != null){
//				source.setName(source.getName() + "1");
//			}
//		} else if(target instanceof MethodNode && source instanceof CategoryNode){
//			MethodNode method = (MethodNode)target;
//			while(method.getCategory(source.getName()) != null){
//				source.setName(source.getName() + "1");
//			}
//		} else if(target instanceof ClassNode && source instanceof MethodNode){
//			ClassNode clazz = (ClassNode)target;
//			MethodNode method = (MethodNode)source;
//			while(clazz.getMethod(method.getName(), method.getCategoriesTypes()) != null){
//				source.setName(source.getName() + "1");
//			}
//		} else if(target instanceof RootNode && source instanceof ClassNode){
//			RootNode root = (RootNode)target;
//			ClassNode clazz = (ClassNode)source;
//			while(root.getClassModel(clazz.getName()) != null){
//				source.setName(source.getName() + "1");
//			}
//		}
		String name = source.getName();
		while(target.getChild(name) != null){
			name += "1";
		}
		source.setName(name);
	}

	public boolean paste(){
		if(fSource != null && fTarget != null){
			if(fTarget instanceof PartitionNode){
				PartitionNode target = (PartitionNode)fTarget;
				if(fSource instanceof PartitionNode){
					PartitionNode source = (PartitionNode)fSource;
					if(target.getCategory().getType().equals(source.getCategory().getType())){
						createUniqueName(source, target);
						target.addPartition(source);
						return true;
					}
				}
			} else if(fTarget instanceof CategoryNode){
				CategoryNode target = (CategoryNode)fTarget;
				if(!target.isExpected()){
					if(fSource instanceof PartitionNode){
						PartitionNode source = (PartitionNode)fSource;
						if(target.getType().equals(source.getCategory().getType())){
							createUniqueName(source, target);
							target.addPartition(source);
							return true;
						}
					}
				}
			} else if(fTarget instanceof MethodNode){
				MethodNode target = (MethodNode)fTarget;
				if(fSource instanceof CategoryNode){
					CategoryNode source = (CategoryNode)fSource;
					createUniqueName(source, target);
					target.addCategory(source);
					/*
					 * TODO should we remove test cases upon adding new category?
					 */
					return true;
				} else if(fSource instanceof ConstraintNode){
					ConstraintNode source = (ConstraintNode)fSource;
					if(source.updateReferences(target)){
						target.addConstraint(source);
						return true;
					}
				} else if(fSource instanceof TestCaseNode){
					TestCaseNode source = (TestCaseNode)fSource.getCopy();
					if(source.updateReferences(target)){
						target.addTestCase(source);
						return true;
					}
				}
			} else if(fTarget instanceof ClassNode){
				ClassNode target = (ClassNode)fTarget;
				if(fSource instanceof MethodNode){
					MethodNode source = (MethodNode)fSource;
					createUniqueName(source, target);
					target.addMethod(source);
					return true;
				}
			} else if(fTarget instanceof RootNode){
				RootNode target = (RootNode)fTarget;
				if(fSource instanceof ClassNode){
					ClassNode source = (ClassNode)fSource;
					createUniqueName(source, target);
					target.addClass(source);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isEnabled(){
		return (fSource != null);
	}
}