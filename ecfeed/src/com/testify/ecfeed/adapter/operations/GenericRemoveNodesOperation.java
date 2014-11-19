package com.testify.ecfeed.adapter.operations;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.testify.ecfeed.model.AbstractNode;

public class GenericRemoveNodesOperation extends BulkOperation {

	private Set<AbstractNode> fRemoved;
	
	public GenericRemoveNodesOperation(Collection<? extends AbstractNode> nodes, boolean validate){
		super(OperationNames.REMOVE_NODES, false);
		fRemoved = new HashSet<>(nodes);
		Iterator<AbstractNode> it = fRemoved.iterator();
		while(it.hasNext()){
			AbstractNode node = it.next();
			for(AbstractNode ancestor : node.getAncestors()){
				if(fRemoved.contains(ancestor)){
					it.remove();
					break;
				}
			}
		}
		
		
		for(AbstractNode node : fRemoved){
			addOperation(FactoryRemoveOperation.getRemoveOperation(node, validate));
		}
	}
}
