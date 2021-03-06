package org.bladerunnerjs.core.plugin.taghandler;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.bladerunnerjs.core.plugin.VirtualProxyPlugin;
import org.bladerunnerjs.model.BundleSet;

public class VirtualProxyTagHandlerPlugin extends VirtualProxyPlugin implements TagHandlerPlugin {
	private TagHandlerPlugin tagHandlerPlugin;
	
	public VirtualProxyTagHandlerPlugin(TagHandlerPlugin tagHandlerPlugin) {
		super(tagHandlerPlugin);
		this.tagHandlerPlugin = tagHandlerPlugin;
	}
	
	@Override
	public String getTagName() {
		return tagHandlerPlugin.getTagName();
	}
	
	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		initializePlugin();
		tagHandlerPlugin.writeDevTagContent(tagAttributes, bundleSet, locale, writer);
	}
	
	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		initializePlugin();
		writeProdTagContent(tagAttributes, bundleSet, locale, writer);
	}
}
