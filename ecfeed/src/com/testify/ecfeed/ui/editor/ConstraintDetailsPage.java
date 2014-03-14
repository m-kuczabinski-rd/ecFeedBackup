package com.testify.ecfeed.ui.editor;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.testify.ecfeed.model.ConstraintNode;

public class ConstraintDetailsPage extends BasicDetailsPage {

	private ConstraintNode fSelectedConstraint;
	private Combo fNameCombo;
	private ConstraintViewer fConstraintViewer;
	
	private class RenameConstraintAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			applyConstraintName(fSelectedConstraint, fNameCombo);
		}

		protected void applyConstraintName(ConstraintNode constraint, Combo nameCombo) {
			String newName = nameCombo.getText();
			if(constraint.getMethod().validateConstraintName(newName) && 
					newName.equals(constraint.getName()) == false){
				constraint.setName(newName);
				modelUpdated(null);
			}
			else{
				nameCombo.setText(constraint.getName());
			}
		}
	}

	private class ConstraintNameListener extends RenameConstraintAdapter implements Listener{
		@Override
		public void handleEvent(Event event) {
			if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
				applyConstraintName(fSelectedConstraint, fNameCombo);
			}
		}
	}
	
	public ConstraintDetailsPage(ModelMasterSection masterSection) {
		super(masterSection);
	}
	
	@Override
	public void createContents(Composite parent){
		super.createContents(parent);
		createConstraintNameEdit(getMainComposite());
		addForm(fConstraintViewer = new ConstraintViewer(this, getToolkit()));
	}
	
	private void createConstraintNameEdit(Composite parent) {
		Composite composite = getToolkit().createComposite(parent);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		getToolkit().createLabel(composite, "Constraint name:");
		ComboViewer nameComboViewer = new ComboViewer(composite, SWT.NONE);
		fNameCombo = nameComboViewer.getCombo();
		fNameCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fNameCombo.addListener(SWT.KeyDown, new ConstraintNameListener());
		fNameCombo.addSelectionListener(new RenameConstraintAdapter());
		Button button = getToolkit().createButton(composite, "Change", SWT.NONE);
		button.addSelectionListener(new RenameConstraintAdapter());
	}

	@Override
	public void refresh(){
		if(getSelectedElement() instanceof ConstraintNode){
			fSelectedConstraint = (ConstraintNode)getSelectedElement();
		}
		if(fSelectedConstraint != null){
			getMainSection().setText(fSelectedConstraint.toString());
			fNameCombo.setItems(fSelectedConstraint.getMethod().getConstraintsNames().toArray(new String[]{}));
			fNameCombo.setText(fSelectedConstraint.getName());
			fConstraintViewer.setInput(fSelectedConstraint);
		}
	}


}
