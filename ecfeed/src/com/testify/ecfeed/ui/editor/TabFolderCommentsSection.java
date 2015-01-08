package com.testify.ecfeed.ui.editor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class TabFolderCommentsSection extends AbstractCommentsSection {

	private class TabFolderSelectionListsner extends AbstractSelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			refreshEditButton();
		}
	}

	private TabFolder fTabFolder;
	private Map<TabItem, Boolean> fEditableIndicator;
	private Map<TabItem, Text> fTextItems;

	public TabFolderCommentsSection(ISectionContext sectionContext, IModelUpdateContext updateContext) {
		super(sectionContext, updateContext);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		getSection().setLayoutData(gd);
		fEditableIndicator = new HashMap<TabItem, Boolean>();
		fTextItems = new HashMap<TabItem, Text>();
	}

	protected TabFolder getTabFolder(){
		return fTabFolder;
	}

	protected void addTabItem(Control control, String title){
		TabItem item = new TabItem(fTabFolder, SWT.NONE);
		item.setText(title);
		item.setControl(control);
	}

	protected TabItem addTextTab(String title, boolean editable){
		Text text = getToolkit().createText(getTabFolder(), "", SWT.WRAP | SWT.V_SCROLL | SWT.MULTI | SWT.READ_ONLY);
		text.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
		TabItem item = new TabItem(fTabFolder, SWT.NONE);
		item.setText(title);
		item.setControl(text);
		fEditableIndicator.put(item, editable);
		fTextItems.put(item, text);
		return item;
	}

	@Override
	public void refresh(){
		super.refresh();
		refreshEditButton();
	}

	private void refreshEditButton() {
		int selectedTabIndex = fTabFolder.getSelectionIndex();
		TabItem selectedItem = fTabFolder.getItem(selectedTabIndex);
		if(selectedTabIndex != -1 && fEditableIndicator.get(selectedItem) != null){
			getEditButton().setEnabled(fEditableIndicator.get(selectedItem));
		}
	}

	@Override
	protected Control createCommentsControl(Composite parent) {
		fTabFolder = new TabFolder(getMainControlComposite(), SWT.BOTTOM);
		fTabFolder.addSelectionListener(new TabFolderSelectionListsner());
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.heightHint = 100;
		fTabFolder.setLayoutData(gd);
		return fTabFolder;
	}

	protected TabItem getActiveItem(){
		return fTabFolder.getItem(fTabFolder.getSelectionIndex());
	}

	protected Text getTextFromTabItem(TabItem item){
		return fTextItems.get(item);
	}
}