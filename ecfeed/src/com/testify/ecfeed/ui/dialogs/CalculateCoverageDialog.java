package com.testify.ecfeed.ui.dialogs;

/*******************************************************************************
 * Copyright (c) 2014 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Michal Gluszko (m.gluszko(at)radytek.com) - initial implementation
 ******************************************************************************/

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.TreeCheckStateListener;
import com.testify.ecfeed.ui.editor.CoverageCalculator;
import com.testify.ecfeed.ui.editor.TestCaseViewerContentProvider;
import com.testify.ecfeed.ui.editor.TestCasesLabelProvider;

public class CalculateCoverageDialog extends TitleAreaDialog {
	private Button fOkButton;
	private CheckboxTreeViewer fTestCasesTreeViewer;
	private Composite fMainContainer;

	private Canvas[] fCanvasSet;
	private CoverageCalculator fCalculator;

	private final String fTitle = Messages.DIALOG_CALCULATE_COVERAGE_TITLE;
	private final String fMessage = Messages.DIALOG_CALCULATE_COVERAGE_MESSAGE;
	private MethodNode fMethod;

	public CalculateCoverageDialog(Shell parentShell, MethodNode method) {
		super(parentShell);
		setHelpAvailable(false);
		setShellStyle(SWT.BORDER | SWT.RESIZE | SWT.TITLE);
		fMethod = method;
		fCalculator = new CoverageCalculator(fMethod.getCategories());
	}

	@Override
	public Point getInitialSize() {
		return new Point(600, 800);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		fOkButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
		fOkButton.setEnabled(true);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(fTitle);
		setMessage(fMessage);
		Composite area = (Composite) super.createDialogArea(parent);
		fMainContainer = new Composite(area, SWT.NONE);
		fMainContainer.setLayout(new GridLayout(1, false));
		fMainContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createTestCaseComposite(fMainContainer);
		createCoverageGraphComposite(fMainContainer);

		return area;
	}

	private void createTestCaseComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		GridData griddata = new GridData(SWT.FILL, SWT.FILL, true, false);
		griddata.minimumHeight = 250;
		griddata.grabExcessVerticalSpace = true;
		composite.setLayoutData(griddata);

		Label selectTestCasesLabel = new Label(composite, SWT.WRAP);
		selectTestCasesLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		selectTestCasesLabel.setText(Messages.DIALOG_CALCULATE_COVERAGE_MESSAGE);

		createTestCaseViewer(composite);
	}

	private void createTestCaseViewer(Composite parent) {
		Tree tree = new Tree(parent, SWT.CHECK | SWT.BORDER);
		tree.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		fTestCasesTreeViewer = new CheckboxTreeViewer(tree);
		fTestCasesTreeViewer.setContentProvider(new TestCaseViewerContentProvider(fMethod));
		fTestCasesTreeViewer.setLabelProvider(new TestCasesLabelProvider(fMethod));
		fTestCasesTreeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		fTestCasesTreeViewer.setInput(fMethod);
		
		//Add tree state listener preparing data for calculator and reverting tree changes if operation gets cancelled.
		fTestCasesTreeViewer.addCheckStateListener(new CoverageTreeViewerListener(fCalculator, fTestCasesTreeViewer));
	}

	private void createCoverageGraphComposite(Composite parent) {
		ScrolledComposite scrolled = new ScrolledComposite(parent, SWT.BORDER | SWT.FILL | SWT.V_SCROLL);
		GridData scrolledgriddata = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrolledgriddata.minimumWidth = 100;
		scrolledgriddata.minimumHeight = 100;
		scrolled.setLayout(new GridLayout(1, false));
		scrolled.setLayoutData(scrolledgriddata);
		scrolled.setExpandHorizontal(true);
		scrolled.setExpandVertical(true);

		Composite composite = new Composite(scrolled, SWT.BORDER | SWT.FILL);
		composite.setLayout(new GridLayout(1, false));
		GridData griddata = new GridData(SWT.FILL, SWT.FILL, true, true);
		griddata.minimumHeight = 100;
		griddata.grabExcessVerticalSpace = true;
		composite.setLayoutData(griddata);
		createCoverageGraphViewer(composite);

		scrolled.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrolled.setContent(composite);
		final ScrollBar vBar = scrolled.getVerticalBar();
		SelectionListener listener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				drawBarGraph();
			}
		};
		vBar.addSelectionListener(listener);
	}

	private void createCoverageGraphViewer(Composite parent) {
		fCanvasSet = new Canvas[fCalculator.getN()];
		for (int n = 0; n < fCalculator.getN(); n++) {
			fCanvasSet[n] = new Canvas(parent, SWT.FILL);
			fCanvasSet[n].setSize(getInitialSize().x, 40);
			GridData griddata = new GridData(SWT.FILL, SWT.FILL, true, false);
			griddata.minimumHeight = 40;
			griddata.grabExcessVerticalSpace = true;
			fCanvasSet[n].setLayoutData(griddata);
		}

		parent.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event e) {
				drawBarGraph();
			}
		});
	}

	
	public class CoverageTreeViewerListener extends TreeCheckStateListener {
		private CheckboxTreeViewer fViewer;
		CoverageCalculator fCalculator;
		List<TestCaseNode> fTestCases;
		String fTestSuiteName;
		boolean fIsSelection;
		// saved tree state
		Object fTreeState[];

		public CoverageTreeViewerListener(CoverageCalculator calculator, CheckboxTreeViewer treeViewer) {
			super(treeViewer);
			this.fCalculator = calculator;
			fViewer = treeViewer;
			fTestCases = new ArrayList<>();
			fTreeState = fViewer.getCheckedElements();
		}

		public void revertLastTreeChange() {
			fViewer.setCheckedElements(fTreeState);
		}

		@Override
		public void checkStateChanged(CheckStateChangedEvent event) {
			Object element = event.getElement();
			fIsSelection = event.getChecked();
			// if action is selection
			if (fIsSelection) {
				// TestSuite
				if (element instanceof String) {
					fTestCases.clear();
					fTestSuiteName = (String) element;
					fTestCases.addAll(getMethod().getTestCases(fTestSuiteName));
				}
				// TestCaseNode
				else {
					fTreeState = null;
					fTestCases.clear();
					fTestSuiteName = null;
					fTestCases.add((TestCaseNode) element);
				}
			}
			// if action is deselection
			else {
				// TestSuite
				if (element instanceof String) {
					fTestCases.clear();
					fTestSuiteName = (String) element;

					// if parent is grayed
					for (Object tcase : fTreeState) {
						if (fTestSuiteName.equals(fContentProvider.getParent(tcase))) {
							fTestCases.add((TestCaseNode) tcase);
						}
					}
					// if parent has no children shown in the tree, but they all
					// are implicitly selected
					if (fTestCases.isEmpty()) {
						fTestCases.addAll(getMethod().getTestCases(fTestSuiteName));
					}
				}
				// TestCaseNode
				else {
					fTreeState = null;
					fTestCases.clear();
					fTestSuiteName = null;
					fTestCases.add((TestCaseNode) element);
				}
			}

			fViewer.setSubtreeChecked(element, fIsSelection);
			setParentGreyed(element);
			if (fViewer.getCheckedElements().length == 0) {
				fCalculator.setCurrentChangedCases(null, fIsSelection);
			} else {
				fCalculator.setCurrentChangedCases(fTestCases, fIsSelection);
			}

			// Execute core calculator function
			if (fCalculator.calculateCoverage()) {
				// if succeed - save changes to the tree
				fTreeState = fViewer.getCheckedElements();
				drawBarGraph();
			} else {
				revertLastTreeChange();
			}
		}

	}
	
	private void drawBarGraph() {
		if (fCalculator.getN() != 0 && fCalculator.getN() > 0) {
			for (int n = 0; n < fCalculator.getN(); n++) {
				Display display = Display.getCurrent();
				Canvas fCanvas = fCanvasSet[n];
				fCanvas.setSize(fCanvas.getParent().getSize().x, 40);

				GC gc = new GC(fCanvas);
				gc.setBackground(display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
				gc.fillRectangle(0, 0, fCanvas.getSize().x, fCanvas.getSize().y);

				int spacing = 5;
				int width = fCanvas.getSize().x - spacing * 8;
				int height = fCanvas.getSize().y;

				// Clear the canvas
				Color outworldTeal = new Color(display, 16, 224, 224);
				gc.setBackground(outworldTeal);
				gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));

				double widthunit = (double) width / 100;
				int fontsize = (int) (height / 4);
				Font font = new Font(display, display.getSystemFont().getFontData()[0].getName(), fontsize, 1);
				gc.setFont(font);

				int linewidth = 2;
				int topborder;
				int topbarborder;
				int bottomborder = 0;
				int fontspacing = (fontsize * 8) / 5;

				topborder = 0;
				topbarborder = topborder + fontspacing;
				bottomborder = height - spacing;

				gc.fillRectangle(0, topbarborder, (int) (fCalculator.getResults()[n] * widthunit), bottomborder - topbarborder);
				gc.setLineWidth(linewidth);
				gc.drawLine(linewidth, bottomborder, (int) width - linewidth, bottomborder);
				gc.drawLine(linewidth / 2, topbarborder, linewidth / 2, bottomborder);
				gc.drawLine((int) (width) - linewidth / 2, topbarborder, (int) (width) - linewidth / 2, bottomborder);

				DecimalFormat df = new DecimalFormat("#.00");
				String nlabel = "N= " + (n + 1);
				String percentvalue = df.format(fCalculator.getResults()[n]) + "%";
				gc.drawString(nlabel, 10, topborder, true);
				gc.drawString(percentvalue, (width / 2) - fontspacing, (int) (topbarborder), true);
				font.dispose();
				outworldTeal.dispose();
				gc.dispose();
			}
		}
	}
	
	private MethodNode getMethod(){
		return fMethod;
	}

}
