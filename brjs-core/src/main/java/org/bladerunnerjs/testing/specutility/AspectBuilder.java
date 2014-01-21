package org.bladerunnerjs.testing.specutility;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BladerunnerUri;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.testing.specutility.engine.AssetContainerBuilder;
import org.bladerunnerjs.testing.specutility.engine.BuilderChainer;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;

public class AspectBuilder extends AssetContainerBuilder<Aspect> {
	private final Aspect aspect;
	
	public AspectBuilder(SpecTest modelTest, Aspect aspect)
	{
		super(modelTest, aspect);
		this.aspect = aspect;
	}
	
	public BuilderChainer indexPageRefersTo(String className) throws Exception {
		FileUtils.write(aspect.file("index.html"), className);
		
		return builderChainer;
	}
	
	public BuilderChainer resourceFileRefersTo(String resourceFileName, String className) throws Exception {
		FileUtils.write(aspect.assetLocation("resources").file(resourceFileName), "<root refs='" + className + "'/>");
		
		return builderChainer;
	}
	
	public BuilderChainer sourceResourceFileRefersTo(String resourceFileName, String className) throws Exception {
		FileUtils.write(aspect.assetLocation("src").file(resourceFileName), "<root refs='" + className + "'/>");
		
		return builderChainer;
	}
	
	public BuilderChainer indexPageHasContent(String content) throws Exception {
		return indexPageRefersTo(content);
	}

	public BuilderChainer indexPageRequires(JsLib thirdpartyLib) throws Exception {
		FileUtils.write(aspect.file("index.html"), "require('"+thirdpartyLib.getName()+"');");
		
		return builderChainer;
	}

	// TODO figure out a better way to make use of this code which is taken from AspectCommander (extending NodeCommander - which extends ModelCommander)
	public BuilderChainer requestHasBeenReceived(String requestPath) throws MalformedRequestException, ResourceNotFoundException, BundlerProcessingException, UnsupportedEncodingException 
	{
		App app = aspect.getApp();
		
		BladerunnerUri uri = new BladerunnerUri(app.root(), app.dir(), "/app", requestPath, null);
		ByteArrayOutputStream responseOutput = new ByteArrayOutputStream();
		app.handleLogicalRequest(uri, responseOutput);
	
		return builderChainer;
	}
}
