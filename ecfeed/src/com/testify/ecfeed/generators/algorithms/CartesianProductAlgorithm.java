package com.testify.ecfeed.generators.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;

public class CartesianProductAlgorithm<E> extends AbstractAlgorithm<E> implements IAlgorithm<E>{
	private boolean fInitialized;
	private List<? extends List<E>> fInput;
	private Collection<? extends IConstraint<E>> fConstraints;
	protected List<E> fLastGenerated;

	@Override
	public void initialize(
			List<? extends List<E>> input, 
			Collection<? extends IConstraint<E>> constraints) throws GeneratorException{
		
		if(input == null) throw new GeneratorException("Input of algorithm cannot be null");
		fInitialized = true;
		fInput = input;
		fConstraints = (constraints == null)?new HashSet<IConstraint<E>>():constraints;
		setTotalWork(calculateProductSize(input));
		reset();
	}
	
	@Override
	public List<E> getNext() throws GeneratorException{
		if(fInitialized == false){
			throw new GeneratorException("Generator not initialized");
		}
		return (fLastGenerated = getNext(fLastGenerated));
	}
	
	public void reset(){
		fLastGenerated = null;
	}

	protected List<E> getNext(List<E> last) throws GeneratorException{
		List<Integer> nextElement = representation(last);
		while((nextElement = incrementVector(nextElement)) != null){
			List<E> instance = instance(nextElement);
			if(checkConstraints(instance)){
				progress(1);
				return instance;
			}
		}
		return null;
	}
	
	protected List<E> instance(List<Integer> vector) {
		List<E> instance = new ArrayList<E>();
		for(int i = 0; i < vector.size(); i++){
			instance.add(fInput.get(i).get(vector.get(i)));
		}
		return instance;
	}
	
	protected List<Integer> representation(List<E> vector){
		if(vector == null) return null;
		List<Integer> representation = new ArrayList<Integer>();
		for(int i = 0; i < vector.size(); i++){
			E element = vector.get(i);
			representation.add(fInput.get(i).indexOf(element));
		}
		return representation;
	}

	protected boolean checkConstraints(List<E> vector) {
			if (vector == null) return true;
			for(IConstraint<E> constraint : fConstraints){
				if(constraint.evaluate(vector) == false){
					return false;
				}
			}
			return true;
	}

	protected List<Integer> incrementVector(List<Integer> vector) {
		if(vector == null){
			return firstVector();
		}
		for(int i = vector.size() - 1; i >= 0; i--){
			if(vector.get(i) < fInput.get(i).size() - 1){
				vector.set(i, vector.get(i) + 1);
				for(++i; i < vector.size(); i++){
					vector.set(i, 0);
				}
				return vector;
			}
		}
		return null;
	}

	protected List<Integer> firstVector() {
		List<Integer> firstVector = new ArrayList<Integer>(fInput.size());
		for(int i = 0; i < fInput.size(); i++){
			firstVector.add(0);
		}
		return firstVector;
	}

	protected int calculateProductSize(List<? extends List<E>> input) {
		int result = 1;
		for(List<E> vector : input){
			result *= vector.size();
		}
		return result;
	}
}
