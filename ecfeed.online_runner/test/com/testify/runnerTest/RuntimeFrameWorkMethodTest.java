package com.testify.runnerTest;



import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Test;

import com.testify.ecfeed.api.GeneratorException;
import com.testify.ecfeed.api.IConstraint;
import com.testify.ecfeed.api.IGenerator;
import com.testify.ecfeed.api.IGeneratorParameter;
import com.testify.ecfeed.model.PartitionNode;
import com.testify.generators.monitors.SilentProgressMonitor;
import com.testify.generators.test.utils.TestUtils;
import com.testify.runner.RuntimeFrameworkMethod;


public class RuntimeFrameWorkMethodTest  {
	
	public IGenerator<PartitionNode> fgenerator;
	IProgressMonitor progressMonitor = new SilentProgressMonitor();
	TestUtils utils = new TestUtils();
	int MAX_VARIABLES = 6;
	int MAX_PARTITIONS = 10;
	
	public Method data() throws NoSuchMethodException, ClassNotFoundException {
		Method[] method = null;	
		Method method1 = null;
		try {
			Class<?> c = Class.forName("com.testify.runnerTest.exampleTest");
			method = c.getDeclaredMethods();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		method1=method[1];
		return method1;
	}

	@SuppressWarnings("unchecked")
//	@Test
	public void testData() {

		List<List<String>> input = utils.prepareInput(5, 5);

		fgenerator = new IGenerator<PartitionNode>() {

			@Override
			public List<IGeneratorParameter> parameters() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void initialize(
					List<? extends List<PartitionNode>> inputDomain,
					Collection<? extends IConstraint<PartitionNode>> constraints,
					Map<String, Object> parameters,
					IProgressMonitor progressMonitor) throws GeneratorException {
				// TODO Auto-generated method stub

			}

			@Override
			public List<PartitionNode> next() throws GeneratorException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void reset() {
				// TODO Auto-generated method stub

			}
		};
		Collection<? extends IConstraint<PartitionNode>> constraints = (Collection<? extends IConstraint<PartitionNode>>) utils
				.generateRandomConstraints(input);
		Map<String, Object> parameters = new HashMap<String, Object>();
	
		
		try {
			RuntimeFrameworkMethod rtfm = new RuntimeFrameworkMethod(data(),
					fgenerator, constraints, input, progressMonitor, parameters);
			System.out.println(rtfm.toString());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	@Test
	public void testResults() throws NoSuchMethodException, ClassNotFoundException, Exception{
		fgenerator = new IGenerator<PartitionNode>() {

			@Override
			public List<IGeneratorParameter> parameters() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void initialize(
					List<? extends List<PartitionNode>> inputDomain,
					Collection<? extends IConstraint<PartitionNode>> constraints,
					Map<String, Object> parameters,
					IProgressMonitor progressMonitor) throws GeneratorException {
				// TODO Auto-generated method stub

			}

			@Override
			public List<PartitionNode> next() throws GeneratorException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void reset() {
				// TODO Auto-generated method stub

			}
		};
		
		
		  Class<?> c = Class.forName("com.testify.runnerTest.exampleTest");
		  Method methodch;
		  int i=0;
			
          Method[] method = c.getDeclaredMethods();
          for (int j = 0; j < method.length; j++) {
        	   methodch=method[i];
        		List<List<String>> input = utils.prepareInput(5, 5);
        		@SuppressWarnings("unchecked")
        		Collection<? extends IConstraint<PartitionNode>> constraints = (Collection<? extends IConstraint<PartitionNode>>) utils
        				.generateRandomConstraints(input);

        		Map<String, Object> parameters = new HashMap<String, Object>();
        		parameters.put("N", 3);
        		fgenerator.parameters();
        		RuntimeFrameworkMethod	rtfm=new RuntimeFrameworkMethod(methodch,(IGenerator<PartitionNode>) fgenerator, constraints, input, progressMonitor, parameters);
				PartitionNode doublePartition     = new PartitionNode("double", (double)0);
				
				try {
					rtfm.invokeExplosively(method, doublePartition);
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("" + rtfm);
         }
   }

}
