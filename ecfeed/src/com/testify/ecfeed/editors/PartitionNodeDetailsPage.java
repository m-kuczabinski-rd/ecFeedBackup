package com.testify.ecfeed.editors;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.constants.DialogStrings;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.utils.EcModelUtils;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;

public class PartitionNodeDetailsPage extends GenericNodeDetailsPage{

	private PartitionNode fSelectedNode;
	private Section fMainSection;
	private Text fPartitionNameText;
	private Text fPartitionValueText;
	
	/**
	 * Create the details page.
	 */
	public PartitionNodeDetailsPage(ModelMasterDetailsBlock parentBlock) {
		super(parentBlock);
	}

	/**
	 * Create contents of the details page.
	 * @param parent
	 */
	public void createContents(Composite parent) {
		parent.setLayout(new FillLayout());
		fMainSection = fToolkit.createSection(parent,
				ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR);

		Composite mainComposite = fToolkit.createComposite(fMainSection, SWT.NONE);
		mainComposite.setLayout(new GridLayout(3, false));
		fToolkit.paintBordersFor(mainComposite);
		fMainSection.setClient(mainComposite);

		fToolkit.createLabel(mainComposite, "Partition name");
		fPartitionNameText = fToolkit.createText(mainComposite, null);
		fPartitionNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fPartitionNameText.addListener(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
					renamePartition(fPartitionNameText.getText());
				}
			}
		});
		createButton(mainComposite, "Change", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				renamePartition(fPartitionNameText.getText());
			}
		});
		
		fToolkit.createLabel(mainComposite, "Partition value");
		fPartitionValueText = fToolkit.createText(mainComposite, null);
		fPartitionValueText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		fPartitionValueText.addListener(SWT.KeyDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
					changePartitionValue(fPartitionValueText.getText());
				}
			}
		});
		createButton(mainComposite, "Change", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				changePartitionValue(fPartitionValueText.getText());
			}
		});
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		if(structuredSelection.getFirstElement() instanceof PartitionNode){
			fSelectedNode = (PartitionNode)structuredSelection.getFirstElement();
			refresh();
		}
	}
	public void refresh() {
		if(fSelectedNode == null){
			return;
		}
		fMainSection.setText(fSelectedNode.toString());
		fPartitionNameText.setText(fSelectedNode.getName());
		fPartitionValueText.setText(fSelectedNode.getValueString());
	}

	private void renamePartition(String name) {
		CategoryNode parent = (CategoryNode)fSelectedNode.getParent();
		if(EcModelUtils.validatePartitionName(name, parent, fSelectedNode)){
			fSelectedNode.setName(name);
			updateModel(fSelectedNode);
		}
		else{
			MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), 
					DialogStrings.DIALOG_PARTITION_NAME_PROBLEM_TITLE, 
					Display.getDefault().getSystemImage(SWT.ICON_ERROR), 
					DialogStrings.DIALOG_PARTITION_NAME_PROBLEM_MESSAGE,
					MessageDialog.ERROR, new String[] {"OK"}, 0);
			dialog.open();
			fPartitionNameText.setText(fSelectedNode.getName());
		}
	}

	private void changePartitionValue(String valueString) {
		CategoryNode parent = (CategoryNode)fSelectedNode.getParent();
		if(EcModelUtils.validatePartitionStringValue(valueString, parent)){
			fSelectedNode.setValue(EcModelUtils.getPartitionValueFromString(valueString, parent.getType()));
			updateModel(fSelectedNode);
		}
		else{
			MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), 
					DialogStrings.DIALOG_PARTITION_VALUE_PROBLEM_TITLE, 
					Display.getDefault().getSystemImage(SWT.ICON_ERROR), 
					DialogStrings.DIALOG_PARTITION_VALUE_PROBLEM_MESSAGE,
					MessageDialog.ERROR, new String[] {"OK"}, 0);
			dialog.open();
			fPartitionValueText.setText(fSelectedNode.getValueString());
		}
	}
}
