package com.testify.ecfeed.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.testify.ecfeed.adapter.java.JavaUtils;
import com.testify.ecfeed.model.GlobalParameterNode;
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.ui.common.IFileInfoProvider;
import com.testify.ecfeed.ui.modelif.AbstractParameterInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;
import com.testify.ecfeed.ui.modelif.MethodParameterInterface;

public class MethodParameterDetailsPage extends AbstractParameterDetailsPage {

	private final String NOT_LINKED = "NOT LINKED";

	private MethodParameterInterface fParameterIf;
	private Button fExpectedCheckbox;
	private Combo fDefaultValueCombo;
	private Composite fDefaultValueComboComposite;
	private Button fLinkedCheckbox;
	private Combo fLinkCombo;

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

	private class SetLinkedListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			fParameterIf.setLinked(fLinkedCheckbox.getSelection());
			fLinkedCheckbox.setSelection(fParameterIf.isLinked());
			GlobalParameterNode link = fParameterIf.getLink();
			fLinkCombo.setText(link != null ? link.toString() : NOT_LINKED);
		}
	}

	private class SetLinkListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			String linkPath = linkPath(fLinkCombo.getText());
			GlobalParameterNode link = fParameterIf.getGlobalParameter(linkPath);
			fParameterIf.setLink(link);
			fLinkCombo.setText(linkName(fParameterIf.getLink()));
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

		GridData checkboxGridData = new GridData(SWT.FILL,  SWT.CENTER, true, false);
		checkboxGridData.horizontalSpan = 2;

		fExpectedCheckbox = getToolkit().createButton(attributesComposite, "Expected", SWT.CHECK);
		fExpectedCheckbox.setLayoutData(checkboxGridData);
		fExpectedCheckbox.addSelectionListener(new SetExpectedListener());

		getToolkit().createLabel(attributesComposite, "Default value: ", SWT.NONE);
		fDefaultValueComboComposite = getToolkit().createComposite(attributesComposite);
		GridLayout gl = new GridLayout(1, false);
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		fDefaultValueComboComposite.setLayout(gl);
		fDefaultValueComboComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		fLinkedCheckbox = getToolkit().createButton(attributesComposite, "Linked", SWT.CHECK);
		fLinkedCheckbox.setLayoutData(checkboxGridData);
		fLinkedCheckbox.addSelectionListener(new SetLinkedListener());

		getToolkit().createLabel(attributesComposite, "Parameter link: ", SWT.NONE);

		fLinkCombo = new Combo(attributesComposite,SWT.DROP_DOWN|SWT.READ_ONLY);
		fLinkCombo.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, true, false));
		fLinkCombo.addSelectionListener(new SetLinkListener());

		return attributesComposite;
	}

	@Override
	public void refresh(){
		super.refresh();
		if(getSelectedElement() instanceof MethodParameterNode){
			super.refresh();
			MethodParameterNode parameter = (MethodParameterNode)getSelectedElement();
			fParameterIf.setTarget(parameter);

			getTypeCombo().setEnabled(typeComboEnabled());

			getMainSection().setText((parameter.isExpected()?"[e]":"") + parameter.toString());
			fExpectedCheckbox.setSelection(parameter.isExpected());

			fExpectedCheckbox.setEnabled(expectedCheckboxEnabled());

			recreateDefaultValueCombo(parameter);
			fLinkedCheckbox.setSelection(parameter.isLinked());
			fLinkedCheckbox.setEnabled(linkedCheckboxEnabled());
			fLinkCombo.setItems(availableLinks().toArray(new String[]{}));

			if(parameter.getLink() == null && fParameterIf.getAvailableLinks().size() > 0){
				parameter.setLink(fParameterIf.getAvailableLinks().get(0));
			}
			if(parameter.getLink() != null){
				fLinkCombo.setText(linkName(parameter.getLink()));
			}
			fLinkCombo.setEnabled(fParameterIf.isLinked());

			if(fParameterIf.isExpected() && fParameterIf.isPrimitive()){
				getChoicesViewer().setVisible(false);
			}
			else{
				getChoicesViewer().setVisible(true);
			}
			getChoicesViewer().setEditEnabled(choicesViewerEnabled());
		}
	}

	private boolean choicesViewerEnabled() {
		return fParameterIf.isLinked() == false;
	}

	private boolean typeComboEnabled() {
		return fParameterIf.isLinked() == false;
	}

	private boolean linkedCheckboxEnabled() {
		return availableLinks().size() > 0 && (fParameterIf.isExpected() == false);
	}

	private boolean expectedCheckboxEnabled(){
		return fParameterIf.isLinked() == false || fParameterIf.isUserType();
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

	private List<String> availableLinks(){
		List<String> result = new ArrayList<>();
		for(GlobalParameterNode parameter : fParameterIf.getAvailableLinks()){
			result.add(linkName(parameter));
		}
		return result;
	}

	private String linkName(GlobalParameterNode parameter) {
		return parameter.getQualifiedName() + " [" + JavaUtils.getLocalName(parameter.getType()) + "]";
	}

	private String linkPath(String linkName){
		return linkName.substring(0, linkName.indexOf(" "));
	}
}
