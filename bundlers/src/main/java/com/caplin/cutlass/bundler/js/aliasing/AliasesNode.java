package com.caplin.cutlass.bundler.js.aliasing;

import org.bladerunnerjs.model.exception.request.BundlerProcessingException;

public class AliasesNode implements AliasingNode {

	private AliasContext context;
	private String scenario;
	private String group;
	
	public AliasesNode(String scenario, String groupName)
	{
		this.scenario = scenario;
		this.group = groupName;
	}
	
	@Override
	public void register() throws BundlerProcessingException {
		throw new BundlerProcessingException("aliasDefinitions.xml should not contain any 'aliases' nodes");
	}

	@Override
	public void use() throws BundlerProcessingException {
		if (scenario != null)
		{
			context.getAliasRegistry().setScenario(scenario);
		}
		if (group != null)
		{
			String[] groups = group.split(" ");
			for (String groupName : groups)
			{
				GroupNode groupNode = new GroupNode(groupName, null);
				groupNode.setContext(context);
				groupNode.use();
			}
		}
	}

	@Override
	public void setContext(AliasContext context) {
		this.context = context;
	}

}
