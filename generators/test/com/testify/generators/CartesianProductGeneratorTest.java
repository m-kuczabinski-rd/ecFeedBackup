package com.testify.generators;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import com.testify.ecfeed.api.GeneratorException;
import com.testify.ecfeed.api.IConstraint;
import com.testify.generators.monitors.SilentProgressMonitor;

public class CartesianProductGeneratorTest {

	private class NegativeConstraint implements IConstraint<String>, Predicate<List<String>>{
		
		private final String[] fForbiddenValues;

		public NegativeConstraint(String[] forbiddenValues) {
			fForbiddenValues = forbiddenValues;
		}
		
		@Override
		public boolean evaluate(List<String> values) {
			if(fForbiddenValues == null) return true;
			for(String forbidden : fForbiddenValues){
				if(values.contains(forbidden)) return false;
			}
			return true;
		}

		@Override
		public boolean apply(List<String> arg0) {
			return evaluate(arg0);
		}
	}
	
	private List<List<String>> fInput = new ArrayList<List<String>>();
	private List<Set<String>> fReferenceInput = new ArrayList<Set<String>>();
	
	@Before
	public void prepareInput() {
		String[][] input = new String[][]{
				{"a1", "a2", "a3", "a4", "a5"}, 
				{"b1", "b2", "b3", "b4", "b5"}, 
				{"c1", "c2", "c3", "c4", "c5"},
				{"d1", "d2", "d3", "d4", "d5"}};

		fInput.clear(); 
		fReferenceInput.clear();
		
		for(String[] row : input){
			List<String> list = new ArrayList<String>(Arrays.asList(row));
			Set<String> set = new LinkedHashSet<String>(list);
			
			fInput.add(list);
			fReferenceInput.add(set);
		}
	}

	@Test
	public void generatorTest() {
		Collection<IConstraint<String>> constraints = new HashSet<IConstraint<String>>();

		Set<List<String>> referenceResult = Sets.cartesianProduct(fReferenceInput);
		CartesianProductGenerator<String> generator = new CartesianProductGenerator<String>();
		assertTrue(generator.requiredParameters().size() == 0);
		
		try {
			generator.initialize(fInput, constraints, null);
			List<String> next; int size = 0;
			while((next = generator.getNext()) != null){
				assertTrue(referenceResult.contains(next));
				size++;
			}
			assertEquals(size, referenceResult.size());
		} catch (GeneratorException e) {
			fail("Unexpected exception from generator");
			e.printStackTrace();
		}
	}

	@Test
	public void constraintsTest(){
		Collection<NegativeConstraint> constraints = new HashSet<NegativeConstraint>();
		constraints.add(new NegativeConstraint(new String[]{"a1"}));

		constraintsTestImplementation(constraints);

		constraints.add(new NegativeConstraint(new String[]{"b2"}));
		constraintsTestImplementation(constraints);
	}
	
	public void constraintsTestImplementation(Collection<NegativeConstraint> constraints){
		Set<List<String>> referenceResult = Sets.cartesianProduct(fReferenceInput);
		for(Predicate<List<String>> predicate : constraints){
			referenceResult = Sets.filter(referenceResult, predicate);
		}

		try {
			CartesianProductGenerator<String> generator = new CartesianProductGenerator<String>();
			generator.initialize(fInput, constraints, null);

			List<String> next; int size = 0;
			while((next = generator.getNext()) != null){
				assertTrue(referenceResult.contains(next));
				size++;
			}
			assertEquals(referenceResult.size(), size);
		} catch (GeneratorException e) {
			fail("Unexpected exception from generator");
			e.printStackTrace();
		}
	}
	
	@Test
	/*
	 * Check that after generator returns null, it will return null also next time 
	 */
	public void finitenessTest(){
		Collection<NegativeConstraint> constraints = new HashSet<NegativeConstraint>();

		CartesianProductGenerator<String> generator = new CartesianProductGenerator<String>();
		try {
			generator.initialize(fInput, constraints, null);
			while((generator.getNext()) != null); // just go to the end
			assertEquals(null, generator.getNext());
			
		} catch (GeneratorException e) {
			fail("Unexpected exception from generator");
			e.printStackTrace();
		}
	}
	
	@Test
	public void cancelabilityTest(){
		Collection<NegativeConstraint> constraints = new HashSet<NegativeConstraint>();
		Set<List<String>> referenceResult = Sets.cartesianProduct(fReferenceInput);

		CartesianProductGenerator<String> generator = new CartesianProductGenerator<String>();
		try {
			IProgressMonitor monitor = new SilentProgressMonitor();
			generator.initialize(fInput, constraints, monitor);
			Set<List<String>> generatedData = new HashSet<List<String>>();
			
			for(int i = 0; i < 5; i++){
				//generate a few elements
				generatedData.add(generator.getNext());
			}
			
			//set cancel
			monitor.setCanceled(true);
			
			//make sure that no more data is generated
			assertEquals(null, generator.getNext());
			
			//continue working
			monitor.setCanceled(false);
			//generate rest of data
			List<String> next;
			while((next = generator.getNext()) != null){
				generatedData.add(next);
			}

			//assert that all data was generated
			assertTrue(generatedData.containsAll(referenceResult));
			assertEquals(referenceResult.size(), generatedData.size());
			
		} catch (GeneratorException e) {
			fail("Unexpected exception from generator");
			e.printStackTrace();
		}
	}
	
	@Test
	public void resetTest(){
		Collection<NegativeConstraint> constraints = new HashSet<NegativeConstraint>();
		Set<List<String>> referenceResult = Sets.cartesianProduct(fReferenceInput);
		CartesianProductGenerator<String> generator = new CartesianProductGenerator<String>();
		Set<List<String>> generatedData = new HashSet<List<String>>();
		List<String> next;
		
		//generate a few elements
		try{
			generator.initialize(fInput, constraints, null);
			for(int i = 0; i < 10; i++){
				generator.getNext();
			}

			generator.reset();
			while((next = generator.getNext())!= null){
				generatedData.add(next);
			}
			
			assertTrue(generatedData.containsAll(referenceResult));
			assertEquals(referenceResult.size(), generatedData.size());
		}catch(GeneratorException e){
			fail("Unexpected exception from generator");
			e.printStackTrace();
		}
	}
	
	@Test
	public void exceptionsTest(){
		CartesianProductGenerator<String> generator = new CartesianProductGenerator<String>();
		boolean exceptionCaught = false;
		try{
			generator.reset();
		}
		catch(GeneratorException e){
			exceptionCaught = true;
		}
		assertTrue(exceptionCaught);
		
		exceptionCaught = false;
		try{
			generator.getNext();
		}
		catch(GeneratorException e){
			exceptionCaught = true;
		}
		assertTrue(exceptionCaught);
	}

}
