package com.caplin.cutlass.command.testIntegration;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang3.StringUtils;

import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.sinbin.CutlassConfig;
import com.caplin.cutlass.bundler.BundlerFileUtils;
import org.bladerunnerjs.model.utility.FileUtility;
import com.caplin.cutlass.structure.CutlassDirectoryLocator;
import com.caplin.cutlass.structure.model.path.AppPath;

public class TestCompiler
{

	public List<File> compileTestDirs(List<File> testContainerDirs) throws CommandOperationException 
	{
		List<File> classRoots = new ArrayList<File>();
		
		for (File testContainerDir : testContainerDirs) 
		{
			File commonSrcDir = AppPath.locateAncestorPath(testContainerDir).testIntegrationSrcPath().getDir();
			commonSrcDir = (commonSrcDir.exists()) ? commonSrcDir : null;
			
			File testDir = new File(testContainerDir, "tests");
			File srcDir = new File(testContainerDir, "src-test");
			srcDir = (srcDir.exists()) ? srcDir : null;
			String sourcePath = getSourcePath(commonSrcDir, srcDir);
			File compiledClassDir = null;
			
			verifyClassNames(testDir, true);
			if(srcDir != null)
			{
				verifyClassNames(srcDir, false);
			}
			
			try
			{
				compiledClassDir = getCompiledClassDir(testContainerDir);
			}
			catch (IOException ex)
			{
				throw new CommandOperationException("Error creating directory for compiled tests.", ex);
			}
			
			// see http://help.eclipse.org/helios/index.jsp?topic=/org.eclipse.jdt.doc.isv/guide/jdt_api_compile.htm for command line args
			String[] compilerArgs = new String[]{ "-1.6", "-sourcepath", sourcePath.toString(), "-d", compiledClassDir.getPath(), "-encoding", "UTF-8", "-nowarn", testContainerDir.getPath() };
			boolean compileReturnValue = org.eclipse.jdt.core.compiler.batch.BatchCompiler.compile(
					compilerArgs, 
					new PrintWriter(System.out), 
					new PrintWriter(System.err), 
					null );
			if (!compileReturnValue)
			{
				throw new CommandOperationException("Error compiling files in '" + testContainerDir.getPath() + "'.");
			}
			classRoots.add(compiledClassDir);
		}
		return classRoots;
	}
	
	private void verifyClassNames(File classesDir, boolean isTestDir) throws CommandOperationException
	{
		List<File> sourceFiles = BundlerFileUtils.recursiveListFiles(classesDir, new SuffixFileFilter(".java"));
		
		for(File sourceFile : sourceFiles)
		{
			boolean isTestClass = sourceFile.getName().endsWith("Test.java");
			
			if(isTestDir != isTestClass)
			{
				if(isTestDir)
				{
					throw new CommandOperationException("'" + sourceFile.getName() +
						"' doesn't end 'Test.java' but has been placed into a 'tests' directory ('" + sourceFile.getAbsolutePath() + "').");
				}
				else
				{
					throw new CommandOperationException("'" + sourceFile.getName() +
						"' ends with 'Test.java' but has been placed outside of 'tests' directory ('" + sourceFile.getAbsolutePath() + "').");
				}
			}
		}
	}

	private String getSourcePath(File commonSrcDir, File srcDir)
	{
		StringBuilder sourcePath = new StringBuilder();
		
		if(commonSrcDir != null)
		{
			sourcePath.append(commonSrcDir.getAbsolutePath());
		}
		
		if(srcDir != null)
		{
			if(sourcePath.length() > 0)
			{
				sourcePath.append(";");
			}
			
			sourcePath.append(srcDir.getAbsolutePath());
		}
		
		return sourcePath.toString();
	}

	public List<Class<?>> loadClasses(List<File> classDirs) throws CommandOperationException
	{
		List<File> classFiles = BundlerFileUtils.recursiveListFiles(classDirs, new SuffixFileFilter(".class"));
   		List<Class<?>> loadedClasses = new ArrayList<Class<?>>();
		ClassLoader classLoader = this.getClass().getClassLoader();
		
		for (File classFile : classFiles)
		{
			if(classFile.getName().endsWith("Test.class"))
			{
				try
				{
					@SuppressWarnings("resource")
					URLClassLoader classloader = new URLClassLoader( new URL[] { classFile.getParentFile().toURI().toURL() }, classLoader);
					Class<?> nextClass = classloader.loadClass(getTestClassName(classFile));
					loadedClasses.add(nextClass);
				}
				catch (MalformedURLException ex)
				{
					throw new CommandOperationException(ex);
				}
				catch (ClassNotFoundException ex)
				{
					throw new CommandOperationException(ex);
				}
			}
		}
		return loadedClasses;
	}

	public File getCompiledClassDir(File testDir) throws IOException 
	{
		File parentApplication = CutlassDirectoryLocator.getAppRootDir(testDir.getAbsoluteFile());
		String applicationPath = parentApplication.getAbsolutePath();
		String testDirPath = testDir.getAbsolutePath();
		String testPathRelativeToParentApp = testDirPath.replace(applicationPath, "").replace("\\", "/");
		// TODO: fix this broken replace
		testPathRelativeToParentApp = StringUtils.substringBefore(testPathRelativeToParentApp, CutlassConfig.TEST_INTEGRATION_PATH);
		return new File(getClassesRoot(testDir), testPathRelativeToParentApp+"/"+CutlassConfig.TEST_INTEGRATION_PATH);
	}
	
	public String getTestClassName(File testFile) 
	{
		String testPath = testFile.getAbsolutePath().replace("\\", "/");
		String classNamePath = StringUtils.substringAfter(testPath, CutlassConfig.TEST_INTEGRATION_PATH+"/");
		String className = StringUtils.substringBeforeLast(classNamePath, ".").replace("/", ".");
		
		return className;
	}
	
	public File getClassesRoot(File root) throws IOException
	{
		File temporaryClassesDir = FileUtility.createTemporaryDirectory("cutlass-compiled-tests");
		
		return new File(temporaryClassesDir, CutlassConfig.TEST_INTEGRATION_CLASSES_DIRNAME);
	}
	
}
