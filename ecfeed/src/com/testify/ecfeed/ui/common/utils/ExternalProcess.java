/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.testify.ecfeed.core.utils.ExceptionHelper;
import com.testify.ecfeed.ui.common.Messages;

public class ExternalProcess {

	Process fProcess = null;

	public ExternalProcess(String messageOnStartError, String... processArgs) {
		ProcessBuilder pb = new ProcessBuilder(processArgs);

		try {
			fProcess = pb.start();
		} catch (IOException e) {
			ExceptionHelper.reportRuntimeException(messageOnStartError);
		}
	}

	public void waitForEnd(IOutputLineProcessor lineProcessor) {
		processOutput(lineProcessor);
		waitFor();
	}

	public void processOutput(IOutputLineProcessor lineProcessor) {
		InputStream is = fProcess.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);

		String line;
		try {
			while ((line = br.readLine()) != null) {
				if (lineProcessor != null && lineProcessor.processLine(line)) {
					System.out.println("\t" + line);
				}
			}
		} catch (IOException e) {
			System.out.println(Messages.CAN_NOT_LOG_OUTPUT);
		}
	}

	private void waitFor() {
		try {
			fProcess.waitFor();
		} catch (InterruptedException e) {
			ExceptionHelper.reportRuntimeException(e.getMessage());
		}
	}	
}
