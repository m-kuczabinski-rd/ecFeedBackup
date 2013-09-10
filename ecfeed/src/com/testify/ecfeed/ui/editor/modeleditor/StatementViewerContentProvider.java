package com.testify.ecfeed.ui.editor.modeleditor;

import java.util.Vector;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeNodeContentProvider;

import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.model.constraint.StatementArray;

public class StatementViewerContentProvider extends TreeNodeContentProvider implements ITreeContentProvider {
	public static final Object[] EMPTY_ARRAY = new Object[]{};

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof Constraint){
			Constraint constraint = (Constraint)inputElement;
			return new Object[]{constraint.getPremise(), constraint.getConsequence()};
		}
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof BasicStatement){
			return ((BasicStatement)parentElement).getChildren().toArray();
		}
		return EMPTY_ARRAY;
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof BasicStatement){
			return ((BasicStatement)element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof StatementArray){
			StatementArray statementArray = (StatementArray)element;
			Vector<BasicStatement> children = statementArray.getChildren();
			return (children.size() > 0);
		}
		return false;
	}

}
