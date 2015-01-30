/*******************************************************************************
 * Copyright (c) 2015 Testify AS..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Patryk Chamuczynski (p.chamuczynski(at)radytek.com) - initial implementation
 ******************************************************************************/

package com.testify.ecfeed.adapter;

public enum EImplementationStatus {
	IMPLEMENTED, PARTIALLY_IMPLEMENTED, NOT_IMPLEMENTED, IRRELEVANT;
	
	public String toString(){
		switch(this){
		case IMPLEMENTED: return "implemented";
		case PARTIALLY_IMPLEMENTED: return "partially implemented";
		case NOT_IMPLEMENTED: return "not implemented";
		case IRRELEVANT: return "irrelevant implementation status";
		}
		return "";
	}
}
