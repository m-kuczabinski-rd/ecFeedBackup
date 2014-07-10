/*******************************************************************************
 * Copyright (c) 2014 Testify AS.                                                
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
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public abstract class ControlMenuListener implements Listener{
	private boolean fEnabled;
	private Composite fParent;
	private Control fControl;
	private Menu fMenu;
	private int fLastIndexSelected;
	private List<List<String>> fDataLists;
	private List<String> fListLabels;
	
	private class MenuItemListener extends SelectionAdapter{
		private int fIndex;

		public MenuItemListener(int index){
			fIndex = index;
		}

		@Override
		public void widgetSelected(SelectionEvent e){
			fLastIndexSelected = fIndex;
			menuItemSelected(fIndex, e);
		}
	}
	
	public ControlMenuListener(Composite parent, Control control){
		fParent = parent;
		fControl = control;
		fMenu = new Menu(parent);
		fDataLists = new ArrayList<List<String>>();
		fListLabels = new ArrayList<String>();
		fEnabled = true;
	}
	
	protected abstract void menuItemSelected(int index, SelectionEvent e);

	@Override
	public void handleEvent(Event e){
		if(fEnabled){
			Point loc = fControl.getLocation();
			Rectangle rect = fControl.getBounds();
			Point mLoc = new Point(loc.x - 1, loc.y + rect.height);
			fMenu.setLocation(fParent.getDisplay().map(fControl.getParent(), null, mLoc));
			fMenu.setVisible(true);
		}
	}

	public void addData(Collection<String> datalist, String listlabel){
		ArrayList<String> data = new ArrayList<>();
		data.addAll(datalist);
		fDataLists.add(data);
		fListLabels.add(listlabel);
	}

	public void clearData(){
		fDataLists.clear();
		fListLabels.clear();
	}

	public void createMenu(){
		fMenu = new Menu(fParent);
		MenuItem item;
		int itemCount = 0;
		int labelCount = 0;

		for(List<String> datalist : fDataLists){
			// Create label menu item
			if(fListLabels.size() > 1){
				if(fListLabels.get(labelCount) != null &&
						!fListLabels.get(labelCount).equals("")
						&& datalist.size() > 0){
					item = new MenuItem(fMenu, SWT.PUSH);
					item.setText(fListLabels.get(labelCount));
					item.setEnabled(false);
					itemCount++;
				}
				labelCount++;
			}
			// Create anything else
			for(String data : datalist){
				item = new MenuItem(fMenu, SWT.PUSH);
				item.setText(data);
				item.addSelectionListener(new MenuItemListener(itemCount));
				itemCount++;
			}
		}
	}
	
	public void setEnabled(boolean enabled){
		fEnabled = enabled;
	}

	public int getSelectedItemIndex(){
		return fLastIndexSelected;
	}
}
