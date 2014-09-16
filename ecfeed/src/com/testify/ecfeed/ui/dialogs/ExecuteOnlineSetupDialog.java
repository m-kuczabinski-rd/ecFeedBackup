/*******************************************************************************
 * Copyright (c) 2013 Testify AS.                                                
 * All rights reserved. This program and the accompanying materials              
 * are made available under the terms of the Eclipse Public License v1.0         
 * which accompanies this distribution, and is available at                      
 * http://www.eclipse.org/legal/epl-v10.html                                     
 *                                                                               
 * Contributors:                                                                 
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.ui.dialogs;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Shell;

import com.testify.ecfeed.generators.api.IGenerator;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.ecfeed.model.constraint.Constraint;
import com.testify.ecfeed.modelif.IImplementationStatusResolver;
import com.testify.ecfeed.ui.common.Messages;

public class ExecuteOnlineSetupDialog extends GeneratorSetupDialog {
	private static final int CONTENT = CONSTRAINTS_COMPOSITE | PARTITIONS_COMPOSITE |
			GENERATOR_SELECTION_COMPOSITE;

	public ExecuteOnlineSetupDialog(Shell parentShell, MethodNode method, IImplementationStatusResolver statusResolver) {
		super(parentShell, method, statusResolver, CONTENT, 
				Messages.DIALOG_EXECUTE_ONLINE_TITLE, 
				Messages.DIALOG_EXECUTE_ONLINE_MESSAGE,
				true);
	}

	public IGenerator<PartitionNode> getSelectedGenerator() {
		return super.selectedGenerator();
	}

	public List<List<PartitionNode>> getAlgorithmInput() {
		return super.algorithmInput();
	}

	public Collection<Constraint> getConstraints() {
		return super.constraints();
	}

	public Map<String, Object> getGeneratorParameters() {
		return super.generatorParameters();
	}
}
