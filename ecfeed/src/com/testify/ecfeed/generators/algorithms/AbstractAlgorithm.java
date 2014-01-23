package com.testify.ecfeed.generators.algorithms;

public abstract class AbstractAlgorithm<E> implements IAlgorithm<E> {

	private int fTotalWork;
	private int fProgress;
	private int fTotalProgress;

	@Override
	public int totalWork() {
		return fTotalWork;
	}

	@Override
	public int workProgress() {
		int progress = fProgress;
		fProgress = 0;
		return progress;
	}
	
	@Override
	public int totalProgress(){
		return fTotalProgress;
	}

	
	protected void progress(int progress){
		fProgress += progress;
		fTotalProgress += progress;
	}
	
	protected void setTotalWork(int totalWork){
		fTotalWork = totalWork;
	}
	
	public void reset(){
		fProgress = 0;
	}
}
