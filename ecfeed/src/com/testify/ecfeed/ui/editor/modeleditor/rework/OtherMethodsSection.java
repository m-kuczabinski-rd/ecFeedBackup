package com.testify.ecfeed.ui.editor.modeleditor.rework;

import java.util.List;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.ui.common.ModelUtils;

public class OtherMethodsSection extends CheckboxTableViewerSection {
	
	public final static int STYLE = Section.TITLE_BAR | Section.EXPANDED;
	private ClassNode fSelectedClass;
	private BasicDetailsPage fParentPage;

	private class AddSelectedAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			for(Object object : getCheckboxViewer().getCheckedElements()){
				if(object instanceof MethodNode){
					fSelectedClass.addMethod((MethodNode)object);
				}
			}
			fParentPage.modelUpdated(OtherMethodsSection.this);
		}
	}
	
	public OtherMethodsSection(Composite parent, FormToolkit toolkit, BasicDetailsPage parentPage) {
		super(parent, toolkit, STYLE, BUTTONS_BELOW);
		fParentPage = parentPage;
		
		addButton("Add selected", new AddSelectedAdapter());
	}
	
	@Override
	protected void createTableColumns() {
	}
	
	public void setInput(ClassNode classNode){
		fSelectedClass = classNode;
		List<MethodNode> notContainedMethods = ModelUtils.getNotContainedMethods(fSelectedClass, fSelectedClass.getQualifiedName());
		setText("Other methods in " + fSelectedClass.getLocalName());
		setVisible(notContainedMethods.size() > 0);
		super.setInput(notContainedMethods);
	}
	
	private void setVisible(boolean visible) {
		GridData gd = (GridData)getSection().getLayoutData();
		gd.exclude = !visible;
		getSection().setLayoutData(gd);
		getSection().setVisible(visible);
	}
	
	@Override
	protected boolean tableLinesVisible() {
		return true;
	}
	
	@Override
	protected boolean tableHeaderVisible() {
		return false;
	}

}
