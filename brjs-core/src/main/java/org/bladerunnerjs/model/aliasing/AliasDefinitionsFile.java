package org.bladerunnerjs.model.aliasing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;
import org.bladerunnerjs.model.utility.FileModifiedChecker;
import org.bladerunnerjs.model.utility.XmlStreamReader;
import org.bladerunnerjs.model.utility.XmlStreamReaderFactory;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;

import com.ctc.wstx.msv.RelaxNGSchemaFactory;

public class AliasDefinitionsFile extends File {
	private static final long serialVersionUID = 822434477840572747L;
	private final FileModifiedChecker fileModifiedChecker;
	private List<AliasDefinition> aliasDefinitions;
	private XMLValidationSchema aliasDefinitionsSchema;
	
	{
		XMLValidationSchemaFactory schemaFactory = new RelaxNGSchemaFactory();
		
		try
		{
			aliasDefinitionsSchema = schemaFactory.createSchema(SchemaConverter.convertToRng("org/bladerunnerjs/model/aliasing/aliasDefinitions.rnc"));
		}
		catch (XMLStreamException | SchemaCreationException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	public AliasDefinitionsFile(File parent, String child) {
		super(parent, child);
		fileModifiedChecker = new FileModifiedChecker(this);
	}
	
	public AliasDefinition getAlias(AliasName aliasName, String scenarioName, List<String> groupNames) throws BundlerFileProcessingException {
		AliasDefinition aliasDefinition = null;
		
		for(AliasDefinition nextAliasDefinition : aliasDefinitions()) {
			String groupName = nextAliasDefinition.getGroup();
			
			if(nextAliasDefinition.getScenario().equals(scenarioName) && ((groupName == null) || groupNames.contains(groupName)) && nextAliasDefinition.getName().equals(aliasName.getName())) {
				aliasDefinition = nextAliasDefinition;
				break;
			}
		}
		
		return aliasDefinition;
	}
	
	public List<AliasDefinition> aliasDefinitions() throws BundlerFileProcessingException {
		if(fileModifiedChecker.fileModifiedSinceLastCheck()) {
			reparseFile();
		}
		
		return aliasDefinitions;
	}
	
	private void reparseFile() throws BundlerFileProcessingException {
		aliasDefinitions = new ArrayList<>();
		
		if(exists()) {
			try(XmlStreamReader streamReader = XmlStreamReaderFactory.createReader(this, aliasDefinitionsSchema)) {
				// TODO: bring more aliasing code over from the 'bundlers' project
			}
			catch (XMLStreamException e) {
				Location location = e.getLocation();
				
				throw new BundlerFileProcessingException(this, location.getLineNumber(), location.getColumnNumber(), e.getMessage());
			}
			catch (FileNotFoundException e) {
				throw new BundlerFileProcessingException(this, e);
			}
		}
	}
}
