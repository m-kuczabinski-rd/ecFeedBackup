package com.testify.runner;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.runners.model.FrameworkMethod;

import com.testify.ecfeed.api.GeneratorException;
import com.testify.ecfeed.api.IConstraint;
import com.testify.ecfeed.api.IGenerator;
import com.testify.ecfeed.model.PartitionNode;

public class RuntimeFrameworkMethod extends FrameworkMethod {

	protected IGenerator<PartitionNode> fGenerator;
	private List<Object> fVal = null;
	private List<PartitionNode> fnext;

	@SuppressWarnings("unchecked")
	public RuntimeFrameworkMethod(Method method,
			IGenerator<PartitionNode> fGeneratorUnderTest,
			Collection<? extends IConstraint<PartitionNode>> constraints,
			List<List<String>> input, IProgressMonitor progressMonitor,
			Map<String, Object> parameters) throws Exception {
		super(method);
		this.fGenerator = (IGenerator<PartitionNode>) fGeneratorUnderTest;
		this.fGenerator.initialize((List<? extends List<PartitionNode>>) input,
				constraints, parameters, progressMonitor);
	}

	@Override
	public Object invokeExplosively(Object target, Object... parameters)
			throws Throwable {
		try {
			System.out.println("target" + target);
			System.out.println("parameters" + parameters);
			while ((fnext = (List<PartitionNode>) fGenerator.next()) != null) {
				for (PartitionNode partitionNode : fnext) {
					fVal.add(partitionNode.getValue());
				}
				super.invokeExplosively(target, fVal.toArray());

			}
		} catch (GeneratorException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String toString() {

		String result = getMethod().getName();
		System.out.println("result" + result);
		return result;
	}

}
