package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.model.AbstractParameterNode;
import com.testify.ecfeed.ui.common.IFileInfoProvider;
import com.testify.ecfeed.ui.modelif.AbstractParameterInterface;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public abstract class AbstractParameterDetailsPage extends BasicDetailsPage {

	private Composite fAttributesComposite;
	private Text fNameText;
	private Combo fTypeCombo;
	private ChoicesViewer fChoicesViewer;

	private class SetNameListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			getParameterIf().setName(fNameText.getText());
			fNameText.setText(getParameterIf().getName());
		}
	}

	private class SetTypeListener extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			getParameterIf().setType(fTypeCombo.getText());
			fTypeCombo.setText(getParameterIf().getType());
		}
	}

	public AbstractParameterDetailsPage(ModelMasterSection masterSection,
			IModelUpdateContext updateContext, IFileInfoProvider fileInforProvider) {
		super(masterSection, updateContext, fileInforProvider);
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);

		createAttributesComposite();
		addForm(fChoicesViewer = new ChoicesViewer(this, this));

		getToolkit().paintBordersFor(getMainComposite());
	}

	@Override
	protected Composite createTextClientComposite(){
		Composite textClient = super.createTextClientComposite();
		createImplementerButton(textClient);
		return textClient;
	}

	@Override
	public void refresh(){
		super.refresh();
		if(getSelectedElement() instanceof AbstractParameterNode){
			AbstractParameterNode parameter = (AbstractParameterNode)getSelectedElement();
			getParameterIf().setTarget(parameter);

			getMainSection().setText(parameter.toString());
			fNameText.setText(parameter.getName());
			fTypeCombo.setItems(AbstractParameterInterface.supportedPrimitiveTypes());
			fTypeCombo.setText(parameter.getType());

			fChoicesViewer.setInput(parameter);
		}
	}

	protected Composite createAttributesComposite(){
		fAttributesComposite = getToolkit().createComposite(getMainComposite());
		fAttributesComposite.setLayout(new GridLayout(2, false));
		fAttributesComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		getToolkit().createLabel(fAttributesComposite, "Parameter name: ", SWT.NONE);
		fNameText = getToolkit().createText(fAttributesComposite, "",SWT.NONE);
		fNameText.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, true, false));
		SelectionListener nameListener = new SetNameListener();
		fNameText.addSelectionListener(nameListener);

		getToolkit().createLabel(fAttributesComposite, "Parameter type: ", SWT.NONE);
		fTypeCombo = new Combo(fAttributesComposite,SWT.DROP_DOWN);
		fTypeCombo.setLayoutData(new GridData(SWT.FILL,  SWT.CENTER, true, false));
		fTypeCombo.addSelectionListener(new SetTypeListener());

		getToolkit().paintBordersFor(fAttributesComposite);
		return fAttributesComposite;
	}

	protected Composite getAttributesComposite(){
		return fAttributesComposite;
	}

	protected ChoicesViewer getChoicesViewer(){
		return fChoicesViewer;
	}

	protected Combo getTypeCombo(){
		return fTypeCombo;
	}

	protected abstract AbstractParameterInterface getParameterIf();
}
