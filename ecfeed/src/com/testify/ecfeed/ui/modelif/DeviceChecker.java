/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.modelif;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.utils.ExceptionHelper;

public class DeviceChecker {

	public static void checkOneDeviceAttached() {
		Process process = startProcess();
		int devices = countDevices(process);
		waitFor(process);

		if (devices == 0) {
			ExceptionHelper.reportRuntimeException(Messages.NO_ANDROID_DEVICES_ATTACHED);
		}
		if (devices > 1) {
			ExceptionHelper.reportRuntimeException(Messages.TOO_MANY_ANDROID_DEVICES_FOUND);
		}
	}

	private static Process startProcess() {
		ProcessBuilder pb = new ProcessBuilder("adb", "devices");

		Process process = null;
		try {
			process = pb.start();
		} catch (IOException e) {
			ExceptionHelper.reportRuntimeException(Messages.EXCEPTION_CAN_NOT_CREATE_INSTALL_PROCESS);
		}
		return process;
	}

	private static int countDevices(Process process) {
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);

		int devices = 0;
		String line;
		try {
			while ((line = br.readLine()) != null) {
				if (!isIgnoredLine(line)) {
					devices++;
				}
			}
		} catch (IOException e) {
			System.out.println(Messages.CAN_NOT_LOG_OUTPUT);
		}
		return devices;
	}

	private static void waitFor(Process process) {
		try {
			process.waitFor();
		} catch (InterruptedException e) {
			ExceptionHelper.reportRuntimeException(e.getMessage());
		}
	}

	private static boolean isIgnoredLine(String line) {
		String trimmedLine = line.trim();

		if (trimmedLine.isEmpty()) {
			return true;
		}
		if (isListOfDevicesLine(trimmedLine)) {
			return true;
		}
		return false;
	}

	private static boolean isListOfDevicesLine(String line) {
		if (line.contains("List of devices attached")) {
			return true;
		}
		return false;
	}
}
