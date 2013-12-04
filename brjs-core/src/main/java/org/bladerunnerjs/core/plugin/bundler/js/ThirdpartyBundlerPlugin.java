package org.bladerunnerjs.core.plugin.bundler.js;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.core.plugin.bundler.AbstractBundlerPlugin;
import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.core.plugin.taghandler.TagHandlerPlugin;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ContentPathParser;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.NonBladerunnerJsLibManifest;
import org.bladerunnerjs.model.NonBladerunnerJsLibSourceModule;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.UnableToInstantiateAssetFileException;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.utility.RequestParserBuilder;


public class ThirdpartyBundlerPlugin extends AbstractBundlerPlugin implements BundlerPlugin, TagHandlerPlugin
{
	private ContentPathParser requestParser;
	private List<String> prodRequestPaths = new ArrayList<>();
	private BRJS brjs;

	{
		RequestParserBuilder requestParserBuilder = new RequestParserBuilder();
		requestParserBuilder
			.accepts("thirdparty/bundle.js").as("bundle-request")
				.and("thirdparty/module/<module>/<file-path>").as("file-request")
			.where("module").hasForm(".+");
		
		requestParser = requestParserBuilder.build();
		prodRequestPaths.add(requestParser.createRequest("bundle-request"));
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;	
	}
	
	@Override
	public String getTagName()
	{
		return getRequestPrefix();
	}

	@Override
	public List<SourceModule> getSourceFiles(AssetLocation assetLocation)
	{
		try
		{
    		List<SourceModule> sourceFiles = new ArrayList<SourceModule>();
    		if (assetLocation.getAssetContainer() instanceof JsLib)
    		{
    			NonBladerunnerJsLibManifest manifest = new NonBladerunnerJsLibManifest(assetLocation);
    			if (manifest.fileExists())
    			{
    				NonBladerunnerJsLibSourceModule sourceFile = (NonBladerunnerJsLibSourceModule) assetLocation.getAssetContainer().root().getAssetFile(NonBladerunnerJsLibSourceModule.class, assetLocation, assetLocation.dir());
    				sourceFile.initManifest(manifest);
    				sourceFiles.add( sourceFile );
    			}
    		}
    		return sourceFiles;
		}
		catch (ConfigException | UnableToInstantiateAssetFileException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<LinkedAsset> getLinkedResourceFiles(AssetLocation assetLocation)
	{
		// TODO Auto-generated method stub
		return Arrays.asList();
	}

	@Override
	public List<Asset> getResourceFiles(AssetLocation assetLocation)
	{
		// TODO Auto-generated method stub
		return Arrays.asList();
	}

	// TODO: this bundler shouldn't be a tag handler
	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException
	{
		// TODO Auto-generated method stub
	}
	
	@Override
	public String getRequestPrefix()
	{
		return "thirdparty";
	}
	
	@Override
	public String getMimeType()
	{
		return "text/javascript";
	}

	@Override
	public ContentPathParser getContentPathParser()
	{
		return requestParser;
	}

	@Override
	public void writeContent(ParsedContentPath request, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException
	{
		try {
			if(request.formName.equals("file-request")) {
				throw new RuntimeException("Not yet implemented!"); //TODO
			}
			else if(request.formName.equals("bundle-request")) {
				try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getDefaultOutputEncoding())) 
				{
					for(SourceModule sourceFile : bundleSet.getSourceFiles()) {
						if(sourceFile instanceof NonBladerunnerJsLibSourceModule)
						{
    						writer.write("// " + sourceFile.getRequirePath() + "\n");
    						IOUtils.copy(sourceFile.getReader(), writer);
    						writer.write("\n\n");
						}
					}
				}
			}
			else {
				throw new BundlerProcessingException("unknown request form '" + request.formName + "'.");
			}
		}
		catch(ConfigException | IOException ex) {
			throw new BundlerProcessingException(ex);
		}
	}

	@Override
	public List<String> getValidDevRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException
	{
		List<String> requestPaths = new ArrayList<>();
		requestPaths.add(requestParser.createRequest("bundle-request"));
		return requestPaths;
	}

	@Override
	public List<String> getValidProdRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException
	{
		return getValidDevRequestPaths(bundleSet, locale);
	}

}