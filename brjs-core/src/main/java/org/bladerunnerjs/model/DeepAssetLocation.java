package org.bladerunnerjs.model;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public class DeepAssetLocation extends ShallowAssetLocation {
	
	Map<File,AssetLocation> resourcesMap = new LinkedHashMap<File,AssetLocation>();
	
	public DeepAssetLocation(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
	}

	@Override
	public List<LinkedAssetFile> seedResources()
	{
		List<LinkedAssetFile> assetFiles = new LinkedList<LinkedAssetFile>();
		
		if (dir().exists())
		{
    		Iterator<File> fileIterator = FileUtils.iterateFilesAndDirs(dir(), FalseFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
    		while (fileIterator.hasNext())
    		{
    			File dir = fileIterator.next();
    			if (!dir.equals(dir()))
    			{
        			AssetLocation dirResources = resourcesMap.get(dir);
        			if (dirResources == null)
        			{
        				dirResources = new ShallowAssetLocation(getAssetContainer().root(), getAssetContainer(), dir);
        				resourcesMap.put(dir, dirResources);
        			}
        			assetFiles.addAll(dirResources.seedResources());
    			}
    		}
		}
		
		return assetFiles;
	}
	
}