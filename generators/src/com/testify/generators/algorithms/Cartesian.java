package com.testify.generators.algorithms;

import java.util.Vector;

import com.testify.ecfeed.api.ITestGenAlgorithm;

public class Cartesian implements ITestGenAlgorithm {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Vector[] generate(Vector[] input) {
		
		if(input.length == 0){
			return input; 
		}
		
		Vector<Vector> product = new Vector<Vector>();
		for(Object element : input[0]){
			Vector v = new Vector();
			v.add(element);
			product.add(v);
		}
		
		for(int i = 1; i < input.length; i++){
			product = cartesian(product, input[i]);
		}
		
		return product.toArray(new Vector[]{});
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Vector<Vector> cartesian(Vector<Vector> product, Vector input) {
		Vector<Vector> newProduct = new Vector<Vector>();
		for(Vector vector : product){
			for(Object element : input){
				Vector newElement = ((Vector)vector.clone());
				newElement.add(element);
				newProduct.add(newElement);
			}
		}
		return newProduct;
	}
}
