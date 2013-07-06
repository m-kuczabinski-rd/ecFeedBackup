package com.testify.ecfeed.dialogs;

import java.util.Vector;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;

public class ClassSelectionContentProvider extends StandardJavaElementContentProvider {

	public ClassSelectionContentProvider() {
		super(true);
	}
	
	@Override
	public Object[] getChildren(Object element){
		Vector<Object> children = new Vector<Object>();
		
		//Filter unwanted elements
		for(Object child : super.getChildren(element)){
			if((child instanceof IType == false) && (hasChildren(child) == false)){
				continue;
			}
			children.add(child);
		}
		return children.toArray();
	}
}
