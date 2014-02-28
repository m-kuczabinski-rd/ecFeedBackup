package com.testify.ecfeed.ui.editor.modeleditor;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;

import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ExpectedValueCategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.ui.dialogs.TestMethodRenameDialog;

public class MethodDetailsPage extends BasicDetailsPage {

	private MethodNode fSelectedMethod;
	private ParametersViewer fParemetersSection;
	private ConstraintsViewer fConstraintsSection;
	private TestCasesViewer fTestCasesSection;
	
	private class ReassignAdapter extends SelectionAdapter{

		@Override
		public void widgetSelected(SelectionEvent e){
			TestMethodRenameDialog dialog = new TestMethodRenameDialog(getActiveShell(), fSelectedMethod);
			if(dialog.open() == IDialogConstants.OK_ID){
				MethodNode selectedMethod = dialog.getSelectedMethod();
				fSelectedMethod.setName(selectedMethod.getName());
				updateParemeters(selectedMethod);
				modelUpdated(null);
			}
		}

		private void updateParemeters(MethodNode newMethod) {
			List<CategoryNode> srcParameters = newMethod.getCategories();
			for(int i = 0; i < srcParameters.size(); i++){
				updateParameter(i, srcParameters.get(i));
			}
		}
		
		private void updateParameter(int index, CategoryNode newCategory){
			boolean isOriginalCategoryExpected = fSelectedMethod.getCategories().get(index) 
					instanceof ExpectedValueCategoryNode;
			boolean isNewCategoryExpected = newCategory instanceof ExpectedValueCategoryNode;
			if(isOriginalCategoryExpected == isNewCategoryExpected){
				fSelectedMethod.getCategories().get(index).setName(newCategory.getName());
			}
			else{
				fSelectedMethod.replaceCategory(index, newCategory);
			}
		}
	}
	
	public MethodDetailsPage(ModelMasterSection masterSection) {
		super(masterSection);
	}

	public MethodNode getSelectedMethod() {
		return fSelectedMethod;
	}

	@Override
	public void createContents(Composite parent){
		super.createContents(parent);

		getMainSection().setText("Method details");
		createTextClient();
		
		addForm(fParemetersSection = new ParametersViewer(this, getToolkit()));
		addForm(fConstraintsSection = new ConstraintsViewer(this, getToolkit()));
		addForm(fTestCasesSection = new TestCasesViewer(this, getToolkit()));
		
		getToolkit().paintBordersFor(getMainComposite());
	}

	private void createTextClient() {
		Composite buttonsComposite = getToolkit().createComposite(getMainSection());
		RowLayout rl = new RowLayout();
		rl.fill = true;
		buttonsComposite.setLayout(rl);
		getMainSection().setTextClient(buttonsComposite);
		Button reassignButton = getToolkit().createButton(buttonsComposite, "Reassign", SWT.NONE);
		reassignButton.addSelectionListener(new ReassignAdapter());
		Button testOnlineButton = getToolkit().createButton(buttonsComposite, "Test online", SWT.NONE);
		testOnlineButton.addSelectionListener(new ExecuteOnlineTestAdapter(this));
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		super.selectionChanged(part, selection);
		if(getSelectedElement() instanceof MethodNode){
			fSelectedMethod = (MethodNode)getSelectedElement();
		}
		refresh();
	}
	
	@Override
	public void refresh(){
		if(fSelectedMethod != null){
			getMainSection().setText(fSelectedMethod.toString());
			fParemetersSection.setInput(fSelectedMethod);
			fConstraintsSection.setInput(fSelectedMethod);
			fTestCasesSection.setInput(fSelectedMethod);
		}
		super.refresh();
	}

}
