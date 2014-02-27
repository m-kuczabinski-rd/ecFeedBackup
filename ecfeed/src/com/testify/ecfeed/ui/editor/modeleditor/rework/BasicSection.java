package com.testify.ecfeed.ui.editor.modeleditor.rework;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class BasicSection extends SectionPart{
	private Composite fClientComposite;
	private FormToolkit fToolkit;
	BasicDetailsPage fParentPage;
	private Control fTextClient;

	@Override
	public void refresh(){
		if(fTextClient != null){
			updateTextClient();
		}
	}

	public BasicSection(BasicDetailsPage parent, FormToolkit toolkit, int style) {
		super(parent.getMainComposite(), toolkit, style);
		fParentPage = parent;
		fToolkit = toolkit;
		createContent();
	}

	public FormToolkit getToolkit(){
		return fToolkit;
	}

	public void setText(String title){
		getSection().setText(title);
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
	
	protected BasicDetailsPage getParentPage(){
		return fParentPage;
	}
	
	protected void modelUpdated(AbstractFormPart source){
		fParentPage.modelUpdated(source);
	}
}
