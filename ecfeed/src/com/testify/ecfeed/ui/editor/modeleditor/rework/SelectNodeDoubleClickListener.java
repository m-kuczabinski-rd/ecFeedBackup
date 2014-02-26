package com.testify.ecfeed.ui.editor.modeleditor.rework;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;

public class SelectNodeDoubleClickListener implements IDoubleClickListener {

	private ModelMasterSection fMasterSection;

	public SelectNodeDoubleClickListener(ModelMasterSection masterSection){
		fMasterSection = masterSection;
	}
	
	@Override
	public void doubleClick(DoubleClickEvent event) {
		if(event.getSelection() instanceof IStructuredSelection){
			IStructuredSelection selection = (IStructuredSelection)event.getSelection();
			fMasterSection.selectElement(selection.getFirstElement());
		}
	}
}
