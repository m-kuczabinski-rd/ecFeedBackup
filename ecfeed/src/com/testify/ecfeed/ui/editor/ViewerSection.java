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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.actions.ActionFactory;

import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.ui.common.IFileInfoProvider;
import com.testify.ecfeed.ui.editor.actions.IActionProvider;
import com.testify.ecfeed.ui.editor.actions.NamedAction;
import com.testify.ecfeed.ui.editor.utils.ExceptionCatchDialog;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

/**
 * Section with a main StructuredViewer composite and buttons below or aside
 */
public abstract class ViewerSection extends ButtonsCompositeSection implements ISelectionProvider{

	private final int VIEWER_STYLE = SWT.BORDER | SWT.MULTI;

	private List<Object> fSelectedElements;

	private StructuredViewer fViewer;
	private Composite fViewerComposite;
	private Menu fMenu;
	private Set<KeyListener> fKeyListsners;

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
		private String fDescriptionWhenError;

		public ActionSelectionAdapter(Action action, String descriptionWhenError ){
			fAction = action;
			fDescriptionWhenError = descriptionWhenError;
		}

		@Override
		public void widgetSelected(SelectionEvent ev){
			try {
				fAction.run();
			} catch (Exception e) {
				ExceptionCatchDialog.display(fDescriptionWhenError, e.getMessage());
			}
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
			public void widgetSelected(SelectionEvent ev){
				try {
					fAction.run();
				} catch (Exception e) {
					ExceptionCatchDialog.display(null, e.getMessage());
				}
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
			if(provider != null){
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
		}

		protected void addMenuItem(String text, Action action){
			MenuItem item = new MenuItem(getMenu(), SWT.NONE);

			item.setText(text);
			item.setEnabled(action.isEnabled());
			item.addSelectionListener(new MenuItemSelectionAdapter(action)); 
		}

	}

	public ViewerSection(
			ISectionContext sectionContext, 
			IModelUpdateContext updateContext, 
			IFileInfoProvider fileInfoProvider, 
			int style) {
		super(sectionContext, updateContext, fileInfoProvider, style);
		fSelectedElements = new ArrayList<>();
		fKeyListsners = new HashSet<KeyListener>();
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

	public List<AbstractNode> getSelectedNodes(){
		List<AbstractNode> result = new ArrayList<>();
		for(Object o : getSelection().toList()){
			if(o instanceof AbstractNode){
				result .add((AbstractNode)o);
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
		createViewer();
		return client;
	}

	protected void createViewer() {
		fViewer = createViewer(getMainControlComposite(), viewerStyle());
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
	}

	protected int viewerStyle(){
		return VIEWER_STYLE;
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

	protected KeyListener addKeyListener(int keyCode, int modifier, Action action){
		ViewerKeyAdapter adapter = new ViewerKeyAdapter(keyCode, modifier, action);
		fViewer.getControl().addKeyListener(adapter);
		return adapter;
	}

	@Override
	protected void setActionProvider(IActionProvider provider){
		setActionProvider(provider, true);
	}

	protected void setActionProvider(IActionProvider provider, boolean addDeleteAction){
		super.setActionProvider(provider);
		fMenu = new Menu(fViewer.getControl());
		fViewer.getControl().setMenu(fMenu);
		fMenu.addMenuListener(getMenuListener());

		if(provider != null){
			if(addDeleteAction && provider.getAction(ActionFactory.DELETE.getId()) != null){
				fKeyListsners.add(addKeyListener(SWT.DEL, SWT.NONE, provider.getAction(ActionFactory.DELETE.getId())));
			}

			if(provider.getAction(NamedAction.MOVE_UP_ACTION_ID) != null){
				fKeyListsners.add(addKeyListener(SWT.ARROW_UP, SWT.ALT, provider.getAction(NamedAction.MOVE_UP_ACTION_ID)));
			}

			if(provider.getAction(NamedAction.MOVE_DOWN_ACTION_ID) != null){
				fKeyListsners.add(addKeyListener(SWT.ARROW_DOWN, SWT.ALT, provider.getAction(NamedAction.MOVE_DOWN_ACTION_ID)));
			}
		}
		else{
			Iterator<KeyListener> it = fKeyListsners.iterator();
			while(it.hasNext()){
				fViewer.getControl().removeKeyListener(it.next());
				it.remove();
			}
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
