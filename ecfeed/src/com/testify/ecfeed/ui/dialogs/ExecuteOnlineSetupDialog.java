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

public class ExecuteOnlineSetupDialog extends GeneratorSetupDialog {
	private static final int CONTENT = CONSTRAINTS_COMPOSITE | PARTITIONS_COMPOSITE |
			GENERATOR_SELECTION_COMPOSITE;

	public ExecuteOnlineSetupDialog(Shell parentShell, MethodNode method) {
		super(parentShell, method, CONTENT, 
				Messages.DIALOG_EXECUTE_ONLINE_TITLE, 
				Messages.DIALOG_EXECUTE_ONLINE_MESSAGE);
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

	public Map<String, Object> getGeneratorParameters() {
		return super.generatorParameters();
	}
}
