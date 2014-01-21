package org.bladerunnerjs.spec.bundling.caching;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class AspectBundlingCacheTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private JsLib sdkLib1, sdkLib2;
	private JsLib appThirdparty;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			
			sdkLib1 = brjs.sdkLib();
			sdkLib2 = brjs.sdkLib();
			
			appThirdparty = app.nonBladeRunnerLib("appThirdpartyLib");
	}

	// Source module for a given asset container (SDK JS lib)
	@Test
	public void indexPageSdkJsAreNotCachedWhenRequestedTwiceFromIndexPage() throws Exception {
		given(sdkLib1).hasClass("br.SdkLib1")
			.and(sdkLib2).hasClass("br.SdkLib2")
			.and(aspect).indexPageHasContent("require('br/SdkLib1');")
			.and(aspect).requestHasBeenReceived("/default-aspect/js/dev/en_GB/combined/bundle.js");
		when(aspect).indexPageContentAppended("require('br/SdkLib2');")
			.and(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsDefinedClasses("br/SdkLib1", "br/SdkLib2");
	}
	
	// Asset locations (app thirdparty libraries) for a given asset container (application)
	@Test
	public void aspectBundlesAppLegacyThirdpartyLibsIfTheyAreReferencedInTheIndexPage() throws Exception {
		given(aspect).indexPageHasContent("")
			.and(aspect).requestHasBeenReceived("/default-aspect/js/dev/en_GB/combined/bundle.js");
		when(appThirdparty).containsFileWithContents("src1.js", "src1 contents")
			.and(appThirdparty).containsFileWithContents("library.manifest", "js: src1.js")
			.and(aspect).indexPageContentAppended("require('" + appThirdparty.getName() + "');")
			.and(app).requestReceived("/default-aspect/js/dev/en_GB/combined/bundle.js", response);
		then(response).containsText("src1 contents");
	}
	
}
