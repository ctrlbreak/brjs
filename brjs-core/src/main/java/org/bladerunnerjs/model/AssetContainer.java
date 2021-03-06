package org.bladerunnerjs.model;

import java.util.List;

/**
 * Represents a location that can contain assets (src or resources) such as an Aspect, Blade or Workbench.
 *
 */
public interface AssetContainer extends BRJSNode {
	App getApp();
	String getRequirePrefix();
	List<SourceFile> sourceFiles();
	SourceFile sourceFile(String requirePath);
	AssetLocation src();
	AssetLocation resources();
	List<AssetLocation> getAllAssetLocations();
}
