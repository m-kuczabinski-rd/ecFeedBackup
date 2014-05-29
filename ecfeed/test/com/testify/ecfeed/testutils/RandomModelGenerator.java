package com.testify.ecfeed.testutils;

import static com.testify.ecfeed.model.Constants.ROOT_NODE_NAME_REGEX;

import java.util.Random;

import nl.flotsam.xeger.Xeger;

import com.testify.ecfeed.model.ClassNode;
import com.testify.ecfeed.model.RootNode;

public class RandomModelGenerator {
	
	private Random rand = new Random();
	
	public int MAX_CLASSES = 0;
	public int MAX_METHODS = 0;
	
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
		return new ClassNode("package.name.ClassName");
	}



	private String generateString(String regex){
		Xeger generator = new Xeger(regex);
		return generator.generate();
	}
	
}
