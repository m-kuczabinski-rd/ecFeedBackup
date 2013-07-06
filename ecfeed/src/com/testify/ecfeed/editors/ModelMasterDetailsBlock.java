package com.testify.ecfeed.editors;

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import org.eclipse.swt.layout.GridLayout;

import com.testify.ecfeed.dialogs.ModelSettingsDialog;
import com.testify.ecfeed.dialogs.TestClassSelectionDialog;
import com.testify.ecfeed.editor.outline.EcContentProvider;
import com.testify.ecfeed.editor.outline.EcLabelProvider;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.utils.EcModelUtils;

import org.eclipse.swt.layout.GridData;
import swing2swt.layout.FlowLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ModelMasterDetailsBlock extends MasterDetailsBlock implements IModelUpdateListener{

	private FormToolkit toolkit;
	private RootNode fModel;
	private EcMultiPageEditor fEditor;
	private Section fMasterPartSection;
	private TreeViewer fTreeViewer;
	
	private class AddTestClassButtonSelectionAdapter extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			IType selectedClass = selectClass();

			if(selectedClass != null){
				ClassNode classNode = EcModelUtils.generateClassModel(selectedClass);
				if(!EcModelUtils.classExists(fModel, classNode)){
					fModel.addClass(classNode);
				}
				fEditor.updateModel(fModel);
			}
		}

		private IType selectClass() {
			TestClassSelectionDialog dialog = new TestClassSelectionDialog(Display.getDefault().getActiveShell());
			
			if (dialog.open() == Window.OK) {
				return (IType)dialog.getFirstResult();
			}
			return null;
		}
	}

	private class RenameModelButtonSelectionAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			ModelSettingsDialog dialog = new ModelSettingsDialog(Display.getDefault().getActiveShell(), fModel);
			dialog.create();
			dialog.create();
			
			if (dialog.open() == Window.OK) {
				String name = dialog.getName();
				if(!name.equals(fModel.getName())){
					fModel.setName(name);
					
					fEditor.updateModel(fModel);
					fMasterPartSection.setText(fModel.getName());
				}
			}
		}
	}

	/**
	 * Create the master details block.
	 */
	public ModelMasterDetailsBlock(EcMultiPageEditor editor, RootNode model) {
		fEditor = editor;
		fModel = model;
		editor.registerModelUpdateListener(this);
	}

	/**
	 * Create contents of the master details block.
	 * @param managedForm
	 * @param parent
	 */
	@Override
	protected void createMasterPart(IManagedForm managedForm, Composite parent) {
		toolkit = managedForm.getToolkit();
		fMasterPartSection = toolkit.createSection(parent,
				ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR);
		fMasterPartSection.setText(fModel.getName());
		
		createRenameButton();
		createTreeEditorBlock();
	}

	private void createRenameButton() {
		Button btnRename = new Button(fMasterPartSection, SWT.NONE);
		btnRename.addSelectionListener(new RenameModelButtonSelectionAdapter());
		
		fMasterPartSection.setTextClient(btnRename);
		btnRename.setBounds(0, 10, 42, 29);
		toolkit.adapt(btnRename, true, true);
		btnRename.setText("Rename");
	}

	private void createTreeEditorBlock() {
		Composite composite = new Composite(fMasterPartSection, SWT.RIGHT);
		toolkit.adapt(composite);
		toolkit.paintBordersFor(composite);
		fMasterPartSection.setClient(composite);
		composite.setLayout(new GridLayout(1, false));
		
		createAddTestClassButton(composite);
		createTreeViewer(composite);
	}

	private void createAddTestClassButton(Composite composite) {
		Composite cmpButtons = new Composite(composite, SWT.RIGHT);
		toolkit.adapt(cmpButtons);
		toolkit.paintBordersFor(cmpButtons);
		cmpButtons.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
				
		Button btnAddTestClass = new Button(cmpButtons, SWT.NONE);
		toolkit.adapt(btnAddTestClass, true, true);
		btnAddTestClass.setText("Add Test Class...");
		btnAddTestClass.addSelectionListener(new AddTestClassButtonSelectionAdapter());
	}

	private void createTreeViewer(Composite composite) {
		fTreeViewer = new TreeViewer(composite, SWT.FILL);
		fTreeViewer.setAutoExpandLevel(2);
		fTreeViewer.setContentProvider(new EcContentProvider());
		fTreeViewer.setLabelProvider(new EcLabelProvider());
		fTreeViewer.setInput(fModel);
		Tree tree = fTreeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		toolkit.paintBordersFor(tree);
	}

	/**
	 * Register the pages.
	 * @param part
	 */
	@Override
	protected void registerPages(DetailsPart part) {
		// Register the pages
	}

	/**
	 * Create the toolbar actions.
	 * @param managedForm
	 */
	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		// Create the toolbar actions
	}

	@Override
	public void modelUpdated(RootNode model) {
		fMasterPartSection.setText(model.getName());
		fTreeViewer.refresh();
	}
}
