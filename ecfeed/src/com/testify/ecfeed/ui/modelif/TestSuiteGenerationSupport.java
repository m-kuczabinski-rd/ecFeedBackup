package com.testify.ecfeed.ui.modelif;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;
import com.testify.ecfeed.generators.api.IGenerator;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.constraint.AbstractConstraint;
import com.testify.ecfeed.ui.dialogs.GenerateTestSuiteDialog;
import com.testify.ecfeed.ui.dialogs.GeneratorProgressMonitorDialog;

public class TestSuiteGenerationSupport {

	private boolean fCanceled;
	private Collection<AbstractConstraint> fSelectedConstraints;
	private MethodNode fTarget;
	private String fTestSuiteName;
	private List<List<PartitionNode>> fGeneratedData;
	private boolean fHasData;
	
	private class ExpectedValueReplacer extends AbstractConstraint{
		
		@Override
		public boolean evaluate(List<PartitionNode> values) {
			return true;
		}

		@Override
		public boolean adapt(List<PartitionNode> values) {
			for(PartitionNode p : values){
				if(p.getCategory().isExpected()){
					values.set(p.getCategory().getIndex(), p.getCopy());
				}
			}
			return true;
		}
	}

	private class GeneratorRunnable implements IRunnableWithProgress{

		private IGenerator<PartitionNode> fGenerator;
		private List<List<PartitionNode>> fGeneratedData;
		private List<List<PartitionNode>> fInput;
		private Collection<IConstraint<PartitionNode>> fConstraints;
		private Map<String, Object> fParameters;

		GeneratorRunnable(IGenerator<PartitionNode> generator, 
				List<List<PartitionNode>> input, 
				Collection<IConstraint<PartitionNode>> constraints, 
				Map<String, Object> parameters, 
				List<List<PartitionNode>> generated){
			fGenerator = generator;
			fInput = input;
			fConstraints = constraints;
			fParameters = parameters;
			fGeneratedData = generated;
		}
		
		@Override
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			List<PartitionNode> next;
			try {
				fGenerator.initialize(fInput, fConstraints, fParameters);
				monitor.beginTask("Generating test data", fGenerator.totalWork());
				while((monitor.isCanceled() == false) && (next = fGenerator.next()) != null){
					fGeneratedData.add(next);
					monitor.worked(fGenerator.workProgress());
				}
				monitor.done();
			} catch (GeneratorException e) {
				throw new InvocationTargetException(e, e.getMessage());
			}
		}
		
	}
	
	public TestSuiteGenerationSupport(MethodNode target) {
		fTarget = target;
		fHasData = false;
	}
	
	public void proceed(){
		fHasData = generate() && !fCanceled;
	}
	
	protected boolean generate(){
		GenerateTestSuiteDialog dialog = new GenerateTestSuiteDialog(getActiveShell(), fTarget, new GenericNodeInterface());
		if(dialog.open() == IDialogConstants.OK_ID){
			IGenerator<PartitionNode> selectedGenerator = dialog.getSelectedGenerator();
			List<List<PartitionNode>> algorithmInput = dialog.getAlgorithmInput();
			fSelectedConstraints = new ArrayList<AbstractConstraint>();
			fSelectedConstraints.addAll(dialog.getConstraints());
			fSelectedConstraints.add(new ExpectedValueReplacer());
			List<IConstraint<PartitionNode>> constraints = new ArrayList<IConstraint<PartitionNode>>();
			constraints.addAll(fSelectedConstraints);
			fTestSuiteName = dialog.getTestSuiteName();
			Map<String, Object> parameters = dialog.getGeneratorParameters();
			fGeneratedData = generateTestData(selectedGenerator, algorithmInput, constraints, parameters);
			return true;
		}
		return false;
	}

	public List<List<PartitionNode>> getGeneratedData(){
		return fGeneratedData;
	}
	
	public String getTestSuiteName(){
		return fTestSuiteName;
	}
	
	private List<List<PartitionNode>> generateTestData(final IGenerator<PartitionNode> generator, 
			final List<List<PartitionNode>> input, 
			final Collection<IConstraint<PartitionNode>> constraints,
			final Map<String, Object> parameters) {

		GeneratorProgressMonitorDialog progressDialog = new GeneratorProgressMonitorDialog(getActiveShell(), generator);
		List<List<PartitionNode>> generated = new ArrayList<List<PartitionNode>>();
		fCanceled = false;
		try {
			GeneratorRunnable runnable = new GeneratorRunnable(generator, input, constraints, parameters, generated);
			progressDialog.open();
			progressDialog.run(true, true, runnable);
		} catch (InvocationTargetException e) {
			MessageDialog.openError(getActiveShell(), "Exception", e.getMessage());
			fCanceled = true;
		}catch (InterruptedException e) {
			fCanceled = true;
			MessageDialog.openError(getActiveShell(), "Exception", e.getMessage());
			e.printStackTrace();
		}
		fCanceled |= progressDialog.getProgressMonitor().isCanceled();
		if(!fCanceled){
			return generated;
		}
		else{
			//return empty set if the operation was canceled
			//TODO add a decision dialog where user may choose whether to add generated data
			return new ArrayList<List<PartitionNode>>();
		}
	}

	private Shell getActiveShell() {
		return Display.getCurrent().getActiveShell();
	}

	public boolean wasCancelled() {
		return fCanceled;
	}
	
	public boolean hasData(){
		return fHasData;
	}
}
