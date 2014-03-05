package com.testify.ecfeed.ui.editor.modeleditor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;

import com.testify.ecfeed.model.PartitionNode;

public class PartitionDetailsPage extends BasicDetailsPage {

	private PartitionNode fSelectedPartition;
	private PartitionChildrenViewer fPartitionChildren;
	private PartitionLabelsViewer fLabelsViewer;
	private Text fPartitionNameText;
	private Text fPartitionValueText;

	public PartitionDetailsPage(ModelMasterSection masterSection) {
		super(masterSection);
	}
	
	@Override
	public void createContents(Composite parent){
		super.createContents(parent);

		createNameValueEdit(getMainComposite());
		addForm(fPartitionChildren = new PartitionChildrenViewer(this, getToolkit()));
		addForm(fLabelsViewer = new PartitionLabelsViewer(this, getToolkit()));
		
		getToolkit().paintBordersFor(getMainComposite());
	}
	
	private void createNameValueEdit(Composite parent) {
		Composite composite = getToolkit().createComposite(parent);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		createNameEdit(composite);
		createValueEdit(composite);
	}

	private void createNameEdit(Composite parent) {
		getToolkit().createLabel(parent, "Name");
		fPartitionNameText = getToolkit().createText(parent, "", SWT.NONE);
		fPartitionNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fPartitionNameText.addListener(SWT.KeyDown, new Listener() {
			public void handleEvent(Event event) {
				if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
					if(applyNewPartitionName()){
						modelUpdated(null);
					}
				}
			}
		});
		Composite buttonComposite = getToolkit().createComposite(parent);
		buttonComposite.setLayout(new GridLayout(1, false));
		buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 2));
		Button applyButton = getToolkit().createButton(buttonComposite, "Change", SWT.CENTER);
		applyButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				boolean updated = false;
				updated |= applyNewPartitionName();
				updated |= applyNewPartitionValue();
				if(updated){
					modelUpdated(null);
				}
			}
		});
		getToolkit().paintBordersFor(parent);

	}

	private boolean applyNewPartitionName() {
		String newName = fPartitionNameText.getText(); 
		if(newName.equals(fSelectedPartition.getName()) == false){
			if(fSelectedPartition.getCategory().validatePartitionName(newName)){
				fSelectedPartition.setName(newName);
				return true;
			}
			else{
				fPartitionNameText.setText(fSelectedPartition.getName());
			}
		}
		return false;
	}

	private void createValueEdit(Composite parent) {
		getToolkit().createLabel(parent, "Value");
		fPartitionValueText = getToolkit().createText(parent, "", SWT.NONE);
		fPartitionValueText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fPartitionValueText.addListener(SWT.KeyDown, new Listener() {
			public void handleEvent(Event event) {
				if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
					if(applyNewPartitionValue()){
						modelUpdated(null);
					}
				}
			}
		});
		getToolkit().paintBordersFor(parent);
	}

	private boolean applyNewPartitionValue() {
		String newValue = fPartitionValueText.getText(); 
		if(newValue.equals(fSelectedPartition.getValueString()) == false){
			if(fSelectedPartition.getCategory().validatePartitionStringValue(newValue)){
				Object value = fSelectedPartition.getCategory().getPartitionValueFromString(newValue);
				fSelectedPartition.setValue(value);
				return true;
			}
			else{
				fPartitionValueText.setText(fSelectedPartition.getValueString());
			}
		}
		return false;
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		super.selectionChanged(part, selection);
		if(getSelectedElement() instanceof PartitionNode){
			fSelectedPartition = (PartitionNode)getSelectedElement();
		}
		refresh();
	}
	
	@Override
	public void refresh(){
		super.refresh();
		if(fSelectedPartition != null){
			getMainSection().setText(fSelectedPartition.toString());
			fPartitionChildren.setInput(fSelectedPartition);
			fLabelsViewer.setInput(fSelectedPartition);
			fPartitionNameText.setText(fSelectedPartition.getName());
			if(fSelectedPartition.isAbstract()){
				fPartitionValueText.setEnabled(false);
				fPartitionValueText.setText("");
			}
			else{
				fPartitionValueText.setEnabled(true);
				fPartitionValueText.setText(fSelectedPartition.getValueString());
			}
		}
	}

}
