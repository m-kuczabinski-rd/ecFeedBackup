/*******************************************************************************
 * Copyright (c) 2013 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)gmail.com) - initial implementation
 ******************************************************************************/

package com.testify.generators.algorithms;

import java.io.PrintStream;

import org.eclipse.core.runtime.IProgressMonitor;

public class ConsoleProgressMonitor implements IProgressMonitor {
	private boolean fCanceled = false;
	private int fTotalWork;
	private PrintStream fOutput;

	public ConsoleProgressMonitor() {
		fOutput = System.out;
	}
	
	public ConsoleProgressMonitor(PrintStream output){
		fOutput = output;
	}
	
	@Override
	public void beginTask(String name, int totalWork) {
		fOutput.println(name + ", " + totalWork + " steps to complete");
		fTotalWork = totalWork;
	}

	@Override
	public void done() {
		fOutput.println("Work done");
	}

	@Override
	public void internalWorked(double work) {
		fOutput.println("Internal progress " + work);
	}

	@Override
	public boolean isCanceled() {
		return fCanceled;
	}

	@Override
	public void setCanceled(boolean value) {
		fOutput.println("Processing canceled set to " + value);
		fCanceled = value;
	}

	@Override
	public void setTaskName(String name) {
		fOutput.println("Task name set to \"" + name + "\"");
	}

	@Override
	public void subTask(String name) {
		fOutput.println(name);
	}

	@Override
	public void worked(int work) {
		fOutput.println(work + "/" + fTotalWork + " steps completed");
	}
}
