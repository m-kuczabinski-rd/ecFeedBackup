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

package com.testify.ecfeed.ui.dialogs;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Shell;

import com.testify.ecfeed.generators.api.IConstraint;
import com.testify.ecfeed.generators.api.IGenerator;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.ui.common.Messages;

public class GenerateTestSuiteDialog extends GeneratorSetupDialog {
	private static final int CONTENT = CONSTRAINTS_COMPOSITE | PARTITIONS_COMPOSITE |
			TEST_SUITE_NAME_COMPOSITE | GENERATOR_SELECTION_COMPOSITE;

	public GenerateTestSuiteDialog(Shell parentShell, MethodNode method) {
		super(parentShell, method, CONTENT, 
				Messages.DIALOG_GENERATE_TEST_SUITE_TITLE, 
				Messages.DIALOG_GENERATE_TEST_SUITE_MESSAGE);
	}

	public IGenerator<PartitionNode> getSelectedGenerator() {
		return super.selectedGenerator();
	}

	public List<List<PartitionNode>> getAlgorithmInput() {
		return super.algorithmInput();
	}

	public Collection<IConstraint<PartitionNode>> getConstraints() {
		return super.constraints();
	}

	public String getTestSuiteName() {
		return super.testSuiteName();
	}

	public Map<String, Object> getGeneratorParameters() {
		return super.generatorParameters();
	}
}