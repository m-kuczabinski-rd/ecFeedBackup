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

package com.testify.ecfeed.ui.modelif;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

public class ModelNodesTransfer extends ByteArrayTransfer {

	public static final String TRANSFER_TYPE = "ecFeedNodeTransfer";
	public static final int TRANSFER_ID = registerType(TRANSFER_TYPE);

	private static ModelNodesTransfer fInstance = new ModelNodesTransfer();
	
	public static ModelNodesTransfer getInstance(){
		return fInstance;
	}
	
	@Override
	protected String[] getTypeNames() {
		return new String[]{TRANSFER_TYPE};
	}

	@Override
	protected int[] getTypeIds() {
		return new int[]{TRANSFER_ID};
	}

//	@SuppressWarnings("unchecked")
	protected void javaToNative (Object object, TransferData transferData) {
//		super.javaToNative(serialize((List<GenericNode>)object), transferData);
//		
//		System.out.println("javaToNative: " + object + ", " + transferData);
	}

	/**
	 * This implementation of <code>nativeToJava</code> converts a platform specific 
	 * representation of a byte array to a java <code>byte[]</code>.   
	 * 
	 * @param transferData the platform specific representation of the data to be converted
	 * @return a java <code>byte[]</code> containing the converted data if the conversion was
	 * 		successful; otherwise null
	 * 
	 * @see Transfer#javaToNative
	 */
	protected Object nativeToJava(TransferData transferData) {
//		if ( !isSupportedType(transferData) || transferData.pValue == 0) return null;
//		int size = transferData.format * transferData.length / 8;
//		if (size == 0) return null;
//		byte[] buffer = new byte[size];
//		OS.memmove(buffer, transferData.pValue, size);
//		return buffer;
//		System.out.println("nativeToJava: " + transferData);
//		List<GenericNode> result = deserialize((byte[])super.nativeToJava(transferData)); 
//		return result;
		return null;
	}
	
//	private byte[] serialize(List<GenericNode> nodes){
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		ObjectOutput out = null;
//		try {
//			out = new ObjectOutputStream(bos);   
//			out.writeObject(nodes);
//		} catch (IOException e) {
//		} finally {
//			try {
//				if (out != null) {
//					out.close();
//				}
//			} catch (IOException ex) {
//			}
//			try {
//				bos.close();
//			} catch (IOException ex) {
//			}
//		}	
//		return bos.toByteArray();
//	}
//	
//	@SuppressWarnings("unchecked")
//	private List<GenericNode> deserialize(byte[] bytes){
//		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
//		ObjectInput in = null;
//		try {
//		  in = new ObjectInputStream(bis);
//		  Object o = in.readObject();
//		  return (List<GenericNode>)o;
//		}
//		catch (IOException e) {
//			System.out.println("IOException");
//		}
//		catch (ClassNotFoundException e) {
//			System.out.println("ClassNotFoundException");
//		} 
//		finally {
//		  try {
//		    bis.close();
//		  } catch (IOException ex) {
//				System.out.println("IOException");
//		  }
//		  try {
//		    if (in != null) {
//		      in.close();
//		    }
//		  } catch (IOException ex) {
//				System.out.println("IOException");
//		  }
//		}
//		return null;
//	}
}
