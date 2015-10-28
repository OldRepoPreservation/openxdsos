package gov.nist.registry.ws.sq;

import gov.nist.registry.common2.exception.MetadataException;
import gov.nist.registry.common2.exception.MetadataValidationException;
import gov.nist.registry.common2.exception.XDSRegistryOutOfResourcesException;
import gov.nist.registry.common2.exception.XdsException;
import gov.nist.registry.common2.exception.XdsInternalException;
import gov.nist.registry.common2.registry.Metadata;
import gov.nist.registry.common2.registry.storedquery.StoredQuerySupport;

import java.util.List;

import org.openhealthtools.openexchange.syslog.LoggerException;

/**
Generic implementation of GetDocuments Stored Query. This class knows how to parse a 
 * GetDocuments Stored Query request producing a collection of instance variables describing
 * the request.  A sub-class must provide the runImplementation() method that uses the pre-parsed
 * information about the stored query and queries a metadata database.
 * @author bill
 *
 */
abstract public class GetDocumentsByLogicalId extends StoredQuery {

	/**
	 * Method required in subclasses (implementation specific class) to define specific
	 * linkage to local database
	 * @return matching metadata
	 * @throws MetadataException
	 * @throws XdsException
	 * @throws LoggerException
	 */
	abstract protected Metadata runImplementation() throws MetadataException, XdsException, LoggerException;

	/**
	 * Basic constructor
	 * @param sqs
	 * @throws MetadataValidationException
	 */
	public GetDocumentsByLogicalId(StoredQuerySupport sqs) {
		super(sqs);
	}

	void validateParameters() throws MetadataValidationException {

		//                         param name,                         required?, multiple?, is string?,   same size as,    alternative
		sqs.validate_parm("$XDSDocumentEntryLogicalID",                true,      false,     true,         null,           "$XDSDocumentEntryLogicalID");

		if (sqs.has_validation_errors) 
			throw new MetadataValidationException("Metadata Validation error present");
	}

	protected String logicalId;
	
	void parseParameters() throws XdsInternalException, XdsException, LoggerException {
		logicalId = sqs.params.getStringParm("$XDSDocumentEntryLogicalID");
	}

	
	/**
	 * Implementation of Stored Query specific logic including parsing and validating parameters.
	 * @throws XdsInternalException
	 * @throws XdsException
	 * @throws LoggerException
	 * @throws XDSRegistryOutOfResourcesException
	 */
	public Metadata runSpecific() throws XdsException, LoggerException {

		validateParameters();
		parseParameters();
		
		if (logicalId == null)
			throw new XdsInternalException("GetDocumentsByLogicalId Stored Query: logicalId not found");

		return runImplementation();
	}


}
