package com.testify.ecfeed.ui.editor.modeleditor.rework;

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

public class BasicSection extends SectionPart{
	private Composite fClientComposite;
	private FormToolkit fToolkit;
	
	private Control fTextClient;

	public BasicSection(Composite parent, FormToolkit toolkit, int style) {
		super(parent, toolkit, style);
		fToolkit = toolkit;
		createContent();
	}

	protected Composite getClientComposite(){
		return fClientComposite;
	}
	
	public FormToolkit getToolkit(){
		return fToolkit;
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
	
	public void refresh(){
		if(fTextClient != null){
			updateTextClient();
		}
	}
	
	public void setText(String title){
		getSection().setText(title);
	}

	protected Shell getActiveShell(){
		return Display.getCurrent().getActiveShell();
	}
}
