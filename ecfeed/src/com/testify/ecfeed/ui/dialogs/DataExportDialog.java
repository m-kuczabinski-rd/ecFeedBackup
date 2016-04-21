package com.testify.ecfeed.ui.dialogs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class DataExportDialog extends TitleAreaDialog{ // XYX rename to TestCasesExportDialog

	private static final String SECTION_HEADER_REGEX = "\\s*\\[([^]]*)\\]\\s*";
	private static final String COMMENTED_LINE_REGEX = "^\\s*#.*";
	private static final String DEFAULT_TEMPLATE_TEXT_FILE = "res/template.txt"; // XYX rename
	private final String HEADER_TEMPLATE_MARKER = "[Header]";
	private final String TEST_CASE_TEMPLATE_MARKER = "[TestCase]";
	private final String TAIL_TEMPLATE_MARKER = "[Tail]";
	
	public static final String DIALOG_EXPORT_TEST_DATA_TITLE = "Export test data";
	public static final String DIALOG_EXPORT_TEST_DATA_MESSAGE = "Define template for data export and select target file";
	public static final String EXPORT_TEST_DATA_TEMPLATE_LABEL = "Define template for export data.";
	public static final String EXPORT_TEST_DATA_TARGET_FILE_LABEL = "Target file";
	
	private String fHeaderTemplate;
	private String fTestCaseTemplate;
	private String fTailTemplate;
	private Text fTemplateText;
	private Text fTargetFileText;
	private String fTargetFile;
	
	class BrowseAdapter extends SelectionAdapter{
		@Override
		public void widgetSelected(SelectionEvent e){
			FileDialog dialog = new FileDialog(getParentShell());
			fTargetFileText.setText(dialog.open());
		}
	}
	
	public DataExportDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(DIALOG_EXPORT_TEST_DATA_TITLE);
		setMessage(DIALOG_EXPORT_TEST_DATA_MESSAGE);

		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		createTemplateDefinitionContainer(container);
		createTargetFileContainer(container);
		
		return area;

	}

	private void createTemplateDefinitionContainer(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createTemplateComposite(parent);
	}

	private void createTemplateComposite(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(EXPORT_TEST_DATA_TEMPLATE_LABEL);

		fTemplateText = new Text(parent, SWT.WRAP|SWT.MULTI|SWT.BORDER|SWT.V_SCROLL);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.minimumHeight = 300;
		fTemplateText.setLayoutData(gd);
		
		fillTemplateText(DEFAULT_TEMPLATE_TEXT_FILE);

	}

	private void fillTemplateText(String templateFilePath) {
		Bundle bundle = FrameworkUtil.getBundle(this.getClass());
		URL url = FileLocator.find(bundle, new Path(templateFilePath), null);
        BufferedReader in;
        String templateText = "";
		try {
			in = new BufferedReader(new InputStreamReader(url.openStream()));
	        String inputLine;
	        while ((inputLine = in.readLine()) != null){
	            templateText += inputLine + "\n";
	        }
	        in.close();
		} catch (IOException e) {
		}
		fTemplateText.setText(templateText);
	}

	private void createTargetFileContainer(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label targetFileLabel = new Label(parent, SWT.NONE); 
		targetFileLabel.setText(EXPORT_TEST_DATA_TARGET_FILE_LABEL);

		Composite targetFileContainer = new Composite(parent, SWT.NONE);
		targetFileContainer.setLayout(new GridLayout(2, false));
		targetFileContainer.setLayoutData(new GridData(GridData.FILL_BOTH));
		fTargetFileText = new Text(targetFileContainer, SWT.BORDER);
		fTargetFileText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Button browseButton = new Button(targetFileContainer, SWT.NONE);
		browseButton.setText("Browse...");
		browseButton.addSelectionListener(new BrowseAdapter());
	}
	
	@Override
	protected void okPressed(){
		String templateText = fTemplateText.getText();
		Map<String, String> templates = parseTemplate(templateText);
		
		// TODO - REMOVE NEWLINES
		fHeaderTemplate = templates.get(HEADER_TEMPLATE_MARKER.toLowerCase());
		fTestCaseTemplate = templates.get(TEST_CASE_TEMPLATE_MARKER.toLowerCase());
		fTailTemplate = templates.get(TAIL_TEMPLATE_MARKER.toLowerCase());

		fTargetFile = fTargetFileText.getText();
		
		System.out.println("Header:\n" + fHeaderTemplate + "\n");
		System.out.println("Test Case:\n" + fTestCaseTemplate + "\n");
		System.out.println("Tail:\n" + fTailTemplate + "\n");
		
		super.okPressed();
	}

	private Map<String, String> parseTemplate(String templateText) {
		Map<String, String> result = new HashMap<String, String>();
		StringTokenizer tokenizer = new StringTokenizer(templateText, "\n");
		String currentSection = "";
		while(tokenizer.hasMoreTokens()){
			String line = tokenizer.nextToken();
			if(line.matches(SECTION_HEADER_REGEX)){
				int sectionTitleStart = line.indexOf('[');
				int sectionTitleStop = line.indexOf(']') + 1;
				currentSection = line.toLowerCase().substring(sectionTitleStart, sectionTitleStop);
				if(result.containsKey(currentSection) == false){
					result.put(currentSection, "");
				}
			}
			else if(line.matches(COMMENTED_LINE_REGEX) == false){
				result.put(currentSection, result.get(currentSection).concat(line + "\n"));
			}
		}
		return result;
	}

	public String getHeaderTemplate(){
		return fHeaderTemplate;
	}

	public String getTestCaseTemplate(){
		return fTestCaseTemplate;
	}

	public String getTailTemplate(){
		return fTailTemplate;
	}

	public String getTargetFile(){
		return fTargetFile;
	}

}