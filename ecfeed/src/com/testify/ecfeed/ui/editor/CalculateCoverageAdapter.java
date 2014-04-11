package com.testify.ecfeed.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.ui.common.TreeCheckStateListener;
import com.testify.ecfeed.ui.dialogs.CalculateCoverageDialog;

public class CalculateCoverageAdapter extends SelectionAdapter {

	private TestCasesViewer fViewerSection;

	CalculateCoverageAdapter(TestCasesViewer viewerSection) {
		fViewerSection = viewerSection;
	}

	private MethodNode getSelectedMethod() {
		return fViewerSection.getSelectedMethod();
	}

	private Shell getActiveShell() {
		return Display.getCurrent().getActiveShell();
	}

	public void widgetSelected(SelectionEvent e) {
		CoverageCalculator calculator = new CoverageCalculator(getSelectedMethod());
		CalculateCoverageDialog dialog = new CalculateCoverageDialog(getActiveShell(), getSelectedMethod());
		dialog.create();

		dialog.addTreeStateListener(new CoverageTreeViewerListener(calculator, dialog.getCheckboxTreeViewer()));
		calculator.addResultChangeListener(dialog);
		dialog.open();

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
					fTestCases.addAll(getSelectedMethod().getTestCases(fTestSuiteName));
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
						fTestCases.addAll(getSelectedMethod().getTestCases(fTestSuiteName));
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
			} else {
				revertLastTreeChange();
			}
		}

		/*
		 * @return the cases selected or deselected in the last operation;
		 */
		public List<TestCaseNode> getTestCases() {
			return fTestCases;
		}

		/*
		 * @return if last action was selection (false if it was deselection);
		 */
		public boolean getLastAction() {
			return fIsSelection;
		}

	}

}
