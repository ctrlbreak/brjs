package org.bladerunnerjs.model;

import java.io.File;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.core.console.ConsoleWriter;
import org.bladerunnerjs.core.log.LoggerFactory;
import org.bladerunnerjs.model.engine.AbstractRootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;


public abstract class AbstractBRJSRootNode extends AbstractRootNode implements BRJSNode {
	public AbstractBRJSRootNode(File dir, LoggerFactory loggerFactory, ConsoleWriter consoleWriter) {
		super(dir, loggerFactory, consoleWriter);
	}
	
	@Override
	public BRJS root() {
		return (BRJS) rootNode;
	}
	
	@Override
	public void populate() throws InvalidNameException, ModelUpdateException {
		BRJSNodeHelper.populate(this);
	}
	
	@Override
	public String getTemplateName() {
		return BRJSNodeHelper.getTemplateName(this);
	}
}
