package com.testify.ecfeed.editors;

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wb.swt.TableViewerColumnSorter;

import com.testify.ecfeed.constants.DialogStrings;
import com.testify.ecfeed.dialogs.RenameModelDialog;
import com.testify.ecfeed.dialogs.TestClassSelectionDialog;
import com.testify.ecfeed.model.RootNode;
import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.utils.EcModelUtils;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.layout.GridData;

public class RootNodeDetailsPage extends GenericNodeDetailsPage{

	private RootNode fSelectedNode;
	private CheckboxTableViewer fClassesViewer;
	private Section fMainSection;

	private class AddTestClassButtonSelectionAdapter extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			IType selectedClass = selectClass();

			if(selectedClass != null){
				ClassNode classNode = EcModelUtils.generateClassModel(selectedClass);
				if(!EcModelUtils.classExists(fSelectedNode, classNode.getQualifiedName())){
					fSelectedNode.addClass(classNode);
					updateModel(fSelectedNode);
				}
				else{
					MessageDialog infoDialog = new MessageDialog(getActiveShell(), 
							DialogStrings.DIALOG_CLASS_EXISTS_TITLE, Display.getDefault().getSystemImage(SWT.ICON_INFORMATION), 
							DialogStrings.DIALOG_CLASS_EXISTS_MESSAGE, MessageDialog.INFORMATION, 
							new String[] {IDialogConstants.OK_LABEL}, 0);
					infoDialog.open();
				}
			}
		}

		private IType selectClass() {
			TestClassSelectionDialog dialog = new TestClassSelectionDialog(getActiveShell());
			
			if (dialog.open() == IDialogConstants.OK_ID) {
				return (IType)dialog.getFirstResult();
			}
			return null;
		}
	}

	private class RemoveClassesButtonSelectionAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e) {
			MessageDialog infoDialog = new MessageDialog(getActiveShell(), 
					DialogStrings.DIALOG_REMOVE_CLASSES_TITLE, 
					Display.getDefault().getSystemImage(SWT.ICON_WARNING), 
					DialogStrings.DIALOG_REMOVE_CLASSES_MESSAGE, 
					MessageDialog.QUESTION_WITH_CANCEL, 
					new String[] {IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL}, 
					IDialogConstants.OK_ID);
			if(infoDialog.open() == 0){
				removeClasses(fClassesViewer.getCheckedElements());
			}
		}

		private void removeClasses(Object[] checkedElements) {
			for(Object element : checkedElements){
				if(element instanceof ClassNode){
					fSelectedNode.removeChild((ClassNode)element);
				}
			}
			updateModel(fSelectedNode);
		}
	}


	public RootNodeDetailsPage(ModelMasterDetailsBlock parentBlock){
		super(parentBlock);
	}
	
	/**
	 * Create contents of the details page.
	 * @param parent
	 */
	public void createContents(Composite parent) {
		parent.setLayout(new FillLayout());
		fMainSection = fToolkit.createSection(parent, Section.TITLE_BAR);

		Composite mainComposite = fToolkit.createComposite(fMainSection, SWT.NONE);
		fToolkit.paintBordersFor(mainComposite);
		fMainSection.setClient(mainComposite);
		mainComposite.setLayout(new GridLayout(1, true));
		
		createClassListViewer(mainComposite);
		createBottomButtons(mainComposite);
		createTextClientComposite(fMainSection);
	}

	private void createClassListViewer(Composite composite) {
		Label classesLabel = new Label(composite, SWT.BOLD);
		classesLabel.setText("Test classes");

		fClassesViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER | SWT.FULL_SELECTION |SWT.FILL);
		fClassesViewer.setContentProvider(new ArrayContentProvider());
		fClassesViewer.addDoubleClickListener(new ChildrenViewerDoubleClickListener());
		
		Table table = fClassesViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		fToolkit.paintBordersFor(table);

		TableViewerColumn classViewerColumn = 
				createTableViewerColumn(fClassesViewer, "Class", 150, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((ClassNode)element).getLocalName();
			}
		});
		new TableViewerColumnSorter(classViewerColumn) {
			protected Object getValue(Object o) {
				return ((ClassNode)o).getLocalName();
			}
		};

		TableViewerColumn qualifiedNameViewerColumn = 
				createTableViewerColumn(fClassesViewer, "Qualified name", 150, new ColumnLabelProvider(){
			@Override
			public String getText(Object element){
				return ((ClassNode)element).getQualifiedName();
			}
		});
		new TableViewerColumnSorter(qualifiedNameViewerColumn) {
			protected Object getValue(Object o) {
				return ((ClassNode)o).getLocalName();
			}
		};
	}

	private void createBottomButtons(Composite composite) {
		Composite bottomButtonsComposite = fToolkit.createComposite(composite, SWT.FILL);
		bottomButtonsComposite.setLayout(new GridLayout(2, false));
		bottomButtonsComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		
		createButton(bottomButtonsComposite, "Add Test Class...", new AddTestClassButtonSelectionAdapter());
		createButton(bottomButtonsComposite, "Remove selected classes", new RemoveClassesButtonSelectionAdapter());
	}

	private void createTextClientComposite(Section parentSection) {
		Composite textClient = new Composite(parentSection, SWT.NONE);
		textClient.setLayout(new FillLayout());
		createButton(textClient, "Rename...", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				RenameModelDialog dialog = new RenameModelDialog(Display.getDefault().getActiveShell(), fSelectedNode);
				if(dialog.open() == IDialogConstants.OK_ID){
					fSelectedNode.setName(dialog.getNewName());
					updateModel(fSelectedNode);
				}
			}
		});
		parentSection.setTextClient(textClient);
	}

	@Override
	public void refresh() {
		if(fSelectedNode == null){
			return;
		}
		fClassesViewer.setInput(fSelectedNode.getClasses());
		fMainSection.setText(fSelectedNode.toString());
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		if(structuredSelection.getFirstElement() instanceof RootNode){
			fSelectedNode = (RootNode)structuredSelection.getFirstElement();
		}
		refresh();
	}

	@Override
	public void modelUpdated(RootNode model) {
		fSelectedNode = model;
		refresh();
	}

}
