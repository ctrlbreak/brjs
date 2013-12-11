package org.bladerunnerjs.plugin;

import java.util.List;

import org.bladerunnerjs.model.BRJS;


public interface PluginLocator
{
	void createPlugins(BRJS brjs);
	List<CommandPlugin> getCommandPlugins();
	List<ModelObserverPlugin> getModelObserverPlugins();
	List<ContentPlugin> getContentPlugins();
	List<BundlerContentPlugin> getBundlerContentPlugins();
	List<TagHandlerPlugin> getTagHandlerPlugins();
	List<BundlerTagHandlerPlugin> getBundlerTagHandlerPlugins();
	List<AssetPlugin> getAssetPlugins();
	List<MinifierPlugin> getMinifierPlugins();
}