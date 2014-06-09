package com.testify.ecfeed.ui.common;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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

/*
 * Simple menu for any purpose. I find it much more effective for both user and programmer - 
 * why mess with overlapping components in layouts when you can add menu and disable it anytime?
 */
public abstract class SimpleControlMenuListener implements Listener{
	private boolean fEnabled;
	private Composite fParent;
	private Control fControl;
	private Menu fMenu;
	private Set<String> fData;

	private class MenuItemListener extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			menuItemSelected(e);
		}
	}

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
	
	public SimpleControlMenuListener(Composite parent, Control control){
		fParent = parent;
		fControl = control;
		fMenu = new Menu(parent);
		fData = new HashSet<String>();
		fEnabled = true;
	}

	public void addData(Collection<String> datalist){
		fData.addAll(datalist);
	}
	
	public void addData(String data){
		fData.add(data);
	}

	public void clearData(){
		fData.clear();
	}

	public void createMenu(){
		fMenu = new Menu(fParent);
		MenuItem item;
		MenuItemListener listener = new MenuItemListener();

		for(String data : fData){
			item = new MenuItem(fMenu, SWT.PUSH);
			item.setText(data);
			item.addSelectionListener(listener);
		}
	}

	public void setEnabled(boolean enabled){
		fEnabled = enabled;
	}
	
	protected abstract void menuItemSelected(SelectionEvent e);

}