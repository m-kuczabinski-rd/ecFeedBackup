package com.testify.ecfeed.ui.dialogs;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

import com.testify.ecfeed.generators.algorithms.Tuples;
import com.testify.ecfeed.model.AbstractCategoryNode;
import com.testify.ecfeed.model.ExpectedCategoryNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;

public class CoverageCalculator {

	private int N;
	private int[] fTuplesCovered;
	private int[] fTotalWork;
	private double[] fResults;
	private List<AbstractCategoryNode> fCategories;

	private List<List<PartitionNode>> fInput;
	// The map of expected categories default values. Said values are used to replace unique values in algorithm.
	private Map<Integer, PartitionNode> fExpectedPartitions;
	// The main map of covered tuples
	private List<Map<List<PartitionNode>, Integer>> fTuples;
	// Test cases and suites (de)selected recently;
	private List<List<PartitionNode>> fCurrentlyChangedCases;
	// If user added test cases = true; else we are substracting tuples;
	private boolean fAddingFlag;

	public CoverageCalculator(List<AbstractCategoryNode> categories) {
		fCategories = categories;
		initialize();
	}

	private void initialize() {
		fInput = prepareInput();
		N = fInput.size();
		fTuplesCovered = new int[N];
		fTotalWork = new int[N];
		fResults = new double[N];
		fCurrentlyChangedCases = new ArrayList<>();

		fTuples = new ArrayList<Map<List<PartitionNode>, Integer>>();
		fExpectedPartitions = prepareExpectedPartitions();

		for (int n = 0; n < fTotalWork.length; n++) {
			fTotalWork[n] = calculateTotalTuples(fInput, n + 1, 100);
			fTuples.add(new HashMap<List<PartitionNode>, Integer>());
		}
	}

	
	private class CalculatorRunnable implements IRunnableWithProgress {
		private boolean isCanceled;
		// if true - add occurences, else substract them

		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			int n = 0;
			List<Map<List<PartitionNode>, Integer>> coveredTuples = new ArrayList<>();

			monitor.beginTask("Calculating Coverage", fCurrentlyChangedCases.size() * N);

			while (!monitor.isCanceled() && n < N) {
				Map<List<PartitionNode>, Integer> mapForN = new HashMap<>();
				for (List<PartitionNode> tcase : fCurrentlyChangedCases) {
					if (monitor.isCanceled()){
						break;
					}
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
					mergeOccurrenceMaps(fTuples.get(n), map, fAddingFlag);
					fTuplesCovered[n] = fTuples.get(n).size();
					fResults[n] = (((double) fTuplesCovered[n]) / ((double) fTotalWork[n])) * 100;
					n++;
				}
			} else {
				isCanceled = true;
			}
			monitor.done();
		}
	}
	

	public boolean calculateCoverage() {
		// CurrentlyChangedCases are null if deselection left no test cases selected, 
		// hence we can just clear tuple map and set results to 0
		if (fCurrentlyChangedCases == null) {
			for (Map<List<PartitionNode>, Integer> tupleMap : fTuples) {
				tupleMap.clear();
			}
			// set results to zero
			resetResults();
			fCurrentlyChangedCases = new ArrayList<>();
			return true;
		} else {
			ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
			try {
				CalculatorRunnable runnable = new CalculatorRunnable();
				progressDialog.open();
				progressDialog.run(true, true, runnable);
				if (runnable.isCanceled) {
					return false;
				} else {
					fCurrentlyChangedCases.clear();
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

	private List<List<PartitionNode>> prepareInput() {
		List<List<PartitionNode>> input = new ArrayList<List<PartitionNode>>();
		for (AbstractCategoryNode cnode : fCategories) {
			List<PartitionNode> category = new ArrayList<PartitionNode>();
			for (PartitionNode pnode : cnode.getLeafPartitions()) {
				category.add(pnode);
			}
			input.add(category);
		}
		return input;
	}
	
	private Map<Integer, PartitionNode> prepareExpectedPartitions() {
		int n = 0;
		Map<Integer, PartitionNode> expected = new HashMap<>();
		for (AbstractCategoryNode cnode : fCategories) {
			if (cnode instanceof ExpectedCategoryNode) {
				expected.put(n, ((ExpectedCategoryNode) cnode).getDefaultValuePartition());
			}
			n++;
		}
		return expected;
	}

	private List<List<PartitionNode>> prepareCasesToAdd(Collection<TestCaseNode> TestCases) {
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

	// Getters & Setters
	public void resetResults() {
		for (int i = 0; i < fResults.length; i++) {
			fResults[i] = 0;
		}
	}
	
	public double[] getCoverage(){
		return fResults;
	}
	
	public void setCurrentChangedCases(Collection<TestCaseNode> testCases, boolean isAdding) {
		fAddingFlag = isAdding;
		if (testCases == null)
			fCurrentlyChangedCases = null;
		else
			fCurrentlyChangedCases = prepareCasesToAdd(testCases);
	}
}
