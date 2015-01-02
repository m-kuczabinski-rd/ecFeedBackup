package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;

import com.testify.ecfeed.model.AbstractNode;
import com.testify.ecfeed.ui.modelif.IModelUpdateContext;

public class TabFolderSection extends BasicSection {

	private final static int STYLE = Section.TITLE_BAR | Section.COMPACT | Section.TWISTIE;

	private TabFolder fTabFolder;

	private AbstractNode fSelectedNode;

	protected class DescriptionTextModifyListener extends AbstractSelectionAdapter{

		private Text fSource;

		public DescriptionTextModifyListener(Text source){
			fSource = source;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			getSelectedNode().setDescription(fSource.getText());
		}
	}

	public TabFolderSection(ISectionContext sectionContext, IModelUpdateContext updateContext, String title) {
		super(sectionContext, updateContext, STYLE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		getSection().setLayoutData(gd);
		getSection().setText(title);
	}

	@Override
	protected Composite createClientComposite() {
		Composite client = super.createClientComposite();
		fTabFolder = new TabFolder(client, SWT.BOTTOM);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.heightHint = 100;
		fTabFolder.setLayoutData(gd);
		return client;
	}

	protected TabFolder getTabFolder(){
		return fTabFolder;
	}

	protected void addTabItem(Control control, String title){
		TabItem item = new TabItem(fTabFolder, SWT.NONE);
		item.setText(title);
		item.setControl(control);
	}

	protected Text addTextTab(String title){
		Text text = getToolkit().createText(getTabFolder(), "", SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		TabItem item = new TabItem(fTabFolder, SWT.NONE);
		item.setText(title);
		item.setControl(text);
		return text;
	}

	public void setInput(AbstractNode input){
		fSelectedNode = input;
	}

	protected AbstractNode getSelectedNode(){
		return fSelectedNode;
	}
}
