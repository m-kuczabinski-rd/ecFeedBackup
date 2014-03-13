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

import com.testify.ecfeed.model.ExpectedValueCategoryNode;

public class ExpectedValueDetailsPage extends BasicDetailsPage {

	private Text fDefaultValueText;
	private ExpectedValueCategoryNode fSelectedCategory;

	private class ApplyButtonAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			if(applyNewDefaultValue(fSelectedCategory, fDefaultValueText)){
				modelUpdated(null);
			}
		}

		protected boolean applyNewDefaultValue(ExpectedValueCategoryNode category, Text valueText) {
			String newValue = valueText.getText();
			if(category.validatePartitionStringValue(newValue)){
				category.setDefaultValue(category.getPartitionValueFromString(newValue));
				return true;
			}
			valueText.setText(category.getDefaultValuePartition().getValueString());
			return false;
		}
	}
	
	private class DefaultValueKeydownListener extends ApplyButtonAdapter implements Listener{
		@Override
		public void handleEvent(Event event) {
			if(event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR){
				if(applyNewDefaultValue(fSelectedCategory, fDefaultValueText)){
					modelUpdated(null);
				}
			}
		}
	}
	
	public ExpectedValueDetailsPage(ModelMasterSection masterSection) {
		super(masterSection);
	}
	
	@Override
	public void createContents(Composite parent){
		super.createContents(parent);
		createDefaultValueEdit();
	}

	private void createDefaultValueEdit() {
		Composite composite = getToolkit().createComposite(getMainComposite());
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		getToolkit().createLabel(composite, "Default value: ", SWT.NONE);
		fDefaultValueText = getToolkit().createText(composite, "",SWT.NONE);
		fDefaultValueText.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, true, false));
		fDefaultValueText.addListener(SWT.KeyDown, new DefaultValueKeydownListener());
		Button applyButton = getToolkit().createButton(composite, "Apply", SWT.NONE);
		getToolkit().paintBordersFor(composite);
		applyButton.addSelectionListener(new ApplyButtonAdapter());
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		super.selectionChanged(part, selection);
		if(getSelectedElement() instanceof ExpectedValueCategoryNode){
			fSelectedCategory = (ExpectedValueCategoryNode)getSelectedElement();
		}
		refresh();
	}
	
	@Override
	public void refresh(){
		super.refresh();
		getMainSection().setText(fSelectedCategory.toString());
		fDefaultValueText.setText(fSelectedCategory.getDefaultValuePartition().getValueString());
	}
}
