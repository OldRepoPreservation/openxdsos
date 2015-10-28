package gov.nist.registry.ws.sq.ebxmlrr21;

import org.openhealthtools.openexchange.syslog.LoggerException;

import gov.nist.registry.common2.exception.MetadataException;
import gov.nist.registry.common2.exception.MetadataValidationException;
import gov.nist.registry.common2.exception.XdsException;
import gov.nist.registry.common2.registry.Metadata;
import gov.nist.registry.common2.registry.MetadataParser;
import gov.nist.registry.common2.registry.storedquery.StoredQuerySupport;
import gov.nist.registry.ws.sq.GetDocumentsByLogicalId;



/**
 * Implementation specific class for FindDocuments stored query. 
 * All the logic is in the runImplementation() method.
 * @author bill
 *
 */
public class EbXML21GetDocumentsByLogicalId extends GetDocumentsByLogicalId {

	EbXML21QuerySupport eb;

	/**
	 * Constructor
	 * @param sqs
	 * @throws MetadataValidationException
	 */
	public EbXML21GetDocumentsByLogicalId(StoredQuerySupport sqs) {
		super(sqs);
		eb = new EbXML21QuerySupport(sqs);
	}

	/**
	 * Main method, runs query logic
	 * @return Metadata
	 * @throws MetadataException
	 * @throws XdsException
	 */
	public Metadata runImplementation() throws MetadataException,
	XdsException, LoggerException {
		
		return MetadataParser.parseNonSubmission(eb.getDocByLogicalId(logicalId));

	}

}
