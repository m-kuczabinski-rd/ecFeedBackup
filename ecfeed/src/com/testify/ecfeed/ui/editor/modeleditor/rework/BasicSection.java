package com.testify.ecfeed.ui.editor.modeleditor.rework;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.testify.ecfeed.model.IGenericNode;

public abstract class BasicSection extends SectionPart{
	private Composite fClientComposite;
	private FormToolkit fToolkit;
	private Control fTextClient;
	private IModelUpdateListener fModelUpdateListener;

	protected class SelectNodeDoubleClickListener implements IDoubleClickListener {

		private ModelMasterSection fMasterSection;

		public SelectNodeDoubleClickListener(ModelMasterSection masterSection){
			fMasterSection = masterSection;
		}
		
		@Override
		public void doubleClick(DoubleClickEvent event) {
			if(event.getSelection() instanceof IStructuredSelection){
				IStructuredSelection selection = (IStructuredSelection)event.getSelection();
				if(selection.getFirstElement() instanceof IGenericNode){
					fMasterSection.selectElement(selection.getFirstElement());
				}
			}
		}
	}

	@Override
	public void refresh(){
		if(fTextClient != null){
			updateTextClient();
		}
	}

	public BasicSection(Composite parent, FormToolkit toolkit, int style, IModelUpdateListener updateListener) {
		super(parent, toolkit, style);
		fModelUpdateListener = updateListener;
		fToolkit = toolkit;
		createContent();
	}

	public FormToolkit getToolkit(){
		return fToolkit;
	}

	public void setText(String title){
		getSection().setText(title);
	}

	public IModelUpdateListener getUpdateListener(){
		return fModelUpdateListener;
	}

	protected Composite getClientComposite(){
		return fClientComposite;
	}
	
	protected void createContent(){
		getSection().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		fTextClient = createTextClient();
		fClientComposite = createClientComposite();
	}
	
	protected Composite createClientComposite() {
		Composite client = fToolkit.createComposite(getSection());
		client.setLayout(clientLayout());
		if(clientLayoutData() != null){
			client.setLayoutData(clientLayoutData());
		}
		getSection().setClient(client);
		getToolkit().adapt(client);
		getToolkit().paintBordersFor(client);
		return client;
	}

	protected Control createTextClient() {
		return null;
	}

	protected Layout clientLayout() {
		GridLayout layout = new GridLayout(1, false);
		return layout;
	}

	protected Object clientLayoutData() {
		return new GridData(SWT.FILL, SWT.FILL, true, true);
	}

	protected void updateTextClient() {
	}
	
	protected Shell getActiveShell(){
		return Display.getCurrent().getActiveShell();
	}
	
	protected void modelUpdated(){
		getUpdateListener().modelUpdated(this);
	}
}
