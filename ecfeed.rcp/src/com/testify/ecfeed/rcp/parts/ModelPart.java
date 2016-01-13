package com.testify.ecfeed.rcp.parts;

import javax.annotation.PostConstruct;

import org.eclipse.swt.widgets.Composite;

public class ModelPart {
	
	ModelPart() {
		System.out.println(this.getClass().getSimpleName() + " constructor");
	}

	 @PostConstruct
	  public void createControls(Composite parent) {
		 System.out.println(this.getClass().getSimpleName() + " post construct");
	 }
}
