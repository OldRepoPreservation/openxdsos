package gov.nist.registry.ws.serviceclasses;

import gov.nist.registry.common2.exception.MetadataException;
import gov.nist.registry.common2.exception.MetadataValidationException;
import gov.nist.registry.common2.exception.XdsException;
import gov.nist.registry.common2.exception.XdsInternalException;
import gov.nist.registry.common2.exception.XdsValidationException;
import gov.nist.registry.common2.exception.XdsWSException;
import gov.nist.registry.common2.registry.Metadata;
import gov.nist.registry.common2.registry.MetadataSupport;
import gov.nist.registry.common2.registry.Response;
import gov.nist.registry.common2.registry.XdsCommon;
import gov.nist.registry.common2.service.AppendixV;
import gov.nist.registry.ws.AdhocQueryRequest;
import gov.nist.registry.ws.ContentValidationService;
import gov.nist.registry.ws.RemoveObjectsRequest;
import gov.nist.registry.ws.SubmitObjectsRequest;
import gov.nist.registry.ws.SubmitObjectsRequestForUpdate;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.openhealthtools.openexchange.syslog.LoggerException;

public abstract class AbstractRegistry extends XdsService implements ContentValidationService {

	abstract protected void validateWS(boolean isSQ) throws XdsWSException;
	abstract protected short getXdsVersion(); 
	abstract protected void validateQueryTransaction(OMElement sor) throws XdsValidationException, MetadataValidationException, XdsInternalException;
	abstract protected void validateSubmitTransaction(OMElement sor) throws XdsValidationException;
	abstract protected void validateRemoveTransaction(OMElement sor) throws XdsValidationException;
	abstract public boolean runContentValidationService(Metadata m, Response response) throws MetadataException;
	abstract public String getServiceName();
	abstract protected void validateQueryInputDecoration(OMElement sor, AdhocQueryRequest a) throws XdsValidationException;
	abstract protected void decorateQueryOutput(OMElement sor, AdhocQueryRequest a, OMElement result) throws XdsValidationException ;

	protected String getRTransactionName(OMElement ahqr) {
		OMElement ahq = MetadataSupport.firstChildWithLocalName(ahqr, "AdhocQuery") ;
		OMElement sql = MetadataSupport.firstChildWithLocalName(ahqr, "SQLQuery") ;

		if (ahq != null)
			return "SQ";
		else if (sql != null)
			return "SQL";
		else if (ahqr.getLocalName().equals("SubmitObjectsRequest"))
			return "SubmitObjectsRequest";
		else if (ahqr.getLocalName().equals("RemoveObjectsRequest"))
			return "RemoveObjectsRequest";
		else
			return "Unknown";
	}
	
	protected boolean isSQ(OMElement ahqr) {
		return MetadataSupport.firstChildWithLocalName(ahqr, "AdhocQuery") != null;
	}

	protected boolean isSQL(OMElement ahqr) {
		return MetadataSupport.firstChildWithLocalName(ahqr, "SQLQuery") != null;
	}


	public OMElement DocumentRegistry_RegisterDocumentSet_b(OMElement sor) throws AxisFault {
		try {
			OMElement startup_error = beginTransaction(getRTransactionName(sor), sor, AppendixV.REGISTRY_ACTOR);
			if (startup_error != null)
				return startup_error;
			if(log_message != null)
				log_message.setTestMessage(getRTransactionName(sor));

			validateWS(false);

			validateSubmitTransaction(sor);

			SubmitObjectsRequest s = new SubmitObjectsRequest(log_message, getXdsVersion(), getMessageContext());
			s.setClientIPAddress(getClientIPAddress());
			s.setContentValidationService(this);
			OMElement result = s.submitObjectsRequest(sor);
			endTransaction(s.getStatus());
			return result;
		} catch (Exception e) {
			return endTransaction(sor, e, AppendixV.REGISTRY_ACTOR, "");
		}
	}

	public OMElement DocumentRegistry_UpdateDocumentSet(OMElement sor) throws AxisFault {
		try {
			OMElement startup_error = beginTransaction(getRTransactionName(sor), sor, AppendixV.REGISTRY_ACTOR);
			if (startup_error != null)
				return startup_error;
			if(log_message != null)
				log_message.setTestMessage(getRTransactionName(sor));

			validateWS(false);

			validateSubmitTransaction(sor);

			SubmitObjectsRequest s = new SubmitObjectsRequestForUpdate(log_message, getXdsVersion(), getMessageContext());
			s.setClientIPAddress(getClientIPAddress());
			s.setContentValidationService(this);
			OMElement result = s.submitObjectsRequest(sor);
			endTransaction(s.getStatus());
			return result;
		} catch (Exception e) {
			return endTransaction(sor, e, AppendixV.REGISTRY_ACTOR, "");
		}
	}
	
	public OMElement DocumentRegistry_DeleteDocumentSet(OMElement ror) throws AxisFault {
		try {
			OMElement startup_error = beginTransaction(getRTransactionName(ror), ror, AppendixV.REGISTRY_ACTOR);
			if (startup_error != null)
				return startup_error;
			if(log_message != null)
				log_message.setTestMessage(getRTransactionName(ror));

			validateWS(false);

			validateRemoveTransaction(ror);

			// TODO: Need to add support here for the implementation of the method
			RemoveObjectsRequest request = new RemoveObjectsRequest(log_message, getXdsVersion(), getMessageContext());
			request.setClientIPAddress(getClientIPAddress());
			request.setContentValidationService(this);
			OMElement result = request.removeObjectsRequest(ror);
			endTransaction(request.getStatus());
			return result;
		} catch (Exception e) {
			return endTransaction(ror, e, AppendixV.REGISTRY_ACTOR, "");
		}
	}
	
	public OMElement DocumentRegistry_RegistryStoredQuery(OMElement ahqr) throws AxisFault {
		OMElement startup_error = beginTransaction(getRTransactionName(ahqr), ahqr, AppendixV.REGISTRY_ACTOR);
		if (startup_error != null)
			return startup_error;
		if(log_message != null)
			log_message.setTestMessage(getRTransactionName(ahqr));

		String type = getRTransactionName(ahqr);

		AdhocQueryRequest a = new AdhocQueryRequest(log_message, getMessageContext(), isSecure(), XdsCommon.xds_b);

		a.setServiceName(service_name);
		
		try {
			
			mustBeSimpleSoap();

			validateWS(type.equals("SQ"));

			validateQueryTransaction(ahqr);

			validateQueryInputDecoration(ahqr, a);

			OMElement result = processAdhocQueryRequest(a, ahqr);
			
			decorateQueryOutput(ahqr, a, result);

			endTransaction(a.getStatus());

			return result;
		} catch (Exception e) {
			return endTransaction(ahqr, e, AppendixV.REGISTRY_ACTOR, "");
		}		
	}

	protected OMElement processAdhocQueryRequest(AdhocQueryRequest a, OMElement ahqr) throws AxisFault, XdsException, XdsValidationException, LoggerException {
		return a.adhocQueryRequest(ahqr);
	}	
}
