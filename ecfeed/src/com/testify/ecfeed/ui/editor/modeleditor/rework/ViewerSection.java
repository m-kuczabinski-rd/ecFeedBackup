package com.testify.ecfeed.ui.editor.modeleditor.rework;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

public abstract class ViewerSection extends BasicSection {
	public static final int BUTTONS_ASIDE = 1;
	public static final int BUTTONS_BELOW = 2;
	
	private final int fButtonsPosition;
	private Object fSelectedElement;

	private Composite fButtonsComposite;
	private StructuredViewer fViewer;
	private Composite fViewerComposite;
	
	public ViewerSection(Composite parent, FormToolkit toolkit, 
			int style, int buttonsPosition) {
		super(parent, toolkit, style);
		fButtonsPosition = buttonsPosition;
	}	
	
	@Override
	protected Composite createClientComposite() {
		Composite client = super.createClientComposite();
		int columns = 1;
		if(fButtonsPosition == BUTTONS_ASIDE){
			columns = 2;
		}
		else if(fButtonsPosition == BUTTONS_BELOW){
			columns = 1;
		}
		
		client.setLayout(new GridLayout(columns, false));
		client.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		createViewerComposite(client); 
		fButtonsComposite = createButtonsComposite(client); 
		return client;
	}

	protected Composite createViewerComposite(Composite parent) {
		fViewerComposite = getToolkit().createComposite(parent);
		fViewerComposite.setLayout(new GridLayout(1, false));
		fViewerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		createViewerLabel(fViewerComposite);
		fViewer = createViewer(fViewerComposite, SWT.BORDER);
		fViewer.setContentProvider(viewerContentProvider());
		fViewer.setLabelProvider(viewerLabelProvider());
		createViewerColumns();

		fViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				fSelectedElement = ((IStructuredSelection)event.getSelection()).getFirstElement();
			}
		});

		return fViewerComposite;
	}

	protected void createViewerLabel(Composite viewerComposite) {
	}

	protected Composite createButtonsComposite(Composite parent) {
		Composite buttonsComposite = getToolkit().createComposite(parent);
		RowLayout rl = new RowLayout();
		rl.pack = false;
		if(fButtonsPosition == BUTTONS_ASIDE){
			rl.type = SWT.VERTICAL;
		}
		buttonsComposite.setLayout(rl);
		return buttonsComposite;
	}
	
	protected Button addButton(String text, SelectionAdapter adapter){
		Button button = getToolkit().createButton(fButtonsComposite, text, SWT.None);
		button.addSelectionListener(adapter);
		return button;
	}
	
	protected void addDoubleClickListener(IDoubleClickListener listener){
		getViewer().addDoubleClickListener(listener);
	}
	
	protected StructuredViewer getViewer(){
		return fViewer;
	}

	@Override
	public void refresh(){
		super.refresh();
		fViewer.refresh();
	}
	
	public Object getSelectedElement(){
		return fSelectedElement;
	}
	
	protected GridData viewerLayoutData(){
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 100;
		gd.heightHint = 100;
		return gd;
	}
	
	public void selectElement(Object element){
		getViewer().setSelection(new StructuredSelection(element), true);
	}

	public void setInput(Object input){
		fViewer.setInput(input);
		refresh();
	}
	
	public Object getInput(){
		return fViewer.getInput();
	}
	
	protected Composite getViewerComposite(){
		return fViewerComposite;
	}
	
	protected abstract void createViewerColumns();
	protected abstract StructuredViewer createViewer(Composite viewerComposite, int style);
	protected abstract IContentProvider viewerContentProvider();
	protected abstract IBaseLabelProvider viewerLabelProvider();


}
