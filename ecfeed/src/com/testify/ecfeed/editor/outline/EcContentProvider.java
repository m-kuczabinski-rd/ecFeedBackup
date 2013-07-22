package com.testify.ecfeed.editor.outline;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeNodeContentProvider;

import com.testify.ecfeed.editor.EcMultiPageEditor;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.RootNode;

public class EcContentProvider extends TreeNodeContentProvider implements ITreeContentProvider {

	public static final Object[] EMPTY_ARRAY = new Object[]{};
	
	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof EcMultiPageEditor){
			RootNode root = ((EcMultiPageEditor)inputElement).getModel(); 
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
