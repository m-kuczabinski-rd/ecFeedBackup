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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.actions.ActionFactory;

import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.ui.editor.actions.IActionProvider;
import com.testify.ecfeed.ui.editor.actions.NamedAction;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public abstract class ViewerSection extends BasicSection implements ISelectionProvider{
	public static final int BUTTONS_ASIDE = 1;
	public static final int BUTTONS_BELOW = 2;

	private final int VIEWER_STYLE = SWT.BORDER | SWT.MULTI;

	private List<Object> fSelectedElements;

	private Composite fButtonsComposite;
	private StructuredViewer fViewer;
	private Composite fViewerComposite;
	private Menu fMenu;

	protected class ViewerKeyAdapter extends KeyAdapter{
		private int fKeyCode;
		private Action fAction;
		private int fModifier;

		public ViewerKeyAdapter(int keyCode, int modifier, Action action){
			fKeyCode = keyCode;
			fModifier = modifier;
			fAction = action;
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if((e.stateMask & fModifier) != 0 || fModifier == SWT.NONE){
				if(e.keyCode == fKeyCode){
					fAction.run();
				}
			}
		}
	}

	protected class ActionSelectionAdapter extends SelectionAdapter{
		private Action fAction;

		public ActionSelectionAdapter(Action action){
			fAction = action;
		}

		@Override
		public void widgetSelected(SelectionEvent e){
			fAction.run();
		}
	}

	protected class ViewerMenuListener implements MenuListener{

		private Menu fMenu;

		private class MenuItemSelectionAdapter extends SelectionAdapter{

			private Action fAction;

			public MenuItemSelectionAdapter(Action action){
				fAction = action;
			}

			@Override
			public void widgetSelected(SelectionEvent e){
				fAction.run();
			}

		}

		public ViewerMenuListener(Menu menu) {
			fMenu = menu;
		}

		protected Menu getMenu(){
			return fMenu;
		}

		@Override
		public void menuHidden(MenuEvent e) {
		}

		@Override
		public void menuShown(MenuEvent e) {
			for(MenuItem item : getMenu().getItems()){
				item.dispose();
			}
			populateMenu();
		}

		protected void populateMenu() {
			IActionProvider provider = getActionProvider();
			Iterator<String> groupIt = provider.getGroups().iterator();
			while(groupIt.hasNext()){
				for(NamedAction action : provider.getActions(groupIt.next())){
					addMenuItem(action.getName(), action);
				}
				if(groupIt.hasNext()){
					new MenuItem(fMenu, SWT.SEPARATOR);
				}
			}
		}

		protected void addMenuItem(String text, Action action){
			MenuItem item = new MenuItem(getMenu(), SWT.NONE);

			item.setText(text);
			item.setEnabled(action.isEnabled());
			item.addSelectionListener(new MenuItemSelectionAdapter(action));
		}

	}

	public ViewerSection(ISectionContext sectionContext, IModelUpdateContext updateContext, int style) {
		super(sectionContext, updateContext, style);
		fSelectedElements = new ArrayList<>();
	}

	@Override
	public void refresh(){
		super.refresh();
		if(fViewer != null && fViewer.getControl().isDisposed() == false){
			fViewer.refresh();
		}
	}

	public Object getSelectedElement(){
		if(fSelectedElements.size() > 0){
			return fSelectedElements.get(0);
		}
		return null;
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

	public StructuredViewer getViewer(){
		return fViewer;
	}

    @Override
	public void addSelectionChangedListener(ISelectionChangedListener listener){
    	fViewer.addSelectionChangedListener(listener);
    }

    @Override
	public IStructuredSelection getSelection(){
    	return (IStructuredSelection)fViewer.getSelection();
    }

    public List<GenericNode> getSelectedNodes(){
    	List<GenericNode> result = new ArrayList<>();
		for(Object o : getSelection().toList()){
    		if(o instanceof GenericNode){
    			result .add((GenericNode)o);
    		}
    	}
    	return result;
    }

    @Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener){
    	fViewer.removeSelectionChangedListener(listener);
    }

    @Override
	public void setSelection(ISelection selection){
    	fViewer.setSelection(selection);
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

	/*
	 * Indicates whether optional buttons are located below (default)
	 * or on the right side of the viewer
	 */
	protected int buttonsPosition() {
		return BUTTONS_BELOW;
	}

	protected int viewerStyle(){
		return VIEWER_STYLE;
	}

	protected Composite createViewerComposite(Composite parent) {
		fViewerComposite = getToolkit().createComposite(parent);
		fViewerComposite.setLayout(new GridLayout(1, false));
		fViewerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		fViewer = createViewer(fViewerComposite, viewerStyle());
		fViewer.setContentProvider(viewerContentProvider());
		fViewer.setLabelProvider(viewerLabelProvider());
		createViewerColumns();

		fViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				fSelectedElements = ((IStructuredSelection)event.getSelection()).toList();
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
			return new GridData(SWT.FILL, SWT.TOP, true, false);
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

	protected GridData viewerLayoutData(){
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 100;
		gd.heightHint = 100;
		return gd;
	}

	protected Composite getViewerComposite(){
		return fViewerComposite;
	}

	protected void addKeyListener(int keyCode, int modifier, Action action){
		fViewer.getControl().addKeyListener(new ViewerKeyAdapter(keyCode, modifier, action));
	}

	@Override
	protected void setActionProvider(IActionProvider provider){
		super.setActionProvider(provider);
		fMenu = new Menu(fViewer.getControl());
		fViewer.getControl().setMenu(fMenu);
		fMenu.addMenuListener(getMenuListener());

		if(provider.getAction(ActionFactory.DELETE.getId()) != null){
			addKeyListener(SWT.DEL, SWT.NONE, provider.getAction(ActionFactory.DELETE.getId()));
		}

		if(provider.getAction(NamedAction.MOVE_UP_ACTION_ID) != null){
			addKeyListener(SWT.ARROW_UP, SWT.ALT, provider.getAction(NamedAction.MOVE_UP_ACTION_ID));
		}

		if(provider.getAction(NamedAction.MOVE_DOWN_ACTION_ID) != null){
			addKeyListener(SWT.ARROW_DOWN, SWT.ALT, provider.getAction(NamedAction.MOVE_DOWN_ACTION_ID));
		}
	}

	protected MenuListener getMenuListener() {
		return new ViewerMenuListener(fMenu);
	}

	protected Menu getMenu(){
		return fMenu;
	}

	protected abstract void createViewerColumns();
	protected abstract StructuredViewer createViewer(Composite viewerComposite, int style);
	protected abstract IContentProvider viewerContentProvider();
	protected abstract IBaseLabelProvider viewerLabelProvider();
}
