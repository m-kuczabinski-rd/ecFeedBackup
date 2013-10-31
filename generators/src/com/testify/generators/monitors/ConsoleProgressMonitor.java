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

package com.testify.generators.monitors;

import java.io.PrintStream;

public class ConsoleProgressMonitor extends SilentProgressMonitor {
	private int fTotalWork;
	private int fWorkCompleted;
	private PrintStream fOutput;

	public ConsoleProgressMonitor() {
		fOutput = System.out;
	}
	
	public ConsoleProgressMonitor(PrintStream output){
		fOutput = output;
	}
	
	@Override
	public void beginTask(String name, int totalWork) {
		fOutput.print(name + ":\n" );
		fTotalWork = totalWork;
		fWorkCompleted = 0;
	}

	@Override
	public void done() {
		fOutput.println("\nDone");
	}

	@Override
	public void internalWorked(double work) {
		fOutput.println("Internal worked " + work);
	}

	@Override
	public void setCanceled(boolean value) {
		super.setCanceled(value);
		fOutput.println("Processing canceled set to " + value);
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
		final int DOTS_TO_PRINT = 50;
		int completedBefore = fWorkCompleted;
		fWorkCompleted += work;
		int dotsToPrint = DOTS_TO_PRINT*fWorkCompleted/fTotalWork - DOTS_TO_PRINT*completedBefore/fTotalWork;
		for(int i = 0; i < dotsToPrint; i++){
			System.out.print(".");
		}
	}
}
