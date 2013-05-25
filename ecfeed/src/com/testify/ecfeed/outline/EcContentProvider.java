package com.testify.ecfeed.outline;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.testify.ecfeed.editors.EcEditor;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.RootNode;

public class EcContentProvider implements ITreeContentProvider {

	public static final Object[] EMPTY_ARRAY = new Object[]{};
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof EcEditor){
			RootNode root = ((EcEditor)inputElement).getModel(); 
			return new Object[]{root};
		}
		else if(inputElement instanceof GenericNode){
			return ((GenericNode)inputElement).getChildren().toArray();
		}
		return EMPTY_ARRAY;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof GenericNode){
			return ((GenericNode)parentElement).getChildren().toArray();
		}
		return EMPTY_ARRAY;
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof GenericNode){
			return ((GenericNode)element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof GenericNode){
			return ((GenericNode)element).hasChildren();
		}
		return false;
	}

}
