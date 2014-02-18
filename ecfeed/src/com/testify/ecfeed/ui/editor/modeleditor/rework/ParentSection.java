package com.testify.ecfeed.ui.editor.modeleditor.rework;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

public abstract class ParentSection extends BasicSection {
	private List<BasicSection> fChildrenSections; 

	public ParentSection(Composite parent, FormToolkit toolkit, int style) {
		super(parent, toolkit, style);
		fChildrenSections = new ArrayList<BasicSection>();
	}

	protected void addChildSection(BasicSection child){
		fChildrenSections.add(child);
	}
	
	@Override
	public void refresh(){
		for(BasicSection child : fChildrenSections){
			child.refresh();
		}
	}
}
