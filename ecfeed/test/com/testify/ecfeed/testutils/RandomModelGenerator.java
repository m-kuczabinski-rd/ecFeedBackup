package com.testify.ecfeed.testutils;

import static com.testify.ecfeed.model.Constants.*;

import java.util.Random;

import nl.flotsam.xeger.Xeger;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.MethodNode;
import com.testify.ecfeed.model.RootNode;

public class RandomModelGenerator {
	
	private Random rand = new Random();
	
	public int MAX_CLASSES = 10;
	public int MAX_METHODS = 10;
	
	public RootNode generateModel(){
		String name = generateString(ROOT_NODE_NAME_REGEX);
		
		RootNode root = new RootNode(name);
		
		for(int i = 0; i < rand.nextInt(MAX_CLASSES) + 1; i++){
			root.addClass(generateClass());
		}
		
		return root;
	}

	
	
	public ClassNode generateClass() {
		String name = generateString(CLASS_NODE_NAME_REGEX);

		ClassNode _class = new ClassNode(name);
		
		for(int i = 0; i < rand.nextInt(MAX_METHODS) + 1; i++){
			_class.addMethod(generateMethod());
		}

		return _class;
	}

	public MethodNode generateMethod(){
		String name = generateString(METHOD_NODE_NAME_REGEX);
		
		MethodNode method = new MethodNode(name);
		
		return method;
	}
	

	private String generateString(String regex){
		Xeger generator = new Xeger(regex);
		return generator.generate();
	}
	
}
