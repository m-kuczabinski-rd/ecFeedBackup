package com.testify.generators.algorithms;

import java.util.Iterator;
import java.util.ArrayList;

import com.testify.ecfeed.api.IConstraint;
import com.testify.ecfeed.api.ITestGenAlgorithm;

public class Cartesian implements ITestGenAlgorithm {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public ArrayList[] generate(ArrayList[] input, IConstraint[] constraints) {
		
		if(input.length == 0){
			return input; 
		}
		
		ArrayList<ArrayList> product = new ArrayList<ArrayList>();
		for(Object element : input[0]){
			ArrayList v = new ArrayList();
			v.add(element);
			product.add(v);
		}
		
		for(int i = 1; i < input.length; i++){
			product = cartesian(product, input[i]);
		}

		for(IConstraint constraint : constraints){
			for(Iterator<ArrayList> it = product.iterator(); it.hasNext();){
				if(constraint.evaluate(it.next()) == false){
					it.remove();
				}
			}
		}
		
		return product.toArray(new ArrayList[]{});
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ArrayList<ArrayList> cartesian(ArrayList<ArrayList> product, ArrayList input) {
		ArrayList<ArrayList> newProduct = new ArrayList<ArrayList>();
		for(ArrayList vector : product){
			for(Object element : input){
				ArrayList newElement = ((ArrayList)vector.clone());
				newElement.add(element);
				newProduct.add(newElement);
			}
		}
		return newProduct;
	}
}
