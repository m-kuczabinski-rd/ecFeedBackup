package com.testify.ecfeed.ui.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;

import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.TestCaseNode;
import com.testify.ecfeed.modelif.ImplementationStatus;
import com.testify.ecfeed.ui.modelif.MethodInterface;

public class TestCasesViewerLabelProvider extends LabelProvider implements IColorProvider {
	private Map<String, Integer> fExecutableTestSuites;
	private Map<TestCaseNode, Boolean> fTestCasesStatusMap;
	private MethodInterface fMethodIf;
	
	public TestCasesViewerLabelProvider(){
		fExecutableTestSuites = new HashMap<String, Integer>();
		fTestCasesStatusMap = new HashMap<TestCaseNode, Boolean>();
		fMethodIf = new MethodInterface(null);
	}
	
	public TestCasesViewerLabelProvider(MethodNode method){
		this();
		fMethodIf.setTarget(method);
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof String) {
			String suiteName = (String)element;
			int executable = 0;
			if(fExecutableTestSuites.containsKey(suiteName)){
				executable = fExecutableTestSuites.get(suiteName);
			}
			Collection<TestCaseNode> testCases = fMethodIf.getTestCases(suiteName);
			String plural = testCases.size() != 1 ? "s" : "";
			return suiteName + " [" + testCases.size() + " test case" + plural + ", " + executable + " executable]";   
		}
		else if(element instanceof TestCaseNode){
			return fMethodIf.getName() + "(" + ((TestCaseNode)element).testDataString() + ")";
		}
		return null;
	}

	@Override
	public Color getForeground(Object element) {
		Color executableColor = ColorManager.getColor(ColorConstants.TEST_CASE_EXECUTABLE);
		if (element instanceof TestCaseNode) {
			TestCaseNode tc = (TestCaseNode)element;
			if(fTestCasesStatusMap.containsKey(tc) && fTestCasesStatusMap.get(tc) == true){
				return executableColor;
			}
			return null;
		}
		if (element instanceof String) {
			String name = (String)element;
			if(fExecutableTestSuites.containsKey(name)){
				boolean suiteExecutable = (fExecutableTestSuites.get(name) == fMethodIf.getTestCases(name).size());
				return suiteExecutable ? executableColor : null;
			}
			return null;
		}
		return null;
	}

	@Override
	public Color getBackground(Object element) {
		return null;
	}

	public void setMethod(MethodNode method){
		fMethodIf.setTarget(method);
		refresh();
	}
	
	public void refresh(){
		updateExecutableTable();
	}

	private void updateExecutableTable() {
		Map<PartitionNode, ImplementationStatus> partitionStatusMap = new HashMap<PartitionNode, ImplementationStatus>();
		fExecutableTestSuites.clear();
		fTestCasesStatusMap.clear();
		for(String testSuite : fMethodIf.getTestSuites()){
			fExecutableTestSuites.put(testSuite, 0);
		}
		if(fMethodIf.implementationStatus() != ImplementationStatus.NOT_IMPLEMENTED){
			for(TestCaseNode tc : fMethodIf.getTestCases()){
				boolean executable = true;
				String name = tc.getName();
				if(fExecutableTestSuites.containsKey(name) == false){
					fExecutableTestSuites.put(name, 0);
				}
				for(PartitionNode p : tc.getTestData()){
					ImplementationStatus status = partitionStatusMap.get(p);
					if(status == null){
						status = fMethodIf.implementationStatus(p);
						partitionStatusMap.put(p, status);
					}
					if(status != ImplementationStatus.IMPLEMENTED){
						executable = false;
						break;
					}
				}
				if(executable){
					int current = fExecutableTestSuites.get(name);
					fExecutableTestSuites.put(name, current + 1);
				}
				fTestCasesStatusMap.put(tc, executable);
			}
		}
	}
}
