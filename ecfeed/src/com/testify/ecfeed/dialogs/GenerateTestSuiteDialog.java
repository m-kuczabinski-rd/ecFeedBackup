package com.testify.ecfeed.dialogs;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.testify.ecfeed.api.ITestGenAlgorithm;
import com.testify.ecfeed.constants.Constants;
import com.testify.ecfeed.constants.DialogStrings;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.utils.EcModelUtils;

public class GenerateTestSuiteDialog extends TitleAreaDialog {
	private Combo fTestSuiteCombo;
	private Combo fAlgorithmCombo;
	private Button fOkButton;
	private ITestGenAlgorithm fSelectedAlgorithm;
	private Map<String, ITestGenAlgorithm> fAvaliableAlgorithms;
	private MethodNode fMethod;
	private String fTestSuiteName;

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
		
		createTestSuiteComposite(container);
		
		createAlgorithmSelectionComposite(container);
		
		return area;
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
		fTestSuiteCombo.setText(Constants.DEFAULT_TEST_SUITE_NAME);
		fTestSuiteCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				validateTestSuiteName();
			}
		});
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

}
