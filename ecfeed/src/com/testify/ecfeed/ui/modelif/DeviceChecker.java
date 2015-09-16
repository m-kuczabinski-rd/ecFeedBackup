/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.testify.ecfeed.ui.modelif;

import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.utils.ExternalProcess;
import com.testify.ecfeed.ui.common.utils.OutputLineProcessor;
import com.testify.ecfeed.utils.ExceptionHelper;

public class DeviceChecker {

	private static class CountDevicesLineProcessor implements OutputLineProcessor {

		private int fDevices = 0;

		@Override
		public boolean printLine(String line) { 
			if (!isIgnoredLine(line)) {
				fDevices++;
			}
			return false;
		} 

		public int getDeviceCount() {
			return fDevices;
		}

		private boolean isIgnoredLine(String line) { 
			String trimmedLine = line.trim();

			if (trimmedLine.isEmpty()) {
				return true;
			}
			if (isListOfDevicesLine(trimmedLine)) {
				return true;
			}
			return false;
		}

		private boolean isListOfDevicesLine(String line) {
			if (line.contains("List of devices attached")) {
				return true;
			}
			return false;
		}	
	}

	public static void checkIfOneDeviceAttached() {
		ExternalProcess externalProcess = 
				new ExternalProcess(
						Messages.EXCEPTION_CAN_NOT_CREATE_INSTALL_PROCESS,
						"adb", "devices" );

		CountDevicesLineProcessor lineProcessor = new CountDevicesLineProcessor();		
		externalProcess.waitForEnd(lineProcessor);

		int devices = lineProcessor.getDeviceCount();
		if (devices == 0) {
			ExceptionHelper.reportRuntimeException(Messages.NO_ANDROID_DEVICES_ATTACHED);
		}
		if (devices > 1) {
			ExceptionHelper.reportRuntimeException(Messages.TOO_MANY_ANDROID_DEVICES_FOUND);
		}
	}
}
