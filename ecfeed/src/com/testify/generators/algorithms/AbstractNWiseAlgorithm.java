package com.testify.generators.algorithms;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import com.testify.ecfeed.api.GeneratorException;
import com.testify.ecfeed.api.IConstraint;
import com.testify.generators.CartesianProductGenerator;
import com.testify.generators.monitors.SilentProgressMonitor;

public class AbstractNWiseAlgorithm<E> implements INWiseAlgorithm<E> {

	private CartesianProductGenerator<E> fCartesianGenerator;
	private List<? extends List<E>> fInput;
	private Collection<? extends IConstraint<E>> fConstraints;
	protected int N  = -1;
	private IProgressMonitor fProgressMonitor;
	private long fTuplesToGenerate;
	
	public void initialize(int n,
			List<? extends List<E>> input, 
			Collection<? extends IConstraint<E>> constraints,
			IProgressMonitor progressMonitor) throws GeneratorException {

		if(n < 1 || n > input.size()){
			throw new GeneratorException("Value of N for this input must be between 1 and " + input.size());
		}
		N = n;
		initialize(input, constraints, progressMonitor);
	}

	public void initialize(List<? extends List<E>> input, 
			Collection<? extends IConstraint<E>> constraints,
			IProgressMonitor progressMonitor) throws GeneratorException {

		if(N == -1) throw new GeneratorException("Parameter N not initialized");
		fCartesianGenerator = new CartesianProductGenerator<E>();
		fCartesianGenerator.initialize(input, constraints, null, null);
		fInput = input;
		fConstraints = constraints;
		fProgressMonitor = (progressMonitor != null)?progressMonitor:new SilentProgressMonitor();
		reset();
	}
	
	@Override
	public List<E> getNext() throws GeneratorException {
		return null;
	}

	@Override
	public void reset(){
		fCartesianGenerator.reset();
		fTuplesToGenerate = totalWork();
		progressMonitor().beginTask("Generating " + N + "-tuples", (int)fTuplesToGenerate);
	}
	
	protected long totalWork(){
		long totalWork = 0;
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

	protected IProgressMonitor progressMonitor(){
		return fProgressMonitor;
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
			generator.initialize(next, null, null, null);
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
