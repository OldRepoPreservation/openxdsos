package org.openhealthtools.openxds.registry.patient;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;
import org.openhealthtools.openexchange.datamodel.Patient;
import org.openhealthtools.openexchange.datamodel.PatientIdentifier;
import org.openhealthtools.openxds.registry.AuthenticationRequest;
import org.openhealthtools.openxds.registry.api.RegistryPatientContext;
import org.openhealthtools.openxds.registry.api.RegistryPatientException;
import org.openhealthtools.openxds.registry.api.XdsRegistryPatientService;
import org.openhie.openempi.model.IdentifierDomain;
import org.openhie.openempi.model.Person;
import org.openhie.openempi.model.PersonIdentifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;

@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class XdsPatientRegistryOpenEmpiImpl implements XdsRegistryPatientService
{
	private static final String OPENEMPI_SESSION_KEY_HEADER = "OPENEMPI_SESSION_KEY";
	
	private Client client;
	private WebResource resource;
	private String sessionKey;
	private Logger log = Logger.getLogger(getClass());
	private String patientRegistryBaseUri;
	private URI patientRegistryUri;
	private boolean logRequests;
	
	@Override
	public boolean isValidPatient(PatientIdentifier pid,
			RegistryPatientContext context) throws RegistryPatientException {
		String key = getSessionKey();
		try {
			PersonIdentifier identifier = getPersonIdentifier(pid);
			client = Client.create();
			if (logRequests) {
				client.addFilter(new LoggingFilter());
			}
			Person person = client.resource(getBaseURI(null))
				.path("person-query-resource")
        		.path("findPersonById")
        		.header(OPENEMPI_SESSION_KEY_HEADER, key)
        		.accept(MediaType.APPLICATION_XML)
        		.post(Person.class, identifier);
			if (person == null) {
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Failed while trying to determine if the patient with the given identifier is known." + e, e);
			throw new RegistryPatientException(e.getMessage());
		}
	}

	private PersonIdentifier getPersonIdentifier(PatientIdentifier pid) {
		PersonIdentifier id = new PersonIdentifier();
		id.setIdentifier(pid.getId());
		IdentifierDomain domain = new IdentifierDomain();
		domain.setNamespaceIdentifier(pid.getAssigningAuthority().getNamespaceId());
		domain.setUniversalIdentifier(pid.getAssigningAuthority().getUniversalId());
		domain.setUniversalIdentifierTypeCode(pid.getAssigningAuthority().getUniversalIdType());
		id.setIdentifierDomain(domain);
		return id;
	}

	@Override
	public void createPatient(Patient patient, RegistryPatientContext context)
			throws RegistryPatientException {
		throw new RegistryPatientException("Method not implemented.");
	}

	@Override
	public void updatePatient(Patient patient, RegistryPatientContext context)
			throws RegistryPatientException {
		throw new RegistryPatientException("Method not implemented.");
	}

	@Override
	public void mergePatients(Patient survivingPatient, Patient mergePatient,
			RegistryPatientContext context) throws RegistryPatientException {
		throw new RegistryPatientException("Method not implemented.");
	}

	@Override
	public void unmergePatients(Patient survivingPatient, Patient mergePatient,
			RegistryPatientContext context) throws RegistryPatientException {
		throw new RegistryPatientException("Method not implemented.");
	}

	private String getSessionKey() {
		client = Client.create();
		if (sessionKey == null) {
			sessionKey = getNewSessionKey();
		} else {
			resource = client.resource(getBaseURI(null));
			String isSessionAlive = resource.path("security-resource")
				.path(sessionKey)
				.header(OPENEMPI_SESSION_KEY_HEADER, sessionKey)
				.get(String.class);
			if (!isSessionAlive.equalsIgnoreCase("true")) {
				sessionKey = getNewSessionKey();
			}
		}
		if (log.isDebugEnabled()) {
			log.debug("Obtained session key " + sessionKey);
		}
		return sessionKey;
	}

	private String getNewSessionKey() {
		AuthenticationRequest request = new AuthenticationRequest("admin", "admin");
		String newKey = client.resource(getBaseURI(null))
			.path("security-resource")
			.path("authenticate")
			.accept(MediaType.APPLICATION_XML)
			.accept(MediaType.APPLICATION_JSON)
			.put(String.class, request);
		return newKey;
	}
	
    private URI getBaseURI(String resourceName) {
    	if (patientRegistryUri == null) {
	    	String uri = "http://localhost:8080/openempi-admin/openempi-ws-rest/";
	    	if (resourceName != null && resourceName.length() > 0) {
	    		uri = uri + resourceName;
	    	}
	        patientRegistryUri = UriBuilder.fromUri(uri).port(8080).build();
    	}
    	return patientRegistryUri;
    }

	public boolean isLogRequests() {
		return logRequests;
	}

	public void setLogRequests(boolean logRequests) {
		this.logRequests = logRequests;
	}

	public String getPatientRegistryBaseUri() {
		return patientRegistryBaseUri;
	}

	public void setPatientRegistryBaseUri(String patientRegistryBaseUri) {
		this.patientRegistryBaseUri = patientRegistryBaseUri;
	}	
}
