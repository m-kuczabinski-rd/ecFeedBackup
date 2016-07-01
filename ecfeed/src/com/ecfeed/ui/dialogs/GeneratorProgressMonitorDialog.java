/*******************************************************************************
 * Copyright (c) 2014 Testify AS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Michal Gluszko (m.gluszko(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.ecfeed.ui.dialogs;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;

import com.ecfeed.core.generators.api.IGenerator;

public class GeneratorProgressMonitorDialog extends ProgressMonitorDialog {
	
	private IGenerator<?> fGenerator;

	public GeneratorProgressMonitorDialog(Shell parent, IGenerator<?> generator) {
		super(parent);
		this.fGenerator = generator;
	}

	public void setGenerator(IGenerator<?> fGenerator) {
		this.fGenerator = fGenerator;
	}
	
	@Override
	protected void cancelPressed() {
		fGenerator.cancel();
		super.cancelPressed();
	}

}
