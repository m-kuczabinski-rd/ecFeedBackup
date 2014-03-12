package com.testify.ecfeed.ui.editor.modeleditor;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.constraint.BasicStatement;
import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.model.constraint.StaticStatement;
import com.testify.ecfeed.ui.common.Constants;
import com.testify.ecfeed.ui.common.Messages;

public class ConstraintsListViewer extends CheckboxTableViewerSection {
	
	private final static int STYLE = Section.TITLE_BAR | Section.EXPANDED;

	private MethodNode fSelectedMethod;

	private class AddConstraintAdapter extends SelectionAdapter{
		@Override 
		public void widgetSelected(SelectionEvent e){
			String name = Constants.DEFAULT_CONSTRAINT_NAME;
			BasicStatement premise = new StaticStatement(true);
			BasicStatement consequence = new StaticStatement(true);
			fSelectedMethod.addConstraint(new ConstraintNode(name, new Constraint(premise, consequence)));
			modelUpdated();
		}
	}
	
	private class RemoveSelectedConstraintsAdapter extends SelectionAdapter{
		@Override 
		public void widgetSelected(SelectionEvent e){
			if(MessageDialog.openConfirm(getActiveShell(), 
					Messages.DIALOG_REMOVE_CONSTRAINTS_TITLE, 
					Messages.DIALOG_REMOVE_CONSTRAINTS_MESSAGE)){
				for(Object constraint : getCheckboxViewer().getCheckedElements()){
					fSelectedMethod.removeConstraint((ConstraintNode)constraint);
				}
				modelUpdated();
			}
		}
	}

	public ConstraintsListViewer(BasicDetailsPage parent, FormToolkit toolkit) {
		super(parent.getMainComposite(), toolkit, STYLE, parent);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.minimumHeight = 250;
		getSection().setLayoutData(gd);
		
		getSection().setText("Constraints");
		
		addButton("Add constraint", new AddConstraintAdapter());
		addButton("Remove selected", new RemoveSelectedConstraintsAdapter());
		
		addDoubleClickListener(new SelectNodeDoubleClickListener(parent.getMasterSection()));
	}

	@Override
	protected void createTableColumns() {
		addColumn("Name", 150, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((ConstraintNode)element).getName();
			}
		});
		
		addColumn("Definition", 150, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((ConstraintNode)element).getConstraint().toString();
			}
		});
	}
	
	public void setInput(MethodNode method){
		fSelectedMethod = method;
		super.setInput(method.getConstraintNodes());
		refresh();
	}
}
