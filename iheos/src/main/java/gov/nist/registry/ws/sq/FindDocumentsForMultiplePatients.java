package gov.nist.registry.ws.sq;

import gov.nist.registry.common2.exception.MetadataException;
import gov.nist.registry.common2.exception.MetadataValidationException;
import gov.nist.registry.common2.exception.XDSRegistryOutOfResourcesException;
import gov.nist.registry.common2.exception.XdsException;
import gov.nist.registry.common2.exception.XdsInternalException;
import gov.nist.registry.common2.registry.Metadata;
import gov.nist.registry.common2.registry.MetadataSupport;
import gov.nist.registry.common2.registry.SQCodedTerm;
import gov.nist.registry.common2.registry.storedquery.StoredQuerySupport;

import java.util.List;

import org.openhealthtools.openexchange.syslog.LoggerException;


/**
Generic implementation of FindDocuments Stored Query. This class knows how to parse a 
 * FindDocuments Stored Query request producing a collection of instance variables describing
 * the request.  A sub-class must provide the runImplementation() method that uses the pre-parsed
 * information about the stored query and queries a metadata database.
 * @author bill
 *
 */
abstract public class FindDocumentsForMultiplePatients extends StoredQuery {

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
	public FindDocumentsForMultiplePatients(StoredQuerySupport sqs) throws MetadataValidationException {
		super(sqs);
	}

	/**
	 * Implementation of Stored Query specific logic including parsing and validating parameters.
	 * @throws XdsInternalException
	 * @throws XdsException
	 * @throws LoggerException
	 * @throws XDSRegistryOutOfResourcesException
	 */
	public Metadata runSpecific() throws XdsInternalException, XdsException, LoggerException, XDSRegistryOutOfResourcesException {

		validateParameters();

		parseParameters();

		if (sqs.return_leaf_class == true) {


			// since the Public Registry gets some crazy requests, first do an ObjectRefs query to see how many 
			// results are planned.  If not out of order then do the real query for LeafClass

			sqs.return_leaf_class = false;

			Metadata m = runImplementation();
			if (m.getObjectRefs().size() > queryMaxReturn) 
				throw new XDSRegistryOutOfResourcesException("GetDocuments Stored Query for LeafClass is limited to "+ queryMaxReturn +" documents on this Registry. Your query targeted " + m.getObjectRefs().size() + " documents");


			sqs.return_leaf_class = true;
		}


		Metadata m = runImplementation();

		if (sqs.log_message != null)
			sqs.log_message.addOtherParam("Results structure", m.structure());

		return m;
	}


	void validateParameters() throws MetadataValidationException {
		//                         param name,                                 required?, multiple?, is string?,   is code?,       support AND/OR                          alternative
		sqs.validate_parm("$XDSDocumentEntryPatientId",                         false,      true,     true,         false,              false,                            (String[])null												);
		sqs.validate_parm("$XDSDocumentEntryClassCode",                         false,     true,      true,         true,               false,                          "$XDSDocumentEntryEventCodeList", "$XDSDocumentEntryHealthcareFacilityTypeCode"												);
		sqs.validate_parm("$XDSDocumentEntryPracticeSettingCode",               false,     true,      true,         true,               false,                           (String[])null												);
		sqs.validate_parm("$XDSDocumentEntryCreationTimeFrom",                  false,     false,     true,         false,              false,                            (String[])null												);
		sqs.validate_parm("$XDSDocumentEntryCreationTimeTo",                    false,     false,     true,         false,              false,                      (String[])null												);
		sqs.validate_parm("$XDSDocumentEntryServiceStartTimeFrom",              false,     false,     true,         false,              false,                      (String[])null												);
		sqs.validate_parm("$XDSDocumentEntryServiceStartTimeTo",                false,     false,     true,         false,              false,                      (String[])null												);
		sqs.validate_parm("$XDSDocumentEntryServiceStopTimeFrom",               false,     false,     true,         false,              false,                      (String[])null												);
		sqs.validate_parm("$XDSDocumentEntryServiceStopTimeTo",                 false,     false,     true,         false,              false,                      (String[])null												);
		sqs.validate_parm("$XDSDocumentEntryHealthcareFacilityTypeCode",        false,     true,      true,         true,               false,                     "$XDSDocumentEntryEventCodeList", "$XDSDocumentEntryClassCode"												);
		sqs.validate_parm("$XDSDocumentEntryEventCodeList",                     false,     true,      true,         true,               true,                           "$XDSDocumentEntryClassCode", "$XDSDocumentEntryHealthcareFacilityTypeCode"												);
		sqs.validate_parm("$XDSDocumentEntryConfidentialityCode",               false,     true,      true,         true,               true,                           (String[])null												);
		sqs.validate_parm("$XDSDocumentEntryFormatCode",                        false,     true,      true,         true,               false,                     (String[])null												);
		sqs.validate_parm("$XDSDocumentEntryStatus",                            true,      true,      true,         false,              false,                      (String[])null												);
		sqs.validate_parm("$XDSDocumentEntryAuthorPerson",                      false,     true,     true,         false,               false,                           (String[])null												);

		if (sqs.has_validation_errors) 
			throw new MetadataValidationException("Metadata Validation error present");

	}



	protected List<String>    patient_id;
	protected SQCodedTerm class_codes;
	protected SQCodedTerm type_codes;
	protected SQCodedTerm practice_setting_codes;
	protected String    creation_time_from;
	protected String    creation_time_to;
	protected String    service_start_time_from;
	protected String    service_start_time_to;
	protected String    service_stop_time_from;
	protected String    service_stop_time_to;
	protected SQCodedTerm hcft_codes;
	protected SQCodedTerm event_codes;
	protected SQCodedTerm conf_codes;
	protected SQCodedTerm format_codes;
	protected List<String> status;
	protected List<String> author_person;

	void parseParameters() throws XdsInternalException, XdsException, LoggerException {

		patient_id                        = sqs.params.getListParm   ("$XDSDocumentEntryPatientId");

		class_codes                       = sqs.params.getCodedParm("$XDSDocumentEntryClassCode");
		type_codes                       = sqs.params.getCodedParm("$XDSDocumentEntryTypeCode");
		practice_setting_codes            = sqs.params.getCodedParm("$XDSDocumentEntryPracticeSettingCode");
		creation_time_from                = sqs.params.getIntParm      ("$XDSDocumentEntryCreationTimeFrom");
		creation_time_to                  = sqs.params.getIntParm      ("$XDSDocumentEntryCreationTimeTo");
		service_start_time_from           = sqs.params.getIntParm      ("$XDSDocumentEntryServiceStartTimeFrom");
		service_start_time_to             = sqs.params.getIntParm      ("$XDSDocumentEntryServiceStartTimeTo");
		service_stop_time_from            = sqs.params.getIntParm      ("$XDSDocumentEntryServiceStopTimeFrom");
		service_stop_time_to              = sqs.params.getIntParm      ("$XDSDocumentEntryServiceStopTimeTo");
		hcft_codes                        = sqs.params.getCodedParm("$XDSDocumentEntryHealthcareFacilityTypeCode");
		event_codes                       = sqs.params.getCodedParm("$XDSDocumentEntryEventCodeList");
		conf_codes                        = sqs.params.getCodedParm("$XDSDocumentEntryConfidentialityCode");
		format_codes                      = sqs.params.getCodedParm("$XDSDocumentEntryFormatCode");
		status                            = sqs.params.getListParm("$XDSDocumentEntryStatus");
		author_person                     = sqs.params.getListParm("$XDSDocumentEntryAuthorPerson");


		String status_ns_prefix = MetadataSupport.status_type_namespace;

//		ArrayList<String> new_status = new ArrayList<String>();
		for (int i=0; i<status.size(); i++) {
			String stat = (String) status.get(i);

			if ( ! stat.startsWith(status_ns_prefix)) 
				throw new MetadataValidationException("Status parameter must have namespace prefix " + status_ns_prefix + " found " + stat);
//			new_status.add(stat.replaceFirst(status_ns_prefix, ""));
		}
//		status = new_status;

	}



}
