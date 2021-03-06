package org.bladerunnerjs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.naming.InvalidNameException;

import org.apache.commons.lang3.ArrayUtils;
import org.bladerunnerjs.ConsoleLoggerConfigurator;
import org.bladerunnerjs.logger.RootConsoleLogger;
import org.bladerunnerjs.core.log.LogConfiguration;
import org.bladerunnerjs.core.plugin.command.CommandList;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.logger.LogLevel;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.slf4j.impl.StaticLoggerBinder;

import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.command.analyse.DependencyAnalyserCommand;
import com.caplin.cutlass.command.analyse.PackageDepsCommand;
import com.caplin.cutlass.command.check.CheckCommand;
import com.caplin.cutlass.command.copy.CopyBladesetCommand;
import com.caplin.cutlass.command.export.ExportApplicationCommand;
import com.caplin.cutlass.command.importing.ImportApplicationCommand;
import com.caplin.cutlass.command.test.TestCommand;
import com.caplin.cutlass.command.test.TestServerCommand;
import com.caplin.cutlass.command.testIntegration.TestIntegrationCommand;
import com.caplin.cutlass.command.war.WarCommand;

// TODO: move all classes in brjs-runner into 'org.bladerunnerjs.runner'?
public class CommandRunner {
	public static void main(String[] args) {
		try {
			new CommandRunner().run(args);
		}
		catch (CommandArgumentsException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
		catch (Exception ex) {
			System.err.println(formatException(ex));
			System.exit(1);
		}
	}
	
	private static String formatException(Exception e) {
		ByteArrayOutputStream byteStreamOutputStream = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(byteStreamOutputStream);
		e.printStackTrace(printStream);
		
		return byteStreamOutputStream.toString().trim();
	}
	
	public void run(String[] args) throws CommandArgumentsException, CommandOperationException, InvalidNameException, ModelUpdateException {
		try {
			if (args.length < 1 || args[0] == null) throw new NoSdkArgumentException("No SDK base directory was provided");
			
			File sdkBaseDir = new File(args[0]);
			args = ArrayUtils.subarray(args, 1, args.length);
			
			if (!sdkBaseDir.exists() || !sdkBaseDir.isDirectory()) throw new InvalidDirectoryException("'" + sdkBaseDir.getPath() + "' is not a directory");
			sdkBaseDir = sdkBaseDir.getCanonicalFile();
			
			args = processGlobalCommandFlags(args);
			BRJS brjs = BRJSAccessor.initialize(new BRJS(sdkBaseDir, new ConsoleLoggerConfigurator(getRootLogger())));
			
			if (!brjs.dirExists()) throw new InvalidSdkDirectoryException("'" + sdkBaseDir.getPath() + "' is not a valid SDK directory");
			
			brjs.populate();
			
			injectLegacyCommands(brjs);
			brjs.runUserCommand(new CommandConsoleLogLevelAccessor(getRootLogger()), args);
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String[] processGlobalCommandFlags(String[] args) {
		if (args.length > 0) {
			String lastArg = args[args.length - 1];
			
			if(lastArg.equals("--quiet") || lastArg.equals("--verbose") || lastArg.equals("--debug")) {
				args = ArrayUtils.subarray(args, 0, args.length - 1);
				setExplicitLogLevel(lastArg);
			}
			else {
				setDefaultLogLevel();
			}
		}
		else {
			setDefaultLogLevel();
		}
		
		return args;
	}
	
	private void setExplicitLogLevel(String levelFlag) {
		RootConsoleLogger rootLogger = getRootLogger();
		LogLevel logLevel = (levelFlag.equals("--quiet")) ? LogLevel.WARN : LogLevel.DEBUG;
		rootLogger.setLogLevel(logLevel);
		
		if(levelFlag.equals("--debug")) {
			rootLogger.setDebugMode(true);
		}
	}
	
	private void setDefaultLogLevel() {
		LogConfiguration logConfigurator = new ConsoleLoggerConfigurator(getRootLogger());
		logConfigurator.ammendProfile(LogLevel.INFO)
			.pkg("brjs.core").logsAt(LogLevel.WARN)
			.pkg("org.hibernate").logsAt(LogLevel.WARN); // TODO: this is a plugin concern, so should be handled within the model
		logConfigurator.setLogLevel(LogLevel.INFO);
	}
	
	private void injectLegacyCommands(BRJS brjs) {
		try {
			CommandList commandList = brjs.commandList();
			commandList.addCommand(new DependencyAnalyserCommand(brjs));
			commandList.addCommand(new CheckCommand());
			commandList.addCommand(new CopyBladesetCommand( brjs.root().dir() ));
			commandList.addCommand(new ImportApplicationCommand( brjs ));
			commandList.addCommand(new TestCommand());
			commandList.addCommand(new TestServerCommand());
			commandList.addCommand(new WarCommand(brjs));
			commandList.addCommand(new PackageDepsCommand());
			commandList.addCommand(new TestIntegrationCommand( brjs.root().dir() ));
			commandList.addCommand(new ExportApplicationCommand( brjs ));
		}
		catch(ConfigException e) {
			throw new RuntimeException(e);
		}
	}
	
	private RootConsoleLogger getRootLogger() {
		return StaticLoggerBinder.getSingleton().getLoggerFactory().getRootLogger();
	}
	
	class NoSdkArgumentException extends CommandOperationException {
		private static final long serialVersionUID = 1L;
		
		public NoSdkArgumentException(String msg) {
			super(msg);
		}
	}
	
	class InvalidDirectoryException extends CommandOperationException {
		private static final long serialVersionUID = 1L;
		
		public InvalidDirectoryException(String msg) {
			super(msg);
		}
	}
	
	class InvalidSdkDirectoryException extends CommandOperationException {
		private static final long serialVersionUID = 1L;
		
		public InvalidSdkDirectoryException(String msg) {
			super(msg);
		}
	}
}
