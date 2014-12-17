package com.testify.ecfeed.ui.editor.actions;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.PartInitException;

import com.testify.ecfeed.adapter.EImplementationStatus;
import com.testify.ecfeed.adapter.IImplementationStatusResolver;
import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.common.EclipseImplementationStatusResolver;
import com.testify.ecfeed.ui.common.IFileInfoProvider;
import com.testify.ecfeed.ui.common.JavaModelAnalyser;
import com.testify.ecfeed.ui.modelif.AbstractNodeInterface;
import com.testify.ecfeed.ui.modelif.NodeInterfaceFactory;

public class GoToImplementationAction extends ModelSelectionAction {

//	private class GoToActionEnabled implements IModelVisitor{
//
//		@Override
//		public Object visit(MethodParameterNode node) throws Exception {
//			return JavaUtils.isUserType(node.getType());
//		}
//
//		@Override
//		public Object visit(GlobalParameterNode node) throws Exception {
//			return JavaUtils.isUserType(node.getType());
//		}
//
//		@Override
//		public Object visit(RootNode node) throws Exception {
//			return false;
//		}
//
//		@Override
//		public Object visit(ClassNode node) throws Exception {
//			return true;
//		}
//
//		@Override
//		public Object visit(MethodNode node) throws Exception {
//			return true;
//		}
//
//		@Override
//		public Object visit(TestCaseNode node) throws Exception {
//			return false;
//		}
//
//		@Override
//		public Object visit(ConstraintNode node) throws Exception {
//			return false;
//		}
//
//		@Override
//		public Object visit(ChoiceNode node) throws Exception {
//			return JavaUtils.isUserType(node.getParameter().getType()) && node.isAbstract() == false;
//		}
//
//	}

	private class GoToImplementationHandler implements IModelVisitor{

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return goToParameter(node);
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return goToParameter(node);
		}

		@Override
		public Object visit(RootNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			IType type = new JavaModelAnalyser().getIType(node.getName());
			if(type == null){
				return null;
			}
			JavaUI.openInEditor(type);
			return null;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			IMethod method = new JavaModelAnalyser().getIMethod(node);
			if(method != null){
				JavaUI.openInEditor(method);
			}
			return null;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return null;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			IType type = new JavaModelAnalyser().getIType(node.getParameter().getType());
			if(type == null || node.isAbstract()){
				return null;
			}
			for(IField field : type.getFields()){
				if(field.getElementName().equals(node.getValueString())){
					JavaUI.openInEditor(field);
					break;
				}
			}
			return null;
		}

		private Object goToParameter(AbstractParameterNode node) throws JavaModelException, PartInitException {
			if(JavaUtils.isUserType(node.getType()) == false){
				return null;
			}
			IType type = new JavaModelAnalyser().getIType(node.getType());
			if(type == null){
				return null;
			}
			JavaUI.openInEditor(type);
			return null;
		}

	}

	private IImplementationStatusResolver fStatusResolver;

	public GoToImplementationAction(ISelectionProvider selectionProvider, IFileInfoProvider fileInfoProvider) {
		super("goToImpl", "Go to implementation", selectionProvider);
		fStatusResolver = new EclipseImplementationStatusResolver();
	}

	@Override
	public void run(){
		if(getSelectedNodes().size() != 1){
			return;
		}
		AbstractNode node = getSelectedNodes().get(0);
		try{
			node.accept(new GoToImplementationHandler());
		}catch(Exception e){}
	}

	@Override
	public boolean isEnabled(){
		if(getSelectedNodes().size() != 1){
			return false;
		}
		AbstractNode node = getSelectedNodes().get(0);
		if(fStatusResolver.getImplementationStatus(node) == EImplementationStatus.NOT_IMPLEMENTED){
			return false;
		}
		AbstractNodeInterface nodeIf = NodeInterfaceFactory.getNodeInterface(node, null);
		return nodeIf.goToImplementationEnabled();
	}

}
