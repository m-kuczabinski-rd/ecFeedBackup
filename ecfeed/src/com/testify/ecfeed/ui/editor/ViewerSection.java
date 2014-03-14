/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                   
 * All rights reserved. This program and the accompanying materials                 
 * are made available under the terms of the Eclipse Public License v1.0            
 * which accompanies this distribution, and is available at                         
 * http://www.eclipse.org/legal/epl-v10.html                                        
 *                                                                                  
 * Contributors:                                                                    
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.editor;

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
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.widgets.FormToolkit;

public abstract class ViewerSection extends BasicSection {
	public static final int BUTTONS_ASIDE = 1;
	public static final int BUTTONS_BELOW = 2;
	
	private Object fSelectedElement;

	private Composite fButtonsComposite;
	private StructuredViewer fViewer;
	private Composite fViewerComposite;
	
	public ViewerSection(Composite parent, FormToolkit toolkit, int style, IModelUpdateListener updateListener) {
		super(parent, toolkit, style, updateListener);
	}	
	
	@Override
	public void refresh(){
		super.refresh();
		fViewer.refresh();
	}

	@Override
	protected Composite createClientComposite() {
		Composite client = super.createClientComposite();
		createViewerComposite(client); 
		fButtonsComposite = createButtonsComposite(client); 
		return client;
	}
	
	@Override 
	protected Layout clientLayout() {
		GridLayout layout = new GridLayout(buttonsPosition() == BUTTONS_BELOW?1:2, false);
		return layout;
	}

	public Object getSelectedElement(){
		return fSelectedElement;
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

	/*
	 * Indicates whether optional buttons are located below (default)
	 * or on the left side of the viewer
	 */
	protected int buttonsPosition() {
		return BUTTONS_BELOW;
	}

	protected Composite createViewerComposite(Composite parent) {
		fViewerComposite = getToolkit().createComposite(parent);
		fViewerComposite.setLayout(new GridLayout(1, false));
		fViewerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
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

	protected Composite createButtonsComposite(Composite parent) {
		Composite buttonsComposite = getToolkit().createComposite(parent);
		buttonsComposite.setLayout(buttonsCompositeLayout());
		if(buttonsCompositeLayoutData() != null){
			buttonsComposite.setLayoutData(buttonsCompositeLayoutData());
		}
		return buttonsComposite;
	}
	
	protected Layout buttonsCompositeLayout() {
		if(buttonsPosition() == BUTTONS_BELOW){
			RowLayout rl = new RowLayout();
			rl.pack = false;
			return rl;
		}
		else{
			return new GridLayout(1, false);
		}
	}

	protected Object buttonsCompositeLayoutData() {
		if(buttonsPosition() == BUTTONS_BELOW){
			return null;
		}
		else{
			return new GridData(SWT.FILL, SWT.TOP, false, true);
		}
	}

	protected Button addButton(String text, SelectionAdapter adapter){
		Button button = getToolkit().createButton(fButtonsComposite, text, SWT.NONE);
		if(adapter != null){
			button.addSelectionListener(adapter);
		}
		if(buttonLayoutData() != null){
			button.setLayoutData(buttonLayoutData());
		}
		return button;
	}
	
	protected Object buttonLayoutData() {
		if(buttonsPosition() == BUTTONS_ASIDE){
			return new GridData(SWT.FILL,  SWT.TOP, true, false);
		}
		return null;
	}

	protected void addDoubleClickListener(IDoubleClickListener listener){
		getViewer().addDoubleClickListener(listener);
	}
	
	protected StructuredViewer getViewer(){
		return fViewer;
	}

	protected GridData viewerLayoutData(){
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 100;
		gd.heightHint = 100;
		return gd;
	}
	
	protected Composite getViewerComposite(){
		return fViewerComposite;
	}
	
	protected abstract void createViewerColumns();
	protected abstract StructuredViewer createViewer(Composite viewerComposite, int style);
	protected abstract IContentProvider viewerContentProvider();
	protected abstract IBaseLabelProvider viewerLabelProvider();


}
