package org.bladerunnerjs.specutil.engine;

import static org.junit.Assert.*;

import org.bladerunnerjs.model.AppConf;
import org.bladerunnerjs.model.exception.ConfigException;


public class AppConfVerifier
{

	AppConf appConf;
	
	public AppConfVerifier(AppConf appConf)
	{
		this.appConf = appConf;
	}

	public void namespaceIs(String string) throws ConfigException
	{
		assertEquals("app conf has the wrong namespace", appConf.getAppNamespace(), string);
	}

}
