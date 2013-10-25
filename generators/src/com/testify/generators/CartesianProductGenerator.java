package com.testify.generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.testify.ecfeed.api.GeneratorException;
import com.testify.ecfeed.api.IConstraint;
import com.testify.ecfeed.api.IGenerator;
import com.testify.ecfeed.api.IGeneratorParameter;
import com.testify.generators.monitors.SilentProgressMonitor;

public class CartesianProductGenerator<E> implements IGenerator<E> {
	private boolean fInitialized = false;
	private List<List<E>> fInput = null;
	private Collection<? extends IConstraint<E>> fConstraints = null;
	private List<E> fLastGenerated = null;
	private IProgressMonitor fProgressMonitor;
	private boolean fDone = true;

	@Override
	public List<IGeneratorParameter> requiredParameters() {
		return new ArrayList<IGeneratorParameter>();
	}

	@Override
	public List<IGeneratorParameter> requiredParameters(
			List<List<E>> inputDomain) {
		return requiredParameters();
	}

	@Override
	public void setParameter(String name, Object value)
			throws GeneratorException {
	}

	@Override
	public void initialize(List<List<E>> inputDomain,
			Collection<? extends IConstraint<E>> constraints,
			IProgressMonitor progressMonitor) throws GeneratorException {
		fInitialized = true;
		fInput = inputDomain;
		fConstraints = constraints;
		if (progressMonitor == null) progressMonitor = new SilentProgressMonitor();
		fProgressMonitor = progressMonitor;
		fLastGenerated = null;
		fProgressMonitor.beginTask("Generating cartesian product", productSize(fInput));
		fDone = false;
	}

	@Override
	public List<E> getNext() throws GeneratorException {
		if(fInitialized == false){
			throw new GeneratorException("Generator not initialized");
		}
		List<E> nextElement;
		if(fLastGenerated == null){
			nextElement = generateFirstElement();
		}
		else{
			nextElement = incrementVector(fLastGenerated);
		}
		
		while(!checkConstraints(nextElement)){
			nextElement = incrementVector(nextElement);
		}
		
		if(nextElement != null){
			fLastGenerated = nextElement;
		}

		return (fDone == true)?null:nextElement;
	}

	private List<E> incrementVector(List<E> vector) {
		if(fProgressMonitor.isCanceled() || fDone) return null;
		List<E> incremented = new ArrayList<E>(vector);
		for(int i = incremented.size() - 1; i >= 0; i--){
			E element = incremented.get(i);
			List<E> category = fInput.get(i);
			int elementIndex = category.indexOf(element);
			
			if(elementIndex == category.size() - 1){
				if(i == 0){
//					//all elements have been iterated
					fProgressMonitor.done();
					fDone = true;
				}
				else{
					incremented.set(i, category.get(0));
				}
			}
			else{
				E nextValue = category.get(elementIndex + 1);
				incremented.set(i, nextValue);
				break;
			}			
		}
		fProgressMonitor.worked(1);
		return incremented;
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

	private List<E> generateFirstElement() throws GeneratorException {
		List<E> firstElement = new ArrayList<E>();
		for(List<E> vector : fInput){
			if(vector.size() > 0){
				firstElement.add(vector.get(0));
			}
			else{
				throw new GeneratorException("Empty input element no. " + fInput.indexOf(vector));
			}
		}
		fProgressMonitor.worked(1);
		return firstElement;
	}

	private int productSize(List<List<E>> input) {
		int size = 1;
		for(List<E> vector : input){
			size *= vector.size();
		}
		return size;
	}

	@Override
	public void reset() throws GeneratorException {
		if(fInitialized == false){
			throw(new GeneratorException("Generator not initialized"));
		}
		initialize(fInput, fConstraints, fProgressMonitor);
	}


}
