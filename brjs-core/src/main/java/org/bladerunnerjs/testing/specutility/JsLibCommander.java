package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.Command;
import org.bladerunnerjs.testing.specutility.engine.CommanderChainer;
import org.bladerunnerjs.testing.specutility.engine.NodeCommander;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;


public class JsLibCommander extends NodeCommander<JsLib> {
	private final JsLib jsLib;
	private JsLibBuilder jsLibBuilder;

	public JsLibCommander(SpecTest modelTest, JsLib jsLib) {
		super(modelTest, jsLib);
		this.jsLib = jsLib;
		this.jsLibBuilder = new JsLibBuilder(modelTest, jsLib);
	}
	
	public CommanderChainer populate(final String libraryNamespace) {
		call(new Command() {
			public void call() throws Exception {
				jsLib.populate(libraryNamespace);
			}
		});
		
		return commanderChainer;
	}
	
	public CommanderChainer containsFile(String filePath) throws Exception {
		jsLibBuilder.containsFile(filePath);
		
		return commanderChainer;
	}
	
	public CommanderChainer containsFileWithContents(String filePath, String fileContents) throws Exception {
		jsLibBuilder.containsFileWithContents(filePath, fileContents);
		
		return commanderChainer;
	}
	
	public CommanderChainer containsFiles(String... filePaths) throws Exception {
		jsLibBuilder.containsFiles(filePaths);
		
		return commanderChainer;
	}
}
