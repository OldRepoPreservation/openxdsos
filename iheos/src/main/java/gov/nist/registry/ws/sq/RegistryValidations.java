package gov.nist.registry.ws.sq;

import gov.nist.registry.common2.exception.MetadataValidationException;
import gov.nist.registry.common2.exception.XMLParserException;
import gov.nist.registry.common2.exception.XdsException;
import gov.nist.registry.common2.registry.Metadata;

import java.util.List;

import org.openhealthtools.openxds.log.LoggerException;

/**
 * Local queries needed to be supported for the internal operation of the registry.
 * @author bill
 *
 */
public interface RegistryValidations {

	/**
	 * Verify that the offered uuids exist in the Registry and have status Approved.
	 * @param uuids
	 * @return list of uuids that do not exist or are not Approved. Returns null if everything is ok
	 * @throws XdsException
	 * @throws LoggerException
	 */
	public List<String> validateApproved(List<String> uuids)  throws  XdsException, LoggerException;
	
	/**
	 * Validate uids found in metadata are proper. Uids of Folder and Submission Set may not
	 * be already present in registry.  Uid of DocumentEntry objects may be present if hash and
	 * size match. 
	 * @param metadata
	 * @throws MetadataValidationException - on all metadata errors
	 * @throws LoggerException - on error writing to Test Log
	 * @throws XdsException - on low level interface errors
	 * @throws XMLParserException 
	 */
	public void validateProperUids(Metadata metadata)  throws LoggerException, XMLParserException, XdsException;

}
