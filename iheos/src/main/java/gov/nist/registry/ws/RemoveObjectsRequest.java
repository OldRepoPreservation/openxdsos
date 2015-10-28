package gov.nist.registry.ws;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerConfigurationException;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.context.MessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openhealthtools.common.utils.OMUtil;
import org.openhealthtools.openexchange.audit.IheAuditTrail;
import org.openhealthtools.openexchange.audit.AuditCodeMappings.AuditTypeCodes;
import org.openhealthtools.openexchange.config.PropertyFacade;
import org.openhealthtools.openexchange.syslog.LogMessage;
import org.openhealthtools.openexchange.syslog.LoggerException;
import org.openhealthtools.openxds.common.XdsFactory;
import org.openhealthtools.openxds.registry.api.RegistryLifeCycleContext;
import org.openhealthtools.openxds.registry.api.RegistryLifeCycleException;
import org.openhealthtools.openxds.registry.api.XdsRegistry;
import org.openhealthtools.openxds.registry.api.XdsRegistryLifeCycleService;

import gov.nist.registry.common2.MetadataTypes;
import gov.nist.registry.common2.datatypes.Hl7Date;
import gov.nist.registry.common2.exception.MetadataValidationException;
import gov.nist.registry.common2.exception.SchemaValidationException;
import gov.nist.registry.common2.exception.XdsDeprecatedException;
import gov.nist.registry.common2.exception.XdsException;
import gov.nist.registry.common2.exception.XdsFormatException;
import gov.nist.registry.common2.exception.XdsInternalException;
import gov.nist.registry.common2.exception.XdsNonIdenticalHashException;
import gov.nist.registry.common2.exception.XdsPatientIdDoesNotMatchException;
import gov.nist.registry.common2.exception.XdsUnknownPatientIdException;
import gov.nist.registry.common2.registry.IdParser;
import gov.nist.registry.common2.registry.Metadata;
import gov.nist.registry.common2.registry.MetadataSupport;
import gov.nist.registry.common2.registry.RegistryResponse;
import gov.nist.registry.common2.registry.RegistryUtility;
import gov.nist.registry.common2.registry.Response;
import gov.nist.registry.common2.registry.XdsCommon;
import gov.nist.registry.common2.registry.storedquery.StoredQuerySupport;
import gov.nist.registry.common2.registry.validation.Validator;
import gov.nist.registry.ws.config.Registry;
import gov.nist.registry.ws.sq.RegistryObjectValidator;
import gov.nist.registry.ws.sq.RegistryValidations;

public class RemoveObjectsRequest extends XdsCommon
{
	boolean submit_raw = false;
	ContentValidationService validater;
	short xds_version;
	protected final static Log logger = LogFactory.getLog(RemoveObjectsRequest.class);
	protected XdsRegistry actor = null;
	static ArrayList<String> sourceIds = null;
	String clientIPAddress;
	/* The IHE Audit Trail for this actor. */
	private IheAuditTrail auditLog = null;

	public RemoveObjectsRequest(LogMessage log_message, short xds_version, MessageContext messageContext) {
		this.log_message = log_message;
		this.messageContext = messageContext;
		this.xds_version = xds_version;
		this.clientIPAddress = null;
		transaction_type = R_transaction;

		try {
			actor = XdsFactory.getRegistryActor();
			if (actor == null) {
				throw new XdsInternalException("Cannot find XdsRegistry actor configuration.");
			}

			auditLog = (IheAuditTrail) actor.getAuditTrail();
			init(new RegistryResponse((xds_version == xds_a) ? Response.version_2 : Response.version_3), xds_version,
					messageContext);

		} catch (XdsInternalException e) {
			logger.fatal(logger_exception_details(e));
		}
	}
	
	public RemoveObjectsRequest() {
	}

	public OMElement removeObjectsRequest(OMElement ror) {

		if (logger.isDebugEnabled()) {
			logger.debug("Request from the Repository:");
			logger.debug(ror.toString());
		}
		try {
			ror.build();
			removeObjectsRequestInternal(ror);
		} catch (XdsFormatException e) {
			response.add_error("XDSRegistryError", "SOAP Format Error: " + e.getMessage(),
					RegistryUtility.exception_trace(e), log_message);
		} catch (XdsInternalException e) {
			response.add_error("XDSRegistryError", "XDS Internal Error:\n " + e.getMessage(),
					RegistryUtility.exception_trace(e), log_message);
			logger.fatal(RegistryUtility.exception_trace(e));
		} catch (MetadataValidationException e) {
			response.add_error("XDSRegistryMetadataError", "Metadata Validation Errors:\n " + e.getMessage(),
					RegistryUtility.exception_trace(e), log_message);
		} catch (LoggerException e) {
			response.add_error("XDSRegistryError", "Internal Logging error: LoggerException: " + e.getMessage(),
					RegistryUtility.exception_trace(e), log_message);
			logger.fatal(logger_exception_details(e));
		} catch (SchemaValidationException e) {
			response.add_error("XDSRegistryMetadataError", "Schema Validation Errors:\n" + e.getMessage(),
					RegistryUtility.exception_trace(e), log_message);
		} catch (XdsException e) {
			response.add_error("XDSRegistryError", "Exception:\n " + e.getMessage(), RegistryUtility.exception_trace(e),
					log_message);
			logger.warn(logger_exception_details(e));
		} catch (TransformerConfigurationException e) {
			response.add_error("XDSRegistryError", "Internal Error: Transformer Configuration Error: " + e.getMessage(),
					RegistryUtility.exception_trace(e), log_message);
			logger.fatal(logger_exception_details(e));
		} catch (SQLException e) {
			response.add_error("XDSRegistryError", "Internal Logging error: SQLException: " + e.getMessage(),
					RegistryUtility.exception_trace(e), log_message);
			logger.fatal(logger_exception_details(e));
		} catch (Exception e) {
			response.add_error("XDSRegistryError", "XDS General Error: " + e.getMessage(),
					RegistryUtility.exception_trace(e), log_message);
			logger.fatal(logger_exception_details(e));
		}

		log_response();

		OMElement res = null;
		try {
			res = response.getResponse();
			if (logger.isDebugEnabled()) {
				logger.debug("Response from the Registry");
				logger.debug(res.toString());
			}
		} catch (XdsInternalException e) {
			logger.error("Encountered an error while getting the response: " + e, e);
		}

		// Notify document submission to subscribers
		if (!response.has_errors()) {
			// todo: remove hardcoded url
			// EndpointReference endpoint = new
			// EndpointReference("http://localhost:8885/opendsub/services/NotificationBroker");
			// EndpointReference producerEndpoint = new
			// EndpointReference("http://localhost:8010/openxds/services/DocumentRegistry");
			// NotificationProducer producer = new
			// DocumentMetadataProducer(endpoint, );
			// LocalDsubPublisher publisher = new LocalDsubPublisher(endpoint);

			// Document Metadata Publish
			// Publisher.getInstance().publish(sor, actor);
		}

		if (logger.isInfoEnabled()) {
			logger.info("response is " + res.toString());
		}
		return res;
	}

	void removeObjectsRequestInternal(OMElement ror)
			throws SQLException, SchemaValidationException, MetadataValidationException, XdsInternalException,
			TransformerConfigurationException, LoggerException, MetadataValidationException, XdsException {
		boolean status;

		RegistryUtility.schema_validate_local(ror, MetadataTypes.METADATA_TYPE_Rb);
		

		StoredQuerySupport sqs = new StoredQuerySupport(response, log_message);
		RegistryObjectValidator rov = Registry.getRegistryObjectValidator(sqs);

//		generateAuditLog(m);

		RegistryValidations vals = null;
		if (PropertyFacade.getBoolean("validate.metadata")) {
			vals = Registry.getRegistryValidations(response, log_message);
		}
		
		if (vals != null) {
			//TODO: Need to validate the request;
//			vals.validateProperUids(m);
		}
		
		if (response.has_errors()) {
			logger.error("metadata validator failed");
		}
		
		if (response.has_errors()) {
			return;
		}

//		if (this.validater != null && !this.validater.runContentValidationService(m, response)) {
//			return;
//		}
				
		// check for references to registry contents
//		List<String> referenced_objects = m.getIdsOfReferencedObjects();
//		if (referenced_objects.size() > 0 && vals != null) {
//			List<String> missing = vals.validateApproved(referenced_objects);
//			if (missing != null)
//				throw new XdsDeprecatedException(
//						"The following registry objects were referenced by this submission but are not present, as Approved documents, in the registry: "
//								+ missing);
//		}
		
		// submit to backend registry
		String to_backend = ror.toString();
		if (log_message != null)
			log_message.addOtherParam("From Registry Adaptor", to_backend);

		status = submit_to_backend_registry(to_backend);
		if (!status) {
			return;
		}

		//TODO: Need to audi the request
//		auditLog(patient_id, ssUid, AuditTypeCodes.RegisterDocumentSet_b);

		log_response();
	}
	protected boolean submit_to_backend_registry(String ror_string) throws XdsInternalException {
		boolean status = true;

		XdsRegistryLifeCycleService lcm = XdsFactory.getXdsRegistryLifeCycleService();
		OMElement result = null;
		try {
			OMElement request = OMUtil.xmlStringToOM(ror_string);
			result = lcm.removeObjects(request, new RegistryLifeCycleContext());
		} catch (XMLStreamException e) {
			response.add_error("XDSRegistryError", e.getMessage(), RegistryUtility.exception_details(e), log_message);
			status = false;
		} catch (RegistryLifeCycleException e) {
			response.add_error("XDSRegistryError", e.getMessage(), RegistryUtility.exception_details(e), log_message);
			status = false;
		}

		if (!status) {
			return status;
		}

		String statusCode = result.getAttributeValue(MetadataSupport.status_qname);
		if (!statusCode.equals(MetadataSupport.response_status_type_namespace + "Success")) {
			OMElement errorList = MetadataSupport.firstChildWithLocalName(result, "RegistryErrorList");
			response.addRegistryErrorList(errorList, log_message);
			status = false;
		}

		return status;
	}
	
	public void setClientIPAddress(String addr) {
		clientIPAddress = addr;
	}

	public void setContentValidationService(ContentValidationService validater) {
		this.validater = validater;
	}
}
