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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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

import com.testify.ecfeed.abstraction.IImplementationStatusResolver;
import com.testify.ecfeed.abstraction.java.JavaImplementationStatusResolver;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.common.EclipseLoaderProvider;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.TestCasesViewerContentProvider;
import com.testify.ecfeed.ui.common.TestCasesViewerLabelProvider;
import com.testify.ecfeed.ui.common.TreeCheckStateListener;

public class CalculateCoverageDialog extends TitleAreaDialog {
	private Canvas[] fCanvasSet;
	private CoverageCalculator fCalculator;
	private MethodNode fMethod;

	//Initial state of the tree viewer
	private final Object[] fInitChecked;
	private final Object[] fInitGrayed;
	private CheckboxTreeViewer fTestCasesViewer;
	private CoverageTreeViewerListener fCheckStateListener;
	private IImplementationStatusResolver fStatusResolver;

	private class CoverageTreeViewerListener extends TreeCheckStateListener {
		// saved tree state
		Object fTreeState[];
	
		public CoverageTreeViewerListener(CheckboxTreeViewer treeViewer) {
			super(treeViewer);
			fTreeState = new Object[0];
		}
	
		@Override
		public void checkStateChanged(CheckStateChangedEvent event) {
			super.checkStateChanged(event);

			Object element = event.getElement();
			boolean checked = event.getChecked();
			Set<TestCaseNode> checkedTestCases;
			
			if(fTestCasesViewer.getCheckedElements().length == 0){
				checkedTestCases = null;
			} else {
				checkedTestCases = getCheckedTestCases(element, checked);
			}

			applyCheckedTestCases(checkedTestCases, checked);
		}
		
		public void applyCheckedTestCases(Collection<TestCaseNode> checkedTestCases,
				boolean checked){
			fCalculator.setCurrentChangedCases(checkedTestCases, checked);
			if(fCalculator.calculateCoverage()){
				fTreeState = getViewer().getCheckedElements();
				drawBarGraph();
			}
			else{
				revertLastTreeChange();
			}
		}
		
		private Set<TestCaseNode> getCheckedTestCases(Object element, boolean checked){
			Set<TestCaseNode> testCases = new HashSet<>();
			
			if (checked) {
				// TestSuite
				if (element instanceof String) {
					String testSuiteName = (String) element;
					testCases.addAll(fMethod.getTestCases(testSuiteName));
				}
				// TestCaseNode
				else {
					testCases.add((TestCaseNode) element);
				}
			}
			// if action is deselection
			else {
				// TestSuite
				if (element instanceof String) {
					String testSuiteName = (String) element;
	
					// if test suite was grayed add all test cases of that suite
					// that were checked
					for (Object tcase : fTreeState) {
						if (testSuiteName.equals(getContentProvider().getParent(tcase))) {
							testCases.add((TestCaseNode) tcase);
						}
					}
					// if parent has no children shown in the tree, but they all
					// are implicitly selected
					if (testCases.isEmpty()) {
						testCases.addAll(fMethod.getTestCases(testSuiteName));
					}
				}
				// TestCaseNode
				else {
					testCases.add((TestCaseNode) element);
				}
			}
			return testCases;
		}

		private void revertLastTreeChange() {
			getViewer().setCheckedElements(fTreeState);
		}
	
	}

	public CalculateCoverageDialog(Shell parentShell, MethodNode method, Object[] checked, Object[] grayed) {
		super(parentShell);
		setHelpAvailable(false);
		setShellStyle(SWT.BORDER | SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);
		fMethod = method;
		fCalculator = new CoverageCalculator(fMethod.getCategories());
		
		fStatusResolver = new JavaImplementationStatusResolver(new EclipseLoaderProvider());
		fInitChecked = checked;
		fInitGrayed = grayed;
	}
	
	@Override
	public Point getInitialSize() {
		return new Point(600, 800);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
		okButton.setEnabled(true);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(Messages.DIALOG_CALCULATE_COVERAGE_TITLE);
		setMessage(Messages.DIALOG_CALCULATE_COVERAGE_MESSAGE);
		Composite area = (Composite) super.createDialogArea(parent);
		Composite mainContainer = new Composite(area, SWT.NONE);
		mainContainer.setLayout(new GridLayout(1, false));
		mainContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createTestCaseComposite(mainContainer);
		createCoverageGraphComposite(mainContainer);
		
		setInitialSelection(fTestCasesViewer, fInitChecked, fInitGrayed);
		
		//Draw bar graph. Possible change for a timer with slight delay if tests prove current solution insufficient in some cases.
		Display.getDefault().asyncExec(new Runnable() {
		    public void run() {
		    	drawBarGraph();
		    }
		});

		return area;
	}

	private void setInitialSelection(CheckboxTreeViewer viewer,
			Object[] checked, Object[] grayed) {
		viewer.setCheckedElements(checked);
		viewer.setGrayedElements(grayed);
		
		Set<TestCaseNode> testCases = new HashSet<>();
		
		for(Object element : checked){
			//if the element is non grayed test suite name
			if(element instanceof String && Arrays.asList(grayed).contains(element) == false){
				testCases.addAll(fMethod.getTestCases((String)element));
			}
			else if(element instanceof TestCaseNode){
				testCases.add((TestCaseNode)element);
			}
		}
		
		fCheckStateListener.applyCheckedTestCases(testCases, true);
	}

	private void createTestCaseComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		GridData griddata = new GridData(SWT.FILL, SWT.FILL, true, true);
		griddata.minimumHeight = 250;
		composite.setLayoutData(griddata);

		Label selectTestCasesLabel = new Label(composite, SWT.WRAP);
		selectTestCasesLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		selectTestCasesLabel.setText(Messages.DIALOG_CALCULATE_COVERAGE_MESSAGE);

		createTestCaseViewer(composite);
	}

	private void createTestCaseViewer(Composite parent) {
		Tree tree = new Tree(parent, SWT.CHECK | SWT.BORDER);
		tree.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		fTestCasesViewer = new CheckboxTreeViewer(tree);
		fTestCasesViewer.setContentProvider(new TestCasesViewerContentProvider(fMethod));
		fTestCasesViewer.setLabelProvider(new TestCasesViewerLabelProvider(fStatusResolver, fMethod));
		fTestCasesViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		fTestCasesViewer.setInput(fMethod);
		
		fCheckStateListener = new CoverageTreeViewerListener(fTestCasesViewer);
		fTestCasesViewer.addCheckStateListener(fCheckStateListener);
	}

	private void createCoverageGraphComposite(Composite parent) {
		ScrolledComposite scrolled = new ScrolledComposite(parent, SWT.BORDER | SWT.FILL | SWT.V_SCROLL);
		GridData scrolledgriddata = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrolledgriddata.minimumWidth = 100;
		scrolledgriddata.minimumHeight = 150;
		scrolled.setLayout(new GridLayout(1, false));
		scrolled.setLayoutData(scrolledgriddata);
		scrolled.setExpandHorizontal(true);
		scrolled.setExpandVertical(true);

		Composite composite = new Composite(scrolled, SWT.BORDER | SWT.FILL);
		composite.setLayout(new GridLayout(1, false));
		GridData griddata = new GridData(SWT.FILL, SWT.FILL, true, true);
		griddata.minimumHeight = 100;
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
		fCanvasSet = new Canvas[getN()];
		for (int n = 0; n < getN(); n++) {
			fCanvasSet[n] = new Canvas(parent, SWT.FILL);
			fCanvasSet[n].setSize(getInitialSize().x, 40);
			GridData griddata = new GridData(SWT.FILL, SWT.FILL, true, false);
			griddata.minimumHeight = 40;
			fCanvasSet[n].setLayoutData(griddata);
		}

		parent.getParent().addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event e) {
				drawBarGraph();
			}
		});
	}

	
	private void drawBarGraph() {
		double[] coverage = fCalculator.getCoverage();
		
		if (getN() == coverage.length) {
			for (int n = 0; n < getN(); n++) {
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
				Color lightBlue = new Color(display, 96, 128, 255);

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

				gc.setForeground(lightBlue);
				gc.fillGradientRectangle(0, topbarborder, (int) (coverage[n] * widthunit), bottomborder - topbarborder, false);
				gc.setLineWidth(linewidth);
				gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
				gc.drawLine(linewidth, bottomborder, (int) width - linewidth, bottomborder);
				gc.drawLine(linewidth / 2, topbarborder, linewidth / 2, bottomborder);
				gc.drawLine((int) (width) - linewidth / 2, topbarborder, (int) (width) - linewidth / 2, bottomborder);

				DecimalFormat df = new DecimalFormat("#.00");
				String nlabel = "N= " + (n + 1);
				String percentvalue = df.format(coverage[n]) + "%";
				gc.drawString(nlabel, 10, topborder, true);
				gc.drawString(percentvalue, (width / 2) - fontspacing, (int) (topbarborder), true);
				font.dispose();
				outworldTeal.dispose();
				gc.dispose();
			}
		}
	}
	
	private int getN(){
		return fMethod.getCategories().size();
	}
}
