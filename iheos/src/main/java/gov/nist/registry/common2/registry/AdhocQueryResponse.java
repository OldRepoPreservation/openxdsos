package gov.nist.registry.common2.registry;

import gov.nist.registry.common2.exception.XdsInternalException;
import gov.nist.registry.common2.xml.Util;

import java.util.List;

import org.apache.axiom.om.OMElement;

public class AdhocQueryResponse extends Response {
	OMElement queryResult = null;

	public AdhocQueryResponse(short version, RegistryErrorList rel)  throws XdsInternalException {
		super(version, rel);

		init(version);
	}

	public AdhocQueryResponse(short version) throws XdsInternalException {
		super(version);

		init(version);
	}
	
	public OMElement getRoot() { return queryResult; }

	private void init(short version) {
		if (version == version_2) {
			response = MetadataSupport.om_factory.createOMElement("RegistryResponse", ebRSns);
			OMElement ahqr = MetadataSupport.om_factory.createOMElement("AdhocQueryResponse", ebQns);
			response.addChild(ahqr);
			OMElement sqr = null;
			sqr = MetadataSupport.om_factory.createOMElement("SQLQueryResult", ebQns);
			queryResult = sqr;
			ahqr.addChild(sqr);
		} else {
			response = MetadataSupport.om_factory.createOMElement("AdhocQueryResponse", ebQns);
		}
	}

	// called to get parent element of query results

	public OMElement getQueryResult() { 
		if (queryResult != null)
			return queryResult;


		if (version == version_2) {
			OMElement adhocQueryResponse = MetadataSupport.om_factory.createOMElement("AdhocQueryResponse", ebQns);
			response.addChild(adhocQueryResponse);
			queryResult = MetadataSupport.om_factory.createOMElement("SQLQueryResult", ebQns);
			adhocQueryResponse.addChild(queryResult);
		} else {  // add RegistryObjectList
			queryResult = MetadataSupport.om_factory.createOMElement("RegistryObjectList", ebRIMns);
			//response.addChild(queryResult);
		}
		return queryResult;
	}

	public void addQueryResults(OMElement metadata)  throws XdsInternalException {
		OMElement res = getQueryResult();  // used for side effect if v3 and error - must
		// still have empty RegistryObjectList after RegistryErrorList
		if (metadata != null)
			res.addChild(Util.deep_copy(metadata));
	}

	public void addQueryResults(List<OMElement> metadatas)  throws XdsInternalException {
		OMElement res = getQueryResult();  // used for side effect if v3 and error - must
		// still have empty RegistryObjectList after RegistryErrorList
		if (metadatas != null)
			for (int i=0; i<metadatas.size(); i++) {
				res.addChild(Util.deep_copy((OMElement) metadatas.get(i)));
			}
	}


}
