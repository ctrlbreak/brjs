package org.bladerunnerjs.spec.command;

import static org.bladerunnerjs.core.plugin.command.standard.JsDocCommand.Messages.*;

import java.io.File;

import org.bladerunnerjs.core.plugin.command.standard.JsDocCommand;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class JsDocCommandTest extends SpecTest {
	App app;
	JsLib appLib;
	File jsdocOutputDir;
	
	@Before
	public void initTestObjects() throws Exception {
		pluginLocator.pluginCommands.add(new JsDocCommand());
		given(brjs).hasBeenCreated();
		app = brjs.app("app");
		appLib = app.jsLib("lib");
		jsdocOutputDir = app.storageDir("jsdoc-toolkit");
	}
	
	
	@Test 
	public void runningJsDocCommandCausesApiDocsToBeCreated() throws Exception {
		given(app).hasBeenCreated()
			.and(appLib).containsFileWithContents("src/MyClass.js", "/** @constructor */MyClass = function() {};");
		when(brjs).runCommand("jsdoc", "app");
		then(jsdocOutputDir).containsFile("MyClass.html")
			.and(output).containsLine(API_DOCS_GENERATED_MSG, jsdocOutputDir.getPath());
	}
	
	@Test 
	public void runningJsDocCommandWithVerboseFlagCausesApiDocsToBeCreated() throws Exception {
		given(app).hasBeenCreated()
			.and(appLib).containsFileWithContents("src/MyClass.js", "/** @constructor */MyClass = function() {};");
		when(brjs).runCommand("jsdoc", "app", "-v");
		then(jsdocOutputDir).containsFile("MyClass.html")
			.and(output).containsLine(API_DOCS_GENERATED_MSG, jsdocOutputDir.getPath());
	}
	
	// TODO: add this test once logging and console output have been merged into one thing, and we can use the same partial
	// line checking available for console output
	@Ignore
	@Test
	public void verboseFlagCausesAParseMessageForEachClass() throws Exception {
	}
	
	@Test
	public void runningJsDocForInvalidAppThrowsException() throws Exception {
		when(brjs).runCommand("jsdoc", "app");
		then(exceptions).verifyException(NodeDoesNotExistException.class, unquoted("App 'app' does not exist"));
	}
	
	@Test
	public void runningJsDocWithTooFewArgsThrowsEception() throws Exception {
		when(brjs).runCommand("jsdoc");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'app-name' is required"));
	}

	@Test
	public void runningJsDocWithTooManyArgsThrowsException() throws Exception {
		when(brjs).runCommand("jsdoc", "app", "extra");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: extra"));
	}
	
	@Test
	public void runningJsDocWithVerboseFlagAndTooManyArgsThrowsException() throws Exception {
		when(brjs).runCommand("jsdoc", "app", "extra");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: extra"));
	}
}
