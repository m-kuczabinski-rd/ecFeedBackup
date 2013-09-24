package com.testify.ecfeed.ui.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import com.testify.ecfeed.api.IConstraint;
import com.testify.ecfeed.api.ITestGenAlgorithm;
import com.testify.ecfeed.constants.Constants;
import com.testify.ecfeed.constants.DialogStrings;
import com.testify.ecfeed.model.CategoryNode;
import com.testify.ecfeed.model.ConstraintNode;
import com.testify.ecfeed.model.ExpectedValueCategoryNode;
import com.testify.ecfeed.model.GenericNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.ui.common.TreeCheckStateListener;
import com.testify.ecfeed.utils.EcModelUtils;

public class GenerateTestSuiteDialog extends TitleAreaDialog {
	private Combo fTestSuiteCombo;
	private Combo fAlgorithmCombo;
	private Button fOkButton;
	private ITestGenAlgorithm fSelectedAlgorithm;
	private Map<String, ITestGenAlgorithm> fAvaliableAlgorithms;
	private MethodNode fMethod;
	private String fTestSuiteName;
	private CheckboxTreeViewer fCategoriesViewer;
	private CheckboxTreeViewer fConstraintsViewer;
	@SuppressWarnings("rawtypes")
	private ArrayList[] fAlgorithmInput;
	private IConstraint[] fConstraints;

	private class CategoriesContentProvider extends TreeNodeContentProvider implements ITreeContentProvider{
		private final Object[] EMPTY_ARRAY = new Object[]{};
		
		@Override
		public Object[] getElements(Object input){
			if(input instanceof MethodNode){
				return ((MethodNode)input).getCategories().toArray();
			}
			return null;
		}
		
		public Object[] getChildren(Object element){
			if(element instanceof CategoryNode){
				return ((CategoryNode)element).getPartitions().toArray();
			}
			return EMPTY_ARRAY;
		}
		
		@Override
		public Object getParent(Object element){
			if(element instanceof GenericNode){
				return ((GenericNode)element).getParent();
			}
			return null;
		}
		
		@Override
		public boolean hasChildren(Object element){
			return getChildren(element).length > 0;
		}
	}
	
	private class ConstraintsViewerContentProvider extends TreeNodeContentProvider implements ITreeContentProvider{
		private final Object[] EMPTY_ARRAY = new Object[]{};
		
		@Override
		public Object[] getElements(Object input){
			if(input instanceof MethodNode){
				return fMethod.getConstraintsNames().toArray();
			}
			return EMPTY_ARRAY;
		}
		
		public Object[] getChildren(Object element){
			if(element instanceof String){
				return fMethod.getConstraints((String)element).toArray();
			}
			return EMPTY_ARRAY;
		}
		
		@Override
		public Object getParent(Object element){
			if(element instanceof ConstraintNode){
				return ((ConstraintNode)element).getName();
			}
			return null;
		}
		
		@Override
		public boolean hasChildren(Object element){
			return getChildren(element).length > 0;
		}
	}
	
	public GenerateTestSuiteDialog(Shell parentShell, MethodNode method) {
		super(parentShell);
		setHelpAvailable(false);
		setShellStyle(SWT.BORDER | SWT.RESIZE | SWT.TITLE);
		fMethod = method;
		fAvaliableAlgorithms = getAvailableAlgorithms();
	}
	
	public ITestGenAlgorithm getSelectedAlgorithm() {
		return fSelectedAlgorithm;
	}

	public String getTestSuiteName(){
		return fTestSuiteName;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(DialogStrings.DIALOG_GENERATE_TEST_SUITE_TITLE);
		setMessage(DialogStrings.DIALOG_GENERATE_TEST_SUITE_MESSAGE);
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createConstraintsComposite(container);
		
		createPartitionsComposite(container);
		
		createTestSuiteComposite(container);
		
		createAlgorithmSelectionComposite(container);
		
		return area;
	}

	private void createConstraintsComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label selectConstraintsLabel = new Label(composite, SWT.NONE);
		selectConstraintsLabel.setText(DialogStrings.DIALOG_GENERATE_TEST_SUITES_SELECT_CONSTRAINTS_LABEL);
		
		createConstraintsViewer(composite);
		
		createConstraintsButtons(composite);
	}

	private void createConstraintsViewer(Composite parent) {
		Tree tree = new Tree(parent, SWT.CHECK|SWT.BORDER);
		tree.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		fConstraintsViewer = new CheckboxTreeViewer(tree);
		fConstraintsViewer.setContentProvider(new ConstraintsViewerContentProvider());
		fConstraintsViewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element){
				if(element instanceof String){
					return (String)element;
				}
				if(element instanceof ConstraintNode){
					return ((ConstraintNode)element).getConstraint().toString();
				}
				return null;
			}
		});
		fConstraintsViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		fConstraintsViewer.setInput(fMethod);
		fConstraintsViewer.addCheckStateListener(new TreeCheckStateListener(fConstraintsViewer));
	}

	private void createConstraintsButtons(Composite parent) {
		Composite buttonsComposite = new Composite(parent, SWT.NONE);
		buttonsComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		Button checkAllButton = new Button(buttonsComposite, SWT.NONE);
		checkAllButton.setText("Check all");
		checkAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				for(String name : fMethod.getConstraintsNames()){
					fConstraintsViewer.setSubtreeChecked(name, true);
				}
			}
		});
		
		Button uncheckAllButton = new Button(buttonsComposite, SWT.NONE);
		uncheckAllButton.setText("Uncheck all");
		uncheckAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				for(String name : fMethod.getConstraintsNames()){
					fConstraintsViewer.setSubtreeChecked(name, false);
				}
			}
		});
	}

	private void createPartitionsComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label selectPartitionsLabel = new Label(composite, SWT.WRAP);
		selectPartitionsLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		selectPartitionsLabel.setText(DialogStrings.DIALOG_GENERATE_TEST_SUITES_SELECT_PARTITIONS_LABEL);
		
		createPartitionsViewer(composite);
	}

	private void createPartitionsViewer(Composite parent) {
		Tree tree = new Tree(parent, SWT.CHECK|SWT.BORDER);
		tree.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		fCategoriesViewer = new CheckboxTreeViewer(tree);
		fCategoriesViewer.setContentProvider(new CategoriesContentProvider());
		fCategoriesViewer.setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element){
				if(element instanceof GenericNode){
					return ((GenericNode)element).getName();
				}
				return null;
			}
		});
		fCategoriesViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		fCategoriesViewer.setInput(fMethod);
		for(CategoryNode category : fMethod.getCategories()){
			fCategoriesViewer.setSubtreeChecked(category, true);
		}
		fCategoriesViewer.addCheckStateListener(new TreeCheckStateListener(fCategoriesViewer));
		fCategoriesViewer.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				for(CategoryNode category : fMethod.getCategories()){
					if(fCategoriesViewer.getChecked(category) == false){
						setOkButton(false);
						return;
					}
					setOkButton(true);
				}
			}
		});
	}

	private void createTestSuiteComposite(Composite container) {
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label testSuiteLabel = new Label(composite, SWT.NONE);
		testSuiteLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		testSuiteLabel.setText("Test suite");
		
		ComboViewer testSuiteViewer = new ComboViewer(composite, SWT.NONE);
		fTestSuiteCombo = testSuiteViewer.getCombo();
		fTestSuiteCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fTestSuiteCombo.setItems(fMethod.getTestSuites().toArray(new String[]{}));
		fTestSuiteCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				validateTestSuiteName();
			}
		});
		fTestSuiteCombo.setText(Constants.DEFAULT_TEST_SUITE_NAME);
	}

	private void validateTestSuiteName() {
		if(!EcModelUtils.validateTestSuiteName(fTestSuiteCombo.getText())){
			setErrorMessage(DialogStrings.DIALOG_TEST_SUITE_NAME_PROBLEM_MESSAGE);
			setOkButton(false);
		}
		else{
			setErrorMessage(null);
			setOkButton(true);
			fTestSuiteName = fTestSuiteCombo.getText();
		}
	}

	private void setOkButton(boolean enabled) {
		if(fOkButton != null && !fOkButton.isDisposed()){
			fOkButton.setEnabled(enabled);
		}
	}

	private void createAlgorithmSelectionComposite(Composite container) {
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label testSuiteLabel = new Label(composite, SWT.NONE);
		testSuiteLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		testSuiteLabel.setText("Algorithm");
		
		ComboViewer algorithmViewer = new ComboViewer(composite, SWT.READ_ONLY);
		String[] algorithmsNames = fAvaliableAlgorithms.keySet().toArray(new String[]{}); 
		fAlgorithmCombo = algorithmViewer.getCombo();
		fAlgorithmCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fAlgorithmCombo.setItems(algorithmsNames);
		if(fAvaliableAlgorithms.size() != 0){
			fAlgorithmCombo.setText(algorithmsNames[0]);
			fSelectedAlgorithm = fAvaliableAlgorithms.get(algorithmsNames[0]);
			setOkButton(true);
		}
		else{
			setOkButton(false);
		}
		fAlgorithmCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				fSelectedAlgorithm = fAvaliableAlgorithms.get(fAlgorithmCombo.getText());
			}
		});
	}

	private Map<String, ITestGenAlgorithm> getAvailableAlgorithms() {
		Map<String, ITestGenAlgorithm> result = new HashMap<String, ITestGenAlgorithm>();
		
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = 
				reg.getConfigurationElementsFor(Constants.TEST_GEN_ALGORITHM_EXTENSION_POINT_ID);
		for(IConfigurationElement element : extensions){
			try {
				String algorithmName = element.getAttribute(Constants.ALGORITHM_NAME_ATTRIBUTE);
				ITestGenAlgorithm implementation = (ITestGenAlgorithm)element.createExecutableExtension(Constants.TEST_GEN_ALGORITHM_IMPLEMENTATION_ATTRIBUTE);
				if(algorithmName != null && implementation != null){
					result.put(algorithmName, implementation);
				}
			} catch (CoreException e) {
				System.out.println("Exception: " + e.getMessage());
				continue;
			}
		}
		return result;
	}

	
	@Override
	public Point getInitialSize(){
		return new Point(600, 800);
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		fOkButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}
	
	@Override
	public void okPressed(){
		saveAlgorithmInput();
		saveConstraints();
		super.okPressed();
	}
	
	private void saveConstraints() {
		Object[] checkedObjects = fConstraintsViewer.getCheckedElements();
		ArrayList<IConstraint> constraints = new ArrayList<IConstraint>();
		for(Object o : checkedObjects){
			if(o instanceof ConstraintNode){
				constraints.add((ConstraintNode)o);
			}
		}
		
		fConstraints = constraints.toArray(new IConstraint[]{});
	}

	private void saveAlgorithmInput() {
		ArrayList<CategoryNode> categories = fMethod.getCategories();
		fAlgorithmInput = new ArrayList[categories.size()];
		for(int i = 0; i < categories.size(); i++){
			ArrayList<PartitionNode> partitions = new ArrayList<PartitionNode>();
			if(fMethod.isExpectedValueCategory(categories.get(i))){
				ExpectedValueCategoryNode category = (ExpectedValueCategoryNode)categories.get(i);
				partitions.add(category.getDefaultValuePartition());
			}
			else{
				for(PartitionNode partition : categories.get(i).getPartitions()){
					if(fCategoriesViewer.getChecked(partition)){
						partitions.add(partition);
					}
				}
			}
			fAlgorithmInput[i] = partitions;
		}
	}

	@SuppressWarnings("rawtypes")
	public ArrayList[] getAlgorithmInput(){
		return fAlgorithmInput;
	}
	
	public IConstraint[] getConstraints(){
		return fConstraints;
	}
}
