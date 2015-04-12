package gov.nist.registry.common2.registry;

import gov.nist.registry.common2.exception.XdsInternalException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.jaxen.JaxenException;

public class RegistryResponseParser {
	OMElement response_element;

	public RegistryResponseParser(OMElement response_element) {
		this.response_element = response_element;
	}

	// also returns errorCode attributes
	public ArrayList<String> get_error_code_contexts() {
		ArrayList<String> result = new ArrayList<String>();
		
		OMElement registry_error_list = null;
		if (response_element.getLocalName().equals("RegistryErrorList"))
			registry_error_list = response_element;
		else 
			registry_error_list = MetadataSupport.firstChildWithLocalName(response_element, "RegistryErrorList") ;
		
		if (registry_error_list == null)
			return result;
		for (OMElement registry_error : MetadataSupport.childrenWithLocalName(registry_error_list, "RegistryError")) {
			String severity = get_att(registry_error, "severity");
			if (severity == null || !severity.endsWith("Error"))
				continue;
			String code_context = get_att(registry_error, "codeContext");
			if (code_context != null)
				result.add(code_context);
			
			String error_code = get_att(registry_error, "errorCode");
			if (error_code != null)
				result.add(error_code);
		}

		return result;
	}

	public ArrayList<String> get_error_codes() {
		ArrayList<String> result = new ArrayList<String>();
		
		OMElement registry_error_list = null;
		if (response_element.getLocalName().equals("RegistryErrorList"))
			registry_error_list = response_element;
		else 
			registry_error_list = MetadataSupport.firstChildWithLocalName(response_element, "RegistryErrorList") ;
		
		if (registry_error_list == null)
			return result;
		for (OMElement registry_error : MetadataSupport.childrenWithLocalName(registry_error_list, "RegistryError")) {
			String errorCode = get_att(registry_error, "errorCode");
			if (errorCode == null)
				continue;
			if (errorCode != null)
				result.add(errorCode);
		}

		return result;
	}

	public String get_regrep_error_msg() {
		if (response_element == null)
			return "No Message";
		OMElement registry_response = MetadataSupport.firstChildWithLocalName(response_element, "RegistryResponse") ;
		OMElement current = (registry_response == null) ? response_element : registry_response;
		OMElement registry_error_list = MetadataSupport.firstChildWithLocalName(current, "RegistryErrorList") ;
		if (registry_error_list == null)
			return "";
		StringBuffer errorMessages = new StringBuffer();
		for (OMElement registry_error : MetadataSupport.childrenWithLocalName(registry_error_list, "RegistryError")) {
			String msg = 
				registry_error.getAttributeValue(new QName("errorCode")) + "  :  " + 
				registry_error.getAttributeValue(new QName("codeContext")) + "  :  " + 
				registry_error.getText();
			if (msg == null) continue;
			errorMessages.append(msg);
		}
		return errorMessages.toString();
	}

	public String get_registry_response_status() throws XdsInternalException {
		try {
			AXIOMXPath xpathExpression = new AXIOMXPath ("@status");
			List nodeList = xpathExpression.selectNodes(response_element); 
			Iterator it = nodeList.iterator();
			if (! it.hasNext())
				throw new XdsInternalException("RegitryResponse:get_registry_response_status: Cannot retrieve /RegistryResponse/@status");
			OMAttribute att = (OMAttribute) it.next();
			return att.getAttributeValue();
		} catch (JaxenException e) {
			throw new XdsInternalException("Jaxen Exception from get_registry_response_status: " + e.getMessage());
		}
	}
	
	public boolean is_error() throws XdsInternalException {
		String status = get_registry_response_status();
		return  ! status.endsWith("Success");
	}

	String get_att(OMElement ele, String name) {
		OMAttribute att = ele.getAttribute(new QName(name));
		if (att == null)
			return null;
		return att.getAttributeValue();

	}

}
