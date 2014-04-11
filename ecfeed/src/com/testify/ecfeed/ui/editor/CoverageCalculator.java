package com.testify.ecfeed.ui.editor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.generators.algorithms.Tuples;
import com.testify.ecfeed.model.AbstractCategoryNode;
import com.testify.ecfeed.model.ExpectedCategoryNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;

public class CoverageCalculator {

	private final MethodNode fMethod;
	private int N;
	private int[] fTuplesCovered;
	private int[] fTotalWork;
	private Map<Integer, PartitionNode> fExpectedPartitions;
	private double[] fResults;
	Set<ChangeListener> fListeners;

	private List<List<PartitionNode>> fInput;
	// The main map of covered tuples
	private List<Map<List<PartitionNode>, Integer>> fTuples;
	// Test cases and suites (de)selected recently;
	private List<List<PartitionNode>> fCurrentChangedCases;
	// If user added test cases = true; else we are substracting tuples;
	private boolean fAddingFlag;

	public CoverageCalculator(MethodNode method) {
		this.fMethod = method;

		initialize();
	}

	private void initialize() {
		fInput = prepareInput();
		N = fInput.size();
		fTuplesCovered = new int[N];
		fTotalWork = new int[N];
		fResults = new double[N];
		fCurrentChangedCases = new ArrayList<>();

		fListeners = new HashSet<>();
		fTuples = new ArrayList<Map<List<PartitionNode>, Integer>>();
		fExpectedPartitions = prepareExpectedPartitions();

		for (int n = 0; n < fTotalWork.length; n++) {
			fTotalWork[n] = calculateTotalTuples(fInput, n + 1, 100);
			fTuples.add(new HashMap<List<PartitionNode>, Integer>());
		}
	}

	private class CalculatorRunnable implements IRunnableWithProgress {
		private List<List<PartitionNode>> fTestCases;
		private CoverageCalculator fCalculator;
		private int N;
		private boolean isCanceled;
		// if true - add occurences, else substract them
		private boolean fIsAdding;

		CalculatorRunnable(CoverageCalculator calculator) {
			fCalculator = calculator;
			fIsAdding = fCalculator.isAddingNow();
			fTestCases = fCalculator.getCurrentChangedCases();
			N = calculator.getN();
			fIsAdding = fCalculator.isAddingNow();
		}

		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			int n = 0;
			List<Map<List<PartitionNode>, Integer>> coveredTuples = new ArrayList<>();

			monitor.beginTask("Calculating Coverage", fTestCases.size() * N);

			while (!monitor.isCanceled() && n < N) {
				Map<List<PartitionNode>, Integer> mapForN = new HashMap<>();
				for (List<PartitionNode> tcase : fTestCases) {
					if (monitor.isCanceled())
						break;
					Tuples<PartitionNode> tuples = new Tuples<PartitionNode>(tcase, n + 1);
					for (List<PartitionNode> pnode : tuples.getAll()) {
						addTuplesToMap(mapForN, pnode);
					}
					monitor.worked(1);
				}
				if (!monitor.isCanceled()) {
					coveredTuples.add(mapForN);
					n++;
				}
			}

			n = 0;
			if (!monitor.isCanceled()) {
				for (Map<List<PartitionNode>, Integer> map : coveredTuples) {
					mergeOccurrenceMaps(fCalculator.getCoveredTuplesMap().get(n), map, fIsAdding);
					fCalculator.getTuplesCovered()[n] = fCalculator.getCoveredTuplesMap().get(n).size();
					fCalculator.getResults()[n] = (((double) getTuplesCovered()[n]) / ((double) fCalculator.getTotalWork()[n])) * 100;
					n++;
				}
			} else {
				isCanceled = true;
			}
			monitor.done();

		}
	}

	public boolean calculateCoverage() {
		if (fCurrentChangedCases == null) {
			for (Map<List<PartitionNode>, Integer> tupleMap : fTuples) {
				tupleMap.clear();
			}
			// set results to zero
			resetResults();
			fCurrentChangedCases = new ArrayList<>();
			notifyChangeListeners();
			return true;
		} else {
			ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
			try {
				CalculatorRunnable runnable = new CalculatorRunnable(this);
				progressDialog.open();
				progressDialog.run(true, true, runnable);
				if (runnable.isCanceled) {
					return false;
				} else {
					fCurrentChangedCases.clear();
					notifyChangeListeners();
					return true;
				}

			} catch (InvocationTargetException e) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Exception", "Invocation: " + e.getCause());
				return false;
			} catch (InterruptedException e) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Exception", "Interrupted: " + e.getMessage());
				e.printStackTrace();
				return false;
			}
		}

	}

	private List<List<PartitionNode>> prepareInput() {
		List<List<PartitionNode>> input = new ArrayList<List<PartitionNode>>();
		for (AbstractCategoryNode cnode : fMethod.getCategories()) {
			List<PartitionNode> category = new ArrayList<PartitionNode>();
			for (PartitionNode pnode : cnode.getLeafPartitions()) {
				category.add(pnode);
			}
			input.add(category);
		}
		return input;
	}

	private List<List<PartitionNode>> prepareCasesToAdd(List<TestCaseNode> TestCases) {
		List<List<PartitionNode>> cases = new ArrayList<>();
		if (fExpectedPartitions.isEmpty()) {
			for (TestCaseNode tcnode : TestCases) {
				List<PartitionNode> partitions = new ArrayList<>();
				for (PartitionNode pnode : tcnode.getTestData()) {
					partitions.add(pnode);
				}
				cases.add(partitions);
			}
		} else {
			for (TestCaseNode tcnode : TestCases) {
				List<PartitionNode> partitions = new ArrayList<>();
				int n = 0;
				for (PartitionNode pnode : tcnode.getTestData()) {
					if (fExpectedPartitions.containsKey(n)) {
						partitions.add(fExpectedPartitions.get(n));
					} else {
						partitions.add(pnode);
					}
					n++;
				}
				cases.add(partitions);
			}
		}
		return cases;
	}

	private Map<Integer, PartitionNode> prepareExpectedPartitions() {
		int n = 0;
		Map<Integer, PartitionNode> expected = new HashMap<>();
		for (AbstractCategoryNode cnode : fMethod.getCategories()) {
			if (cnode instanceof ExpectedCategoryNode) {
				expected.put(n, ((ExpectedCategoryNode) cnode).getDefaultValuePartition());
			}
			n++;
		}
		return expected;
	}

	private static void addTuplesToMap(Map<List<PartitionNode>, Integer> map, List<PartitionNode> tuple) {
		if (!map.containsKey(tuple)) {
			map.put(tuple, 1);
		} else {
			map.put(tuple, map.get(tuple) + 1);
		}
	}

	private static void mergeOccurrenceMaps(Map<List<PartitionNode>, Integer> targetMap, Map<List<PartitionNode>, Integer> sourceMap,
			boolean isAdding) {
		if (isAdding) {
			for (List<PartitionNode> key : sourceMap.keySet()) {
				if (!targetMap.containsKey(key)) {
					targetMap.put(key, sourceMap.get(key));
				} else {
					targetMap.put(key, sourceMap.get(key) + targetMap.get(key));
				}
			}
		} else {
			for (List<PartitionNode> key : sourceMap.keySet()) {
				if (!targetMap.containsKey(key)) {
					System.err.println("Negative occurences...");
				} else {
					int i = targetMap.get(key) - sourceMap.get(key);
					if (i <= 0)
						targetMap.remove(key);
					else
						targetMap.put(key, i);
				}
			}
		}
	}

	private int calculateTotalTuples(List<List<PartitionNode>> input, int n, int coverage) {
		int totalWork = 0;

		Tuples<List<PartitionNode>> tuples = new Tuples<List<PartitionNode>>(input, n);
		while (tuples.hasNext()) {
			long combinations = 1;
			List<List<PartitionNode>> tuple = tuples.next();
			for (List<PartitionNode> category : tuple) {
				combinations *= category.size();
			}
			totalWork += combinations;
		}

		return (int) Math.ceil(((double) (coverage * totalWork)) / 100);
	}

	// notify GUI to update.
	public void notifyChangeListeners() {
		for (ChangeListener listener : fListeners) {
			listener.stateChanged(new ChangeEvent(fResults));
		}
	}

	public void addResultChangeListener(ChangeListener listener) {
		fListeners.add(listener);
	}

	// Getters & Setters

	public int getN() {
		return N;
	}

	public double[] getResults() {
		return fResults;
	}

	public void resetResults() {
		for (int i = 0; i < fResults.length; i++) {
			fResults[i] = 0;
		}
	}

	public int[] getTuplesCovered() {
		return fTuplesCovered;
	}

	public int[] getTotalWork() {
		return fTotalWork;
	}

	public List<List<PartitionNode>> getInput() {
		return fInput;
	}

	public MethodNode getMethod() {
		return fMethod;
	}

	public Map<Integer, PartitionNode> getExpectedPartitions() {
		return fExpectedPartitions;
	}

	public List<Map<List<PartitionNode>, Integer>> getTuples() {
		return fTuples;
	}

	public boolean isAddingNow() {
		return fAddingFlag;
	}

	public void setAddingNow(boolean isAdding) {
		fAddingFlag = isAdding;
	}

	public List<List<PartitionNode>> getCurrentChangedCases() {
		return fCurrentChangedCases;
	}

	public void setCurrentChangedCases(List<TestCaseNode> testCases, boolean isAdding) {
		fAddingFlag = isAdding;
		if (testCases == null)
			fCurrentChangedCases = null;
		else
			fCurrentChangedCases = prepareCasesToAdd(testCases);
	}

	public List<Map<List<PartitionNode>, Integer>> getCoveredTuplesMap() {
		return fTuples;
	}
}
