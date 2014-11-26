package com.testify.ecfeed.ui.editor;

import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;

import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.modelif.AbstractNodeInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.NodeDnDBuffer;
import com.testify.ecfeed.ui.modelif.NodeInterfaceFactory;
import com.testify.ecfeed.ui.modelif.SelectionInterface;

public class ModelNodeDropListener extends ViewerDropAdapter{

	private final IModelUpdateContext fUpdateContext;

	private class DropValidator implements IModelVisitor{

		@Override
		public Object visit(RootNode node) throws Exception {
			return NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof ClassNode;
		}

		@Override
		public Object visit(ClassNode node) throws Exception {
			return NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof MethodNode;
		}

		@Override
		public Object visit(MethodNode node) throws Exception {
			boolean result = NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof MethodParameterNode;
			result |= NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof ConstraintNode;
			result |= NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof TestCaseNode;
			return result;
		}

		@Override
		public Object visit(MethodParameterNode node) throws Exception {
			return NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof ChoiceNode;
		}

		@Override
		public Object visit(GlobalParameterNode node) throws Exception {
			return NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof ChoiceNode;
		}

		@Override
		public Object visit(TestCaseNode node) throws Exception {
			return false;
		}

		@Override
		public Object visit(ConstraintNode node) throws Exception {
			return false;
		}

		@Override
		public Object visit(ChoiceNode node) throws Exception {
			return NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof ChoiceNode;
		}

	}

	protected ModelNodeDropListener(Viewer viewer, IModelUpdateContext updateContext) {
		super(viewer);
		fUpdateContext = updateContext;
	}

	@Override
	public boolean performDrop(Object data) {
		List<AbstractNode> dragged = NodeDnDBuffer.getInstance().getDraggedNodes();
		SelectionInterface selectionIf = new SelectionInterface(fUpdateContext);
		selectionIf.setTarget(dragged);
		if((dragged.size() == 0) || (selectionIf.isSingleType() == false)) return false;
		AbstractNode newParent = determineNewParent(getCurrentTarget(), getCurrentLocation());
		int index = determineNewIndex((AbstractNode)getCurrentTarget(), getCurrentLocation());
		if(newParent == null || index < 0 || index > newParent.getMaxChildIndex(dragged.get(0))){
			return false;
		}
		switch(getCurrentOperation()){
		case DND.DROP_COPY:
			AbstractNodeInterface nodeIf = NodeInterfaceFactory.getNodeInterface(newParent, fUpdateContext);
			return nodeIf.addChildren(NodeDnDBuffer.getInstance().getDraggedNodesCopy(), index);
		case DND.DROP_MOVE:
			return selectionIf.move(newParent, index);
		default:
			return false;
		}
	}

	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {
		AbstractNode parent = determineNewParent(target, getCurrentLocation());
		SelectionInterface selectionIf = new SelectionInterface(fUpdateContext);
		List<AbstractNode>dragged = NodeDnDBuffer.getInstance().getDraggedNodes();
		selectionIf.setTarget(dragged);
		if(dragged.size() == 0) return false;
		if(selectionIf.isSingleType() == false) return false;
		try {
			return (boolean)parent.accept(new DropValidator());
		} catch (Exception e) {
			return false;
		}
	}

	protected AbstractNode determineNewParent(Object target, int location){
		int position = determineLocation(getCurrentEvent());
		if(target instanceof AbstractNode == false) return null;
		AbstractNode parent = (AbstractNode)target;
		switch(position){
		case LOCATION_ON:
			return parent;
		case LOCATION_AFTER:
		case LOCATION_BEFORE:
			return parent.getParent();
		}
		return null;
	}

	protected int determineNewIndex(AbstractNode target, int location){
		int position = determineLocation(getCurrentEvent());
		switch(position){
		case LOCATION_ON:
			return target.getMaxChildIndex(NodeDnDBuffer.getInstance().getDraggedNodes().get(0));
		case LOCATION_AFTER:
			return target.getIndex() + 1;
		case LOCATION_BEFORE:
			return target.getIndex();
		}
		return -1;
	}

}