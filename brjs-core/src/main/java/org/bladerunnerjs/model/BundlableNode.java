package org.bladerunnerjs.model;

import java.util.List;

import org.bladerunnerjs.model.aliasing.AliasDefinition;
import org.bladerunnerjs.model.aliasing.AliasName;
import org.bladerunnerjs.model.aliasing.AliasesFile;
import org.bladerunnerjs.model.aliasing.AmbiguousAliasException;
import org.bladerunnerjs.model.aliasing.UnresolvableAliasException;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;


public interface BundlableNode extends Node, AssetContainer {
	AliasesFile aliasesFile();
	SourceFile getSourceFile(String requirePath) throws RequirePathException;
	List<LinkedAssetFile> seedFiles();
	List<AssetContainer> getAssetContainers();
	BundleSet getBundleSet() throws ModelOperationException;
	AliasDefinition getAlias(AliasName aliasName) throws UnresolvableAliasException, AmbiguousAliasException, BundlerFileProcessingException;
}
