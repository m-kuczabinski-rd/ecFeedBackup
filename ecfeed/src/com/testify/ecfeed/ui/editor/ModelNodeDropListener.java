package com.testify.ecfeed.ui.editor;

import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.IModelVisitor;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.modelif.GenericNodeInterface;
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
			boolean result = NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof CategoryNode;
			result |= NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof ConstraintNode;
			result |= NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof TestCaseNode;
			return result;
		}

		@Override
		public Object visit(CategoryNode node) throws Exception {
			return NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof PartitionNode;
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
		public Object visit(PartitionNode node) throws Exception {
			return NodeDnDBuffer.getInstance().getDraggedNodes().get(0) instanceof PartitionNode;
		}
		
	}
	
	protected ModelNodeDropListener(Viewer viewer, IModelUpdateContext updateContext) {
		super(viewer);
		fUpdateContext = updateContext;
	}

	@Override
	public boolean performDrop(Object data) {
		List<GenericNode> dragged = NodeDnDBuffer.getInstance().getDraggedNodes(); 
		SelectionInterface selectionIf = new SelectionInterface(fUpdateContext);
		selectionIf.setTarget(dragged);
		if((dragged.size() == 0) || (selectionIf.isSingleType() == false)) return false;
		GenericNode target = (GenericNode)getCurrentTarget();
		int location = getCurrentLocation();
		int index = determineNewIndex(target, location);
		if(target == null || index < 0 || index > target.getParent().getMaxChildIndex(dragged.get(0))){
			return false;
		}
		switch(getCurrentOperation()){
		case DND.DROP_COPY: 
			GenericNodeInterface nodeIf = NodeInterfaceFactory.getNodeInterface(target.getParent(), fUpdateContext);
			return nodeIf.addChildren(NodeDnDBuffer.getInstance().getDraggedNodesCopy(), index);
		case DND.DROP_MOVE:
			return selectionIf.move(target.getParent(), index);
		default:
			return false;
		}
	}

	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {
		GenericNode parent = determineNewParent(target, getCurrentLocation());
		SelectionInterface selectionIf = new SelectionInterface(fUpdateContext);
		List<GenericNode>dragged = NodeDnDBuffer.getInstance().getDraggedNodes();
		selectionIf.setTarget(dragged);
		if(dragged.size() == 0) return false;
		if(selectionIf.isSingleType() == false) return false;
		try {
			return (boolean)parent.accept(new DropValidator());
		} catch (Exception e) {
			return false;
		}
	}
	
	protected GenericNode determineNewParent(Object target, int location){
		int position = determineLocation(getCurrentEvent());
		if(target instanceof GenericNode == false) return null;
		GenericNode parent = (GenericNode)target;
		switch(position){
		case LOCATION_ON:
			return parent;
		case LOCATION_AFTER:
		case LOCATION_BEFORE:
			return parent.getParent();
		}
		return null;
	}
	
	protected int determineNewIndex(GenericNode target, int location){
		int position = determineLocation(getCurrentEvent());
		switch(position){
		case LOCATION_ON:
			return target.getParent().getMaxChildIndex(NodeDnDBuffer.getInstance().getDraggedNodes().get(0));
		case LOCATION_AFTER:
			return target.getIndex() + 1;
		case LOCATION_BEFORE:
			return target.getIndex();
		}
		return -1;
	}
	
}