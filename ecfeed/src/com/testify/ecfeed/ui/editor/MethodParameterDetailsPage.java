package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.ui.common.IFileInfoProvider;
import com.testify.ecfeed.ui.modelif.AbstractParameterInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.MethodParameterInterface;

public class MethodParameterDetailsPage extends AbstractParameterDetailsPage {

	private MethodParameterInterface fParameterIf;
	private Button fExpectedCheckbox;
	private Combo fDefaultValueCombo;
	private Composite fDefaultValueComboComposite;

	private class SetDefaultValueListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			fParameterIf.setDefaultValue(fDefaultValueCombo.getText());
			fDefaultValueCombo.setText(fParameterIf.getDefaultValue());
		}
	}

	private class SetExpectedListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			fParameterIf.setExpected(fExpectedCheckbox.getSelection());
			fExpectedCheckbox.setSelection(fParameterIf.isExpected());
		}
	}

	public MethodParameterDetailsPage(ModelMasterSection masterSection, IModelUpdateContext updateContext,
			IFileInfoProvider fileInforProvider) {
		super(masterSection, updateContext, fileInforProvider);
		getParameterIf();
	}

	@Override
	protected AbstractParameterInterface getParameterIf() {
		if(fParameterIf == null){
			fParameterIf = new MethodParameterInterface(this);
		}
		return fParameterIf;
	}

	@Override
	protected Composite createAttributesComposite(){
		Composite attributesComposite = super.createAttributesComposite();
		getToolkit().createLabel(attributesComposite, "Default value: ", SWT.NONE);

		fDefaultValueComboComposite = getToolkit().createComposite(attributesComposite);
		GridLayout gl = new GridLayout(1, false);
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		fDefaultValueComboComposite.setLayout(gl);
		fDefaultValueComboComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		fExpectedCheckbox = getToolkit().createButton(getMainComposite(), "Expected", SWT.CHECK);
		fExpectedCheckbox.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, true, false));
		fExpectedCheckbox.addSelectionListener(new SetExpectedListener());
		return attributesComposite;
	}

	@Override
	public void refresh(){
		super.refresh();
		if(getSelectedElement() instanceof MethodParameterNode){
			MethodParameterNode parameter = (MethodParameterNode)getSelectedElement();
			fParameterIf.setTarget(parameter);

			getMainSection().setText((parameter.isExpected()?"[e]":"") + parameter.toString());
			recreateDefaultValueCombo(parameter);
			fExpectedCheckbox.setSelection(parameter.isExpected());
			if(fParameterIf.isExpected() && fParameterIf.isPrimitive()){
				getChoicesViewer().setVisible(false);
			}
			else{
				getChoicesViewer().setVisible(true);
			}

			getChoicesViewer().setInput(parameter);
		}
	}

	private void recreateDefaultValueCombo(MethodParameterNode parameter) {
		if(fDefaultValueCombo != null && fDefaultValueCombo.isDisposed() == false){
			fDefaultValueCombo.dispose();
		}
		if(fParameterIf.hasLimitedValuesSet()){
			fDefaultValueCombo = new Combo(fDefaultValueComboComposite,SWT.DROP_DOWN | SWT.READ_ONLY);
		}
		else{
			fDefaultValueCombo = new Combo(fDefaultValueComboComposite,SWT.DROP_DOWN);
		}
		fDefaultValueCombo.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, true, false));
		fDefaultValueCombo.setItems(fParameterIf.defaultValueSuggestions());
		fDefaultValueCombo.setText(parameter.getDefaultValue());
		fDefaultValueCombo.addSelectionListener(new SetDefaultValueListener());

		fDefaultValueCombo.setEnabled(parameter.isExpected());

		fDefaultValueComboComposite.layout();
	}

}
