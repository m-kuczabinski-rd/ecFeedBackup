package com.testify.ecfeed.adapter.operations;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.testify.ecfeed.model.GenericNode;

public class GenericRemoveNodesOperation extends BulkOperation {

	private Set<GenericNode> fRemoved;
	
	public GenericRemoveNodesOperation(Collection<? extends GenericNode> nodes, boolean validate){
		super(OperationNames.REMOVE_NODES, false);
		fRemoved = new HashSet<>(nodes);
		Iterator<GenericNode> it = fRemoved.iterator();
		while(it.hasNext()){
			GenericNode node = it.next();
			for(GenericNode ancestor : node.getAncestors()){
				if(fRemoved.contains(ancestor)){
					it.remove();
					break;
				}
			}
		}
		
		
		for(GenericNode node : fRemoved){
			addOperation(FactoryRemoveOperation.getRemoveOperation(node, validate));
		}
	}
}
