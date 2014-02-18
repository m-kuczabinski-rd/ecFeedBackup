package com.testify.ecfeed.ui.editor.modeleditor.rework;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.ui.editor.IModelUpdateListener;

public class BasicSection extends SectionPart implements IModelUpdateListener, IModelSelectionListener{
	private Composite fClientComposite;
	private FormToolkit fToolkit;
	
	private Control fTextClient; 

	public BasicSection(Composite parent, FormToolkit toolkit, int style) {
		super(parent, toolkit, style);
		fToolkit = toolkit;
		createContent();
	}

	public Composite getClientComposite(){
		return fClientComposite;
	}
	
	public FormToolkit getToolkit(){
		return fToolkit;
	}
	
	protected void createContent(){
		fTextClient = createTextClient();
		fClientComposite = createClientComposite();
	}
	
	
	protected Composite createClientComposite() {
		Composite client = fToolkit.createComposite(getSection());
		client.setLayout(clientGridLayout(1, false));
		client.setLayoutData(clientGridLayoutData());
		getSection().setClient(client);
		return client;
	}

	protected Control createTextClient() {
		return null;
	}

	protected GridLayout clientGridLayout(int columns, boolean columnsEqual) {
		GridLayout layout = new GridLayout(columns, columnsEqual);
		return layout;
	}

	protected Object clientGridLayoutData() {
		return new GridData(SWT.FILL, SWT.FILL, true, true);
	}

	@Override
	public void modelUpdated(RootNode model) {
		refresh();
	}

	@Override
	public void modelSelectionChanged(ISelection newSelection) {
	}

	protected void updateTextClient() {
	}
	
	@Override
	public void refresh(){
		if(fTextClient != null){
			updateTextClient();
		}
	}
	
	public void setText(String title){
		getSection().setText(title);
	}
}
