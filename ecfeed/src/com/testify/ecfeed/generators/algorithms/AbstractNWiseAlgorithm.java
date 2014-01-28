package com.testify.ecfeed.generators.algorithms;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.testify.ecfeed.generators.CartesianProductGenerator;
import com.testify.ecfeed.generators.api.GeneratorException;
import com.testify.ecfeed.generators.api.IConstraint;

public class AbstractNWiseAlgorithm<E> extends AbstractAlgorithm<E> implements IAlgorithm<E> {

	private CartesianProductGenerator<E> fCartesianGenerator;
	private List<? extends List<E>> fInput;
	private Collection<? extends IConstraint<E>> fConstraints;
	protected int N  = -1;
	private int fTuplesToGenerate;
	protected int fProgress;
	
	public AbstractNWiseAlgorithm(int n){
		N = n;
	}
	
	public void initialize(List<? extends List<E>> input, 
			Collection<? extends IConstraint<E>> constraints) throws GeneratorException {

		if(N < 1 || N > input.size()){
			throw new GeneratorException("Value of N for this input must be between 1 and " + input.size());
		}
		fCartesianGenerator = new CartesianProductGenerator<E>();
		fCartesianGenerator.initialize(input, constraints, null);
		fInput = input;
		fConstraints = constraints;
		reset();
	}
	
	@Override
	public List<E> getNext() throws GeneratorException {
		return null;
	}

	@Override
	public void reset(){
		fCartesianGenerator.reset();
		fTuplesToGenerate = calculateTotalTuples();
		setTotalWork(fTuplesToGenerate);
		super.reset();
	}
	
	public int getN(){
		return N;
	}
	
	private int calculateTotalTuples(){
		int totalWork = 0;
		Tuples<List<E>> tuples = new Tuples<>(fInput, N);
		while(tuples.hasNext()){
			long combinations = 1;
			List<List<E>> tuple = tuples.next();
			for(List<E> category : tuple){
				combinations *= category.size();
			}
			totalWork += combinations;
		}
		return totalWork;
	}

	protected List<E> cartesianNext() throws GeneratorException{
		return fCartesianGenerator.next();
	}

	protected List<? extends List<E>>getInput(){
		return fInput;
	}

	protected Collection<? extends IConstraint<E>>getConstraints(){
		return fConstraints;
	}
	
	protected int maxTuples(List<? extends List<E>> input, int n){
		return (new Tuples<List<E>>(input, n)).getAll().size();
	}
	
	protected Set<List<E>> getTuples(List<E> vector){
		return (new Tuples<E>(vector, N)).getAll();
	}

	protected Set<List<E>> getAllTuples(List<? extends List<E>> inputDomain, int n) throws GeneratorException {
		Set<List<E>> result  = new HashSet<List<E>>();
		Tuples<List<E>> categoryTuples = new Tuples<List<E>>(inputDomain, n);
		while(categoryTuples.hasNext()){
			List<List<E>> next = categoryTuples.next();
			CartesianProductGenerator<E> generator = new CartesianProductGenerator<E>();
			generator.initialize(next, null, null);
			List<E> tuple;
			while((tuple = generator.next()) != null){
				result.add(tuple);
			}
		}
		return result;
	}
	
	protected long tuplesToGenerate(){
		return fTuplesToGenerate;
	}
	
	protected void cartesianReset(){
		fCartesianGenerator.reset();
	}
}
