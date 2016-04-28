/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 ******************************************************************************/

package com.testify.ecfeed.ui.dialogs;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Shell;

import com.testify.ecfeed.core.generators.api.IGenerator;
import com.testify.ecfeed.core.model.ChoiceNode;
import com.testify.ecfeed.core.model.Constraint;
import com.testify.ecfeed.core.model.MethodNode;
import com.testify.ecfeed.ui.common.Messages;
import com.testify.ecfeed.ui.common.utils.IFileInfoProvider;

public class ExportOnlineSetupDialog extends GeneratorSetupDialog {
	private static final int CONTENT = CONSTRAINTS_COMPOSITE | CHOICES_COMPOSITE |
			GENERATOR_SELECTION_COMPOSITE;

	public static final String DIALOG_EXPORT_ONLINE_TITLE = "Export testcases online";

	public ExportOnlineSetupDialog(Shell parentShell, MethodNode method, IFileInfoProvider fileInfoProvider) {
		super(parentShell, 
				method, CONTENT, 
				DIALOG_EXPORT_ONLINE_TITLE, 
				Messages.DIALOG_EXECUTE_ONLINE_MESSAGE,
				true,
				fileInfoProvider);
	}

	public IGenerator<ChoiceNode> getSelectedGenerator() {
		return super.selectedGenerator();
	}

	public List<List<ChoiceNode>> getAlgorithmInput() {
		return super.algorithmInput();
	}

	public Collection<Constraint> getConstraints() {
		return super.constraints();
	}

	public Map<String, Object> getGeneratorParameters() {
		return super.generatorParameters();
	}
}
