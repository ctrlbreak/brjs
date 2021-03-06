package com.caplin.cutlass.command.testIntegration;

import java.io.File;
import java.util.List;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import org.bladerunnerjs.core.log.Logger;
import org.bladerunnerjs.core.log.LoggerType;
import com.caplin.cutlass.BRJSAccessor;


public class IntegrationTestRunner
{
	private Logger logger = BRJSAccessor.root.logger(LoggerType.COMMAND, IntegrationTestRunner.class);
	
	public Result runTests(File runnerConf, List<Class<?>> classes) throws Exception {
		logger.info("Running tests '" + classes.toString() + "'");
		
		JUnitCore testRunner = new JUnitCore();
		testRunner.addListener(new CutlassIntegrationTestRunListener());

		Result testResult = testRunner.run(classes.toArray(new Class<?>[0]));
		return testResult;
	}
	
}
