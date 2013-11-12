package com.testify.generators.monitors;

import org.eclipse.core.runtime.IProgressMonitor;

public class SilentProgressMonitor implements IProgressMonitor {
	private boolean fCanceled = false;

	@Override
	public void beginTask(String name, int totalWork) {
	}

	@Override
	public void done() {
	}

	@Override
	public void internalWorked(double work) {
	}

	@Override
	public boolean isCanceled() {
		return fCanceled;
	}

	@Override
	public void setCanceled(boolean value) {
		fCanceled = value;
	}

	@Override
	public void setTaskName(String name) {
	}

	@Override
	public void subTask(String name) {
	}

	@Override
	public void worked(int work) {
	}

}
