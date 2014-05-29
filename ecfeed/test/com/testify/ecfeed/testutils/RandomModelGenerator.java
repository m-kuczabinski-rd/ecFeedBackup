package com.testify.ecfeed.testutils;

import static com.testify.ecfeed.model.Constants.*;

import java.util.Random;

import nl.flotsam.xeger.Xeger;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.RootNode;

public class RandomModelGenerator {
	
	private Random rand = new Random();
	
	public int MAX_CLASSES = 5;
	public int MAX_METHODS = 5;
	
	public RootNode generateModel(){
		String name = generateString(ROOT_NODE_NAME_REGEX);
		
		RootNode root = new RootNode(name);
		int classCount = rand.nextInt(MAX_CLASSES); 
		
		
		for(int i = 0; i < classCount; i++){
			root.addClass(generateClass());
		}
		
		return root;
	}

	
	
	public ClassNode generateClass() {
		String name = generateString(CLASS_NODE_NAME_REGEX);

		return new ClassNode(name);
	}



	private String generateString(String regex){
		Xeger generator = new Xeger(regex);
		return generator.generate();
	}
	
}
