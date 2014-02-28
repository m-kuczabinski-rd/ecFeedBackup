package com.testify.ecfeed.ui.editor.modeleditor.rework;

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.dialogs.TestClassSelectionDialog;

public class ClassDetailsPage extends BasicDetailsPage {

	private ClassNode fSelectedClass;
	private QualifiedNameForm fQualifiedNameForm;
	private MethodsViewer fMethodsSection;
	private OtherMethodsViewer fOtherMethodsSection;
	
	private class QualifiedNameForm extends TextForm{

		public QualifiedNameForm(Composite parent, FormToolkit toolkit) {
			super(parent, toolkit, "Qualified name: ", "Reassign");
		}

		@Override
		protected Control createEntry(Composite parent, int style, GridData gridData) {
			Text text = super.createText(parent, style, gridData);
			text.setEditable(false);
			return text;
		}

		@Override
		public void buttonSelected(SelectionEvent e){
			IType selectedClass = selectClass();

			if(selectedClass != null){
				String qualifiedName = selectedClass.getFullyQualifiedName();
				if(fSelectedClass.getRoot().getClassModel(qualifiedName) == null){
					fSelectedClass.setName(qualifiedName);
					modelUpdated(this);
				}
				else{
					MessageDialog.openInformation(getActiveShell(), 
							Messages.DIALOG_CLASS_EXISTS_TITLE, 
							Messages.DIALOG_CLASS_EXISTS_MESSAGE);
				}
			}
		}
		
		private IType selectClass() {
			TestClassSelectionDialog dialog = new TestClassSelectionDialog(Display.getDefault().getActiveShell());
			
			if (dialog.open() == IDialogConstants.OK_ID) {
				return (IType)dialog.getFirstResult();
			}
			return null;
		}
	}

	public ClassDetailsPage(ModelMasterSection masterSection) {
		super(masterSection);
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);

		Composite textClientComposite = getToolkit().createComposite(getMainSection());
		textClientComposite.setLayout(new RowLayout());
		
		Button refreshButton = getToolkit().createButton(textClientComposite, "Refresh", SWT.NONE);
		refreshButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e){
				refresh();
			}
		});
		getMainSection().setTextClient(textClientComposite);
		
		addForm(fQualifiedNameForm = new QualifiedNameForm(getMainComposite(), getToolkit()));
		addForm(fMethodsSection = new MethodsViewer(this, getToolkit()));
		addForm(fOtherMethodsSection = new OtherMethodsViewer(this, getToolkit()));
		
		getToolkit().paintBordersFor(getMainComposite());
	}

	
	
	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		super.selectionChanged(part, selection);
		if(getSelectedElement() instanceof ClassNode){
			fSelectedClass = (ClassNode)getSelectedElement();
		}
		refresh();
	}
	
	@Override
	public void refresh(){
		super.refresh();
		if(fSelectedClass != null){
			getMainSection().setText(fSelectedClass.getLocalName());
			fQualifiedNameForm.setText(fSelectedClass.getQualifiedName());
			fMethodsSection.setInput(fSelectedClass);
			fOtherMethodsSection.setInput(fSelectedClass);
			getMainSection().layout();
		}
	}

}
