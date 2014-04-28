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
	boolean fDisabled;
	Composite fParent;
	Control fControl;
	Menu fMenu;
	int fLastIndexSelected;
	List<List<String>> fDataLists;
	List<String> fListLabels;
	
	public abstract class MenuItemListener extends SelectionAdapter{
		protected int mIndex;

		public MenuItemListener(int index){
			mIndex = index;
		}

		public int getIndex(){
			return mIndex;
		}

		@Override
		public void widgetSelected(SelectionEvent e){

		}
	}
	
	protected abstract MenuItemListener prepareMenuItemListener(int itemCount);

	public ControlMenuListener(Composite parent, Control control){
		this.fParent = parent;
		this.fControl = control;
		this.fMenu = new Menu(parent);
		this.fDataLists = new ArrayList<List<String>>();
		this.fListLabels = new ArrayList<String>();
	}
	
	@Override
	public void handleEvent(Event e){
		if(fDisabled)
			return;
		Point loc = fControl.getLocation();
		Rectangle rect = fControl.getBounds();
		Point mLoc = new Point(loc.x - 1, loc.y + rect.height);
		fMenu.setLocation(fParent.getDisplay().map(fControl.getParent(), null, mLoc));
		fMenu.setVisible(true);
	}

	public void addData(Collection<String> datalist, String listlabel){
		ArrayList<String> data = new ArrayList<>();
		data.addAll(datalist);
		this.fDataLists.add(data);
		this.fListLabels.add(listlabel);
	}

	public void clearData(){
		fDataLists.clear();
		fListLabels.clear();
	}

	public void setMenu(){
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
				item.addSelectionListener(prepareMenuItemListener(itemCount));
				itemCount++;
			}
		}
	}
	
	public void setDisabled(boolean disabled){
		fDisabled = disabled;
	}

	public void setLastSelected(int index){
		if((index > -1) && (index < fMenu.getItemCount()))
			fLastIndexSelected = index;
	}
	
	public int getLastSelected(){
		return fLastIndexSelected;
	}
}
