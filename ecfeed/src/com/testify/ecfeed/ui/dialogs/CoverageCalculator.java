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
import com.testify.ecfeed.model.MethodParameterNode;
import com.testify.ecfeed.model.ChoiceNode;
import com.testify.ecfeed.model.TestCaseNode;

public class CoverageCalculator {

	private int N;
	private int[] fTuplesCovered;
	private int[] fTotalWork;
	private double[] fResults;
	private List<MethodParameterNode> fParameters;

	private List<List<ChoiceNode>> fInput;
	// The map of expected parameters default values. Said values are used to replace unique values in algorithm.
	private Map<Integer, ChoiceNode> fExpectedChoices;
	// The main map of covered tuples
	private List<Map<List<ChoiceNode>, Integer>> fTuples;
	// Test cases and suites (de)selected recently;
	private List<List<ChoiceNode>> fCurrentlyChangedCases;
	// If user added test cases = true; else we are substracting tuples;
	private boolean fAddingFlag;

	public CoverageCalculator(List<MethodParameterNode> parameters) {
		fParameters = parameters;
		initialize();
	}

	private void initialize() {
		fInput = prepareInput();
		N = fInput.size();
		fTuplesCovered = new int[N];
		fTotalWork = new int[N];
		fResults = new double[N];
		fCurrentlyChangedCases = new ArrayList<>();

		fTuples = new ArrayList<Map<List<ChoiceNode>, Integer>>();
		fExpectedChoices = prepareExpectedChoices();

		for (int n = 0; n < fTotalWork.length; n++) {
			fTotalWork[n] = calculateTotalTuples(fInput, n + 1, 100);
			fTuples.add(new HashMap<List<ChoiceNode>, Integer>());
		}
	}

	
	private class CalculatorRunnable implements IRunnableWithProgress {
		private boolean isCanceled;
		// if true - add occurences, else substract them

		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			int n = 0;
			List<Map<List<ChoiceNode>, Integer>> coveredTuples = new ArrayList<>();

			monitor.beginTask("Calculating Coverage", fCurrentlyChangedCases.size() * N);

			while (!monitor.isCanceled() && n < N) {
				Map<List<ChoiceNode>, Integer> mapForN = new HashMap<>();
				for (List<ChoiceNode> tcase : fCurrentlyChangedCases) {
					if (monitor.isCanceled()){
						break;
					}
					Tuples<ChoiceNode> tuples = new Tuples<ChoiceNode>(tcase, n + 1);
					for (List<ChoiceNode> pnode : tuples.getAll()) {
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
				for (Map<List<ChoiceNode>, Integer> map : coveredTuples) {
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
			for (Map<List<ChoiceNode>, Integer> tupleMap : fTuples) {
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
	
	private static void addTuplesToMap(Map<List<ChoiceNode>, Integer> map, List<ChoiceNode> tuple) {
		if (!map.containsKey(tuple)) {
			map.put(tuple, 1);
		} else {
			map.put(tuple, map.get(tuple) + 1);
		}
	}

	private static void mergeOccurrenceMaps(Map<List<ChoiceNode>, Integer> targetMap, Map<List<ChoiceNode>, Integer> sourceMap,
			boolean isAdding) {
		if (isAdding) {
			for (List<ChoiceNode> key : sourceMap.keySet()) {
				if (!targetMap.containsKey(key)) {
					targetMap.put(key, sourceMap.get(key));
				} else {
					targetMap.put(key, sourceMap.get(key) + targetMap.get(key));
				}
			}
		} else {
			for (List<ChoiceNode> key : sourceMap.keySet()) {
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

	private List<List<ChoiceNode>> prepareInput() {
		List<List<ChoiceNode>> input = new ArrayList<List<ChoiceNode>>();
		for (MethodParameterNode cnode : fParameters) {
			List<ChoiceNode> parameter = new ArrayList<ChoiceNode>();
			if(cnode.isExpected()){
				parameter.add(new ChoiceNode("expected", cnode.getDefaultValue()));
			} else {
				for (ChoiceNode pnode : cnode.getLeafChoices()) {
					parameter.add(pnode);
				}
			}
			input.add(parameter);
		}
		return input;
	}
	
	private Map<Integer, ChoiceNode> prepareExpectedChoices() {
		int n = 0;
		Map<Integer, ChoiceNode> expected = new HashMap<>();
		for (MethodParameterNode cnode : fParameters) {
			if (cnode.isExpected()) {
				ChoiceNode p = new ChoiceNode("", cnode.getDefaultValue());
				p.setParent(cnode);
				expected.put(n, p);
//				expected.put(n, cnode.getDefaultValueChoice());
			}
			n++;
		}
		return expected;
	}

	private List<List<ChoiceNode>> prepareCasesToAdd(Collection<TestCaseNode> TestCases) {
		List<List<ChoiceNode>> cases = new ArrayList<>();
		if (fExpectedChoices.isEmpty()) {
			for (TestCaseNode tcnode : TestCases) {
				List<ChoiceNode> choices = new ArrayList<>();
				for (ChoiceNode pnode : tcnode.getTestData()) {
					choices.add(pnode);
				}
				cases.add(choices);
			}
		} else {
			for (TestCaseNode tcnode : TestCases) {
				List<ChoiceNode> choices = new ArrayList<>();
				int n = 0;
				for (ChoiceNode pnode : tcnode.getTestData()) {
					if (fExpectedChoices.containsKey(n)) {
						choices.add(fExpectedChoices.get(n));
					} else {
						choices.add(pnode);
					}
					n++;
				}
				cases.add(choices);
			}
		}
		return cases;
	}

	private int calculateTotalTuples(List<List<ChoiceNode>> input, int n, int coverage) {
		int totalWork = 0;

		Tuples<List<ChoiceNode>> tuples = new Tuples<List<ChoiceNode>>(input, n);
		while (tuples.hasNext()) {
			long combinations = 1;
			List<List<ChoiceNode>> tuple = tuples.next();
			for (List<ChoiceNode> parameter : tuple) {
				combinations *= parameter.size();
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
