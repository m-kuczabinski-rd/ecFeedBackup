package com.testify.ecfeed.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.testify.ecfeed.core.utils.StringHelper;
	
public class FormObjectToolkit {
	
	private static FormObjectToolkit fInstance = null;
	private static FormToolkit fFormToolkit = null;

	protected FormObjectToolkit() {
	}
	
	public static FormObjectToolkit getInstance(FormToolkit formToolkit) {
		if (fInstance == null) {
			fInstance = new FormObjectToolkit();
			fFormToolkit = formToolkit;
		}
		return fInstance;
	}

	public void paintBorders(Composite composite) {
		fFormToolkit.paintBordersFor(composite);
	}

	public Composite createGridComposite(Composite parentComposite, int countOfColumns) {
		Composite composite = fFormToolkit.createComposite(parentComposite);
		
		composite.setLayout(new GridLayout(countOfColumns, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		return composite;
	}
	
	public Composite createRowComposite(Composite parentComposite) {
		Composite composite = fFormToolkit.createComposite(parentComposite);
		
		RowLayout rowLayout = new RowLayout();
		composite.setLayout(rowLayout);
		
		return composite;
	}	

	public Label createLabel(Composite parentComposite, String text) {
		return fFormToolkit.createLabel(parentComposite, text, SWT.NONE);
	}
	
	public Label createSpacer(Composite parentComposite, int size) {
		return createLabel(parentComposite, StringHelper.createString(" ", size));
	}
	
	public Text createGridText(Composite parentGridComposite, SelectionListener listener) {
		Text text = fFormToolkit.createText(parentGridComposite, null, SWT.NONE);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		text.addSelectionListener(listener);
		return text;
	}
	
	public Button createButton(Composite parentComposite, String text, SelectionListener selectionListener) {
		Button button = fFormToolkit.createButton(parentComposite, text, SWT.NONE);
		
		if (selectionListener != null) {
			button.addSelectionListener(selectionListener);
		}
		
		return button;
	}
}
