package gov.nist.registry.ws;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerConfigurationException;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.context.MessageContext;
import org.openhealthtools.openexchange.audit.AuditCodeMappings.AuditTypeCodes;
import org.openhealthtools.openexchange.config.PropertyFacade;
import org.openhealthtools.openexchange.syslog.LogMessage;
import org.openhealthtools.openexchange.syslog.LoggerException;

import gov.nist.registry.common2.MetadataTypes;
import gov.nist.registry.common2.exception.MetadataValidationException;
import gov.nist.registry.common2.exception.SchemaValidationException;
import gov.nist.registry.common2.exception.XDSRegistryOutOfResourcesException;
import gov.nist.registry.common2.exception.XdsDeprecatedException;
import gov.nist.registry.common2.exception.XdsException;
import gov.nist.registry.common2.exception.XdsInternalException;
import gov.nist.registry.common2.exception.XdsPatientIdDoesNotMatchException;
import gov.nist.registry.common2.registry.IdParser;
import gov.nist.registry.common2.registry.Metadata;
import gov.nist.registry.common2.registry.MetadataSupport;
import gov.nist.registry.common2.registry.RegistryUtility;
import gov.nist.registry.common2.registry.storedquery.StoredQuerySupport;
import gov.nist.registry.common2.registry.validation.Validator;
import gov.nist.registry.ws.config.Registry;
import gov.nist.registry.ws.sq.RegistryObjectValidator;
import gov.nist.registry.ws.sq.RegistryValidations;
import gov.nist.registry.ws.sq.SQFactory;

public class SubmitObjectsRequestForUpdate extends SubmitObjectsRequest
{
	private enum UpdateMetadataType {
			UPDATE_DOCUMENTENTRY_METADATA,
			UPDATE_DOCUMENTENTRY_AVAILABILITY_STATUS,
			UPDATE_FOLDER_METADATA,
			UPDATE_FOLDER_AVAILABILITY_STATUS,
			UPDATE_ASSOCIATION_AVAILABILITY_STATUS,
			SUBMIT_ASSOCIATIONS
	};
	
	public SubmitObjectsRequestForUpdate() {
		super();
		setIsUpdate(true);
	}

	public SubmitObjectsRequestForUpdate(LogMessage log_message, short xds_version, MessageContext messageContext) {
		super(log_message, xds_version, messageContext);
		setIsUpdate(true);
	}
	

	void submitObjectsRequestInternal(OMElement sor)
			throws SQLException, SchemaValidationException, MetadataValidationException, XdsInternalException,
			TransformerConfigurationException, LoggerException, XdsException {

		// String sor_string = sor.toString();

		if (submit_raw) {
			submit_to_backend_registry(sor.toString());
			return;
		}

		if (xds_version == xds_b) {
			RegistryUtility.schema_validate_local(sor, MetadataTypes.METADATA_TYPE_Rb);
		} else {
			RegistryUtility.schema_validate_local(sor, MetadataTypes.METADATA_TYPE_R);
		}
		
		// try {
		Metadata m = new Metadata(sor, true);

		StoredQuerySupport sqs = new StoredQuerySupport(response, log_message);
		RegistryObjectValidator rov = Registry.getRegistryObjectValidator(sqs);

		generateAuditLog(m);

		logIds(m);

		Validator val = new Validator(m, response.registryErrorList, true, xds_version == xds_b, log_message, false,
				actor.getActorDescription());
		val.run();

		RegistryValidations vals = null;
		if (PropertyFacade.getBoolean("validate.metadata")) {
			vals = Registry.getRegistryValidations(response, log_message);
		}
		
		if (vals != null) {
			vals.validateProperUids(m);
		}
		
		if (response.has_errors()) {
			logger.error("metadata validator failed");
		}
		
		if (response.has_errors()) {
			return;
		}

		if (this.validater != null && !this.validater.runContentValidationService(m, response)) {
			return;
		}
		
		String patient_id = m.getSubmissionSetPatientId();
		if (log_message != null)
			log_message.addOtherParam("Patient ID", patient_id);

		validate_patient_id(patient_id);

		if (vals != null) {
			validateSourceId(m);
		}
		
		// check for references to registry contents
		List<String> referenced_objects = m.getIdsOfReferencedObjects();
		if (referenced_objects.size() > 0 && vals != null) {
			List<String> missing = vals.validateApproved(referenced_objects);
			if (missing != null)
				throw new XdsDeprecatedException(
						"The following registry objects were referenced by this submission but are not present, as Approved documents, in the registry: "
								+ missing);

			// make allowance for by reference inclusion
			missing = rov.validateSamePatientId(m.getReferencedObjectsThatMustHaveSamePatientId(), patient_id);
			if (missing != null)
				throw new XdsPatientIdDoesNotMatchException(
						"The following registry objects were referenced by this submission but do not reference the same patient ID: "
								+ missing);
		}

		// Get SSUID before UUID allocation
		String ssUid = m.getSubmissionSetUniqueId();

		// allocate uuids for symbolic ids
		IdParser ra = new IdParser(m);
		ra.compileSymbolicNamesIntoUuids();

		// check that submission does not include any object ids that are
		// already in registry
		List<String> ids_in_submission = m.getAllDefinedIds();
		List<String> ids_already_in_registry = rov.validateNotExists(ids_in_submission);
		if (ids_already_in_registry.size() != 0)
			response.add_error(MetadataSupport.XDSRegistryMetadataError,
					"The following UUIDs which are present in the submission are already present in registry: "
							+ ids_already_in_registry,
					"SubmitObjectsRequest.java", log_message);

		auditLog(patient_id, ssUid, AuditTypeCodes.RegisterDocumentSet_b);

		UpdateMetadataType updateType = identifyUpdateMetadataUseCase(m);
		
		if (updateType == UpdateMetadataType.UPDATE_ASSOCIATION_AVAILABILITY_STATUS) {
			processUpdateAssociationAvailabilityStatus(sor, m, rov, ra);
		} else if (updateType == UpdateMetadataType.UPDATE_DOCUMENTENTRY_METADATA) {
			processUpdateDocumentEntryMetadata(sor, m, rov, ra, patient_id, ssUid);
		} else {
			throw new MetadataValidationException("Transaction trigger not supported yet: " +
					updateType);
		}
		
		log_response();
	}

	private void processUpdateDocumentEntryMetadata(OMElement sor, Metadata m, RegistryObjectValidator rov,
			IdParser ra, String patient_id, String ssUid) throws LoggerException, XdsException {
		
		OMElement assoc = getHasMemberWithPreviousVersionSlotAssociation(m);
		if (assoc == null) {
			logger.info("Update document entry metadata request is missing PreviousVersion slot.");
			throw new MetadataValidationException("Update document entry metadata update is missing the PreviousVersion slot.");
		}

		// We must first deprecate the previous version of the document referenced by the lid UUID
		//
		String lid = m.getExtrinsicObjectLid();
		if (lid == null) {
			logger.info("Update document entry metadata request is missing logicalId attribute.");
			throw new MetadataValidationException("Update document entry metadata request is missing logicalId attribute.");
		}
		
		// Deprecate all the existing documents with the same lid
		List<String> deprecatableObjectIds = new ArrayList<String>();
		List<String> docIds = new ArrayList<String>();
		try {
			Metadata me = new SQFactory(this).getDocuments(lid, false);
			docIds = me.getObjectIds(me.getObjectRefs());
		} catch (XDSRegistryOutOfResourcesException e) {
			throw new XdsException("Failed while retrieving documents using logical ID: " +
					e.getMessage(), e);
		}
		deprecatableObjectIds.addAll(docIds);
		
		// validate that these are documents first
		List<String> missing = rov.validateDocuments(deprecatableObjectIds);
		if (missing != null) {
			throw new XdsException("The following documents were referenced by this submission but are not present"
					+ " in the registry: " + missing);
		}

		if (deprecatableObjectIds.size() > 0) {
			OMElement deprecate = ra.getDeprecateObjectsRequest(deprecatableObjectIds);
			if (log_message != null) {
				log_message.addOtherParam("Deprecate", deprecate.toString());
			}
			submit_to_backend_registry(deprecate.toString());
		}
		
		addUpdatedDocToFolder(m, docIds);
		
		// Now we can register the new document
		//
		// submit to backend registry
		String to_backend = m.getV3SubmitObjectsRequest().toString();
		if (log_message != null)
			log_message.addOtherParam("From Registry Adaptor", to_backend);

		boolean status = submit_to_backend_registry(to_backend);
		if (!status) {
			return;
		}

		auditLog(patient_id, ssUid, AuditTypeCodes.RegisterDocumentSet_b);

		// Approve
		List<String> approvable_object_ids = ra.approvable_object_ids(m);

		if (approvable_object_ids.size() > 0) {

			OMElement approve = ra.getApproveObjectsRequest(approvable_object_ids);
			if (log_message != null)
				log_message.addOtherParam("Approve", approve.toString());

			submit_to_backend_registry(approve.toString());
		}
		log_response();		
	}

	private void processUpdateAssociationAvailabilityStatus(OMElement sor, Metadata m, RegistryObjectValidator rov, IdParser ra)
			throws LoggerException, XdsException {
		OMElement assoc = getUpdateAvailabilityStatusAssociation(m);
		String uuid = m.getAssocTarget(assoc);
		
		// Deprecate
		List<String> deprecatableObjectIds = new ArrayList<String>();
		deprecatableObjectIds.add(uuid);
		
			// validate that these are documents first
		List<String> missing = rov.validateDocuments(deprecatableObjectIds);
		if (missing != null) {
			throw new XdsException("The following documents were referenced by this submission but are not present"
					+ " in the registry: " + missing);
		}

		OMElement deprecate = ra.getDeprecateObjectsRequest(deprecatableObjectIds);
		if (log_message != null) {
			log_message.addOtherParam("Deprecate", deprecate.toString());
		}
		submit_to_backend_registry(deprecate.toString());
	}

	private OMElement getHasMemberWithPreviousVersionSlotAssociation(Metadata m) {
		List<OMElement> assocs = m.getAssociations();
		for (OMElement assoc : assocs) {
			String associationType = assoc.getAttributeValue(MetadataSupport.association_type_qname); 
			if (!associationType.equals(MetadataSupport.HAS_MEMBER_ASSOCIATION_TYPE) ||
					!m.hasSlot(assoc, "PreviousVersion")) {
				continue;
			}
			return assoc;
		}
		return null;
	}
	/*
	<rim:Association id="as-hasmember" associationType="urn:oasis:names:tc:ebxml-regrep:AssociationType:HasMember" sourceObject="SubmissionSet" targetObject="urn:uuid:e589ec36-f24c-4b9f-990c-a578d83794cb">
		<rim:Slot name="SubmissionSetStatus">
			<rim:ValueList>
				<rim:Value>Original</rim:Value>
			</rim:ValueList>
		</rim:Slot>
		<rim:Slot name="PreviousVersion">
			<rim:ValueList>
				<rim:Value>1</rim:Value>
			</rim:ValueList>
		</rim:Slot>
	</rim:Association>
	 */
	private OMElement getUpdateAvailabilityStatusAssociation(Metadata m) {
		List<OMElement> assocs = m.getAssociations();
		for (OMElement assoc : assocs) {
			String associationType = assoc.getAttributeValue(MetadataSupport.association_type_qname); 
			if (associationType.equals(MetadataSupport.UPDATE_AVAILABILITY_ASSOCIATION_TYPE)) {
				return assoc;
			}
		}
		return null;
	}

	private UpdateMetadataType identifyUpdateMetadataUseCase(Metadata m) {
		List<OMElement> assocs = m.getAssociations();
		for (OMElement assoc : assocs) {
			String associationType = assoc.getAttributeValue(MetadataSupport.association_type_qname); 
			if (associationType.equals(MetadataSupport.UPDATE_AVAILABILITY_ASSOCIATION_TYPE)) {
				return UpdateMetadataType.UPDATE_ASSOCIATION_AVAILABILITY_STATUS;
			}
			if (associationType.equals(MetadataSupport.HAS_MEMBER_ASSOCIATION_TYPE) &&
					hasLogicalIdAttribute(m)) {
				return UpdateMetadataType.UPDATE_DOCUMENTENTRY_METADATA;
			}
		}
		return UpdateMetadataType.UPDATE_ASSOCIATION_AVAILABILITY_STATUS;
	}

	private boolean hasLogicalIdAttribute(Metadata m) {
		return (getLogicalIdAttributeValue(m) != null) ? true : false;
	}
	
	private String getLogicalIdAttributeValue(Metadata m) {
		List<OMElement> eos = m.getExtrinsicObjects();
		for (OMElement elem : eos) {
			OMAttribute attrib = elem.getAttribute(new QName("lid"));
			if (attrib != null) {
				return attrib.getAttributeValue();
			}
		}
		return null;
	}
}
