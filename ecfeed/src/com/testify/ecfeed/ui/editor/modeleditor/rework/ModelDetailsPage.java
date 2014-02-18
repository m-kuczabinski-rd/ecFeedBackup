package com.testify.ecfeed.ui.editor.modeleditor.rework;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

public class ModelDetailsPage extends BasicDetailsPage {

	public ModelDetailsPage(ModelMasterSection masterSection) {
		super(masterSection);
	}

	private static final int STYLE = Section.EXPANDED | Section.TITLE_BAR;

	@Override
	protected BasicSection createMainSection(Composite parent) {
		ClassViewerSection classesSection = new ClassViewerSection(parent, getToolkit(), STYLE);
		classesSection.getViewer().setContentProvider(new ArrayContentProvider());
		return classesSection;
	}
	
	@Override
	public void createContents(Composite parent) {
		super.createContents(parent);
		getClassesSection().setInput(getModel().getClasses());
	}
	
	@Override
	public void refresh(){
		super.refresh();
		getMainSection().setText(getModel().getName());
	}
	
	protected ClassViewerSection getClassesSection(){
		return (ClassViewerSection)getMainSection();
	}
}
