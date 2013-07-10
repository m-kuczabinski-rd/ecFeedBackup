package com.testify.ecfeed.editors;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import org.eclipse.swt.layout.GridLayout;

import com.testify.ecfeed.editor.outline.EcContentProvider;
import com.testify.ecfeed.editor.outline.EcLabelProvider;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.RootNode;

import org.eclipse.swt.layout.GridData;

public class ModelMasterDetailsBlock extends MasterDetailsBlock implements IModelUpdateListener{

	private FormToolkit fToolkit;
	private RootNode fModel;
	private EcMultiPageEditor fEditor;
	private SectionPart fMasterSectionPart;
	private Section fMasterSection;
	private TreeViewer fTreeViewer;
	
	/**
	 * Create the master details block.
	 */
	public ModelMasterDetailsBlock(EcMultiPageEditor editor, RootNode model) {
		fEditor = editor;
		fModel = model;
		fEditor.registerModelUpdateListener(this);
	}

	/**
	 * Create contents of the master details block.
	 * @param managedForm
	 * @param parent
	 */
	@Override
	protected void createMasterPart(IManagedForm managedForm, Composite parent) {
		fToolkit = managedForm.getToolkit();

		fMasterSection = fToolkit.createSection(parent,
				ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR);
		fMasterSectionPart = new SectionPart(fMasterSection, fToolkit, 0);
		fMasterSection.setText("Structure");
		
		createTreeEditorBlock();
	}

	private void createTreeEditorBlock() {
		Composite composite = new Composite(fMasterSection, SWT.RIGHT);
		fToolkit.adapt(composite);
		fToolkit.paintBordersFor(composite);
		fMasterSection.setClient(composite);
		composite.setLayout(new GridLayout(1, false));
		
		createTreeViewer(composite);
	}

	private void createTreeViewer(Composite composite) {
		FilteredTree filteredTree = new FilteredTree(composite, SWT.BORDER, new PatternFilter(), true);
		fTreeViewer = filteredTree.getViewer();
		fTreeViewer.setAutoExpandLevel(2);
		fTreeViewer.setContentProvider(new EcContentProvider());
		fTreeViewer.setLabelProvider(new EcLabelProvider());
		fTreeViewer.setInput(fEditor);

		fTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				detailsPart.selectionChanged(fMasterSectionPart, event.getSelection());
			}
		});

		
		Tree tree = fTreeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		fToolkit.paintBordersFor(tree);
	}

	/**
	 * Register the pages.
	 * @param part
	 */
	@Override
	protected void registerPages(DetailsPart part) {
		part.registerPage(RootNode.class, new RootNodeDetailsPage(fEditor, this));

		selectNode(fModel);
	}

	/**
	 * Create the toolbar actions.
	 * @param managedForm
	 */
	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
	}

	@Override
	public void modelUpdated(RootNode model) {
		fMasterSection.setText(model.getName());
		fTreeViewer.refresh();
	}
	
	void selectNode(GenericNode node){
		fTreeViewer.setSelection(new StructuredSelection(node), true);
	}
}
