package com.testify.generators.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.testify.ecfeed.api.GeneratorException;
import com.testify.ecfeed.api.IConstraint;
import com.testify.generators.monitors.SilentProgressMonitor;

public class CartesianProductAlgorithm<E> implements IAlgorithm<E>{
	private boolean fInitialized;
	private List<? extends List<E>> fInput;
	private Collection<? extends IConstraint<E>> fConstraints;
	private IProgressMonitor fProgressMonitor;
	private List<Integer> fLastGenerated;

	@Override
	public void initialize(
			List<? extends List<E>> input, 
			Collection<? extends IConstraint<E>> constraints,
			IProgressMonitor progressMonitor) throws GeneratorException{
		
		if(input == null) throw new GeneratorException("Input of algorithm cannot be null");
		fInitialized = true;
		fInput = input;
		fConstraints = (constraints == null)?new HashSet<IConstraint<E>>():constraints;
		fProgressMonitor = (progressMonitor == null)?new SilentProgressMonitor():progressMonitor;
		reset();
	}
	
	@Override
	public List<E> getNext() throws GeneratorException{
		if(fInitialized == false){
			throw new GeneratorException("Generator not initialized");
		}
		List<Integer> nextElement;
		while((nextElement = incrementVector(fLastGenerated)) != null){
			fProgressMonitor.worked(1);
			List<E> instance = instance(nextElement);
			if(checkConstraints(instance)){
				fLastGenerated = nextElement;
				return instance; 
			}
		}
		fProgressMonitor.done();
		return null;
	}
	
	private List<E> instance(List<Integer> vector) {
		List<E> instance = new ArrayList<E>();
		for(int i = 0; i < vector.size(); i++){
			instance.add(fInput.get(i).get(vector.get(i)));
		}
		return instance;
	}

	private boolean checkConstraints(List<E> vector) {
			if (vector == null) return true;
			for(IConstraint<E> constraint : fConstraints){
				if(constraint.evaluate(vector) == false){
					return false;
				}
			}
			return true;
	}

	private List<Integer> incrementVector(List<Integer> vector) {
		if(vector == null){
			fLastGenerated = firstVector();
			return fLastGenerated;
		}
		for(int i = vector.size() - 1; i >= 0; i--){
			if(vector.get(i) < fInput.get(i).size() - 1){
				vector.set(i, vector.get(i) + 1);
				for(++i; i < vector.size(); i++){
					vector.set(i, 0);
				}
				fLastGenerated = vector;
				return fLastGenerated;
			}
		}
		fLastGenerated = null;
		return fLastGenerated;
	}

	private List<Integer> firstVector() {
		List<Integer> firstVector = new ArrayList<Integer>(fInput.size());
		for(int i = 0; i < fInput.size(); i++){
			firstVector.add(0);
		}
		return firstVector;
	}

	public void reset(){
		fLastGenerated = null;
		fProgressMonitor.beginTask("Generating cartesian product", totalWork());
	}
	
	protected int totalWork(){
		int totalWork = 1;
		for(List<E> vector : fInput){
			totalWork *= vector.size();
		}
		return totalWork;
	}
}
