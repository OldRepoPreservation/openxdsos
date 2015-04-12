package gov.nist.registry.common2.xca;

import gov.nist.registry.common2.registry.MetadataSupport;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;

public class HomeAttribute {
	String home;
	String errs = "";

	public HomeAttribute(String home) {
		this.home = home;
	}

	boolean isIdentifiable(String name) {
		return name.equals("ObjectRef") ||
		name.equals("ExtrinsicObject") ||
		name.equals("RegistryPackage") ||
		name.equals("ExternalIdentifier") ||
		name.equals("Association") ||
		name.equals("Classification");

	}

	public void set(OMElement root) {
		String localname = root.getLocalName();

		if (isIdentifiable(localname)) 
			root.addAttribute("home", home, null);

		for (OMNode child=root.getFirstElement(); child != null; child=child.getNextOMSibling()) {
			if (child instanceof OMText) {
				continue;				
			}
			OMElement child_e = (OMElement) child;
			set(child_e);
		}
	}

	public String validate(OMElement root) {
		errs = "";
		validate1(root);
		return errs;
	}

	public void validate1(OMElement root) {
		String localname = root.getLocalName();
		
		if (isIdentifiable(localname)) {

			OMAttribute home_att = root.getAttribute(MetadataSupport.home_qname);

			if (home_att == null) {
				errs += "\nElement of type " + localname + " does not contain a home attribute";
			} else {
				String home1 = home_att.getAttributeValue();
				if (home1 == null) home1 = "";
				if ( !home1.equals(home))
					errs += "\nElement of type " + localname + " has home of [" + home1 + "] which does not match expected value of [" + home + "]"; 
			}
		}

		for (OMNode child=root.getFirstElement(); child != null; child=child.getNextOMSibling()) {
			OMElement child_e = (OMElement) child;
			validate1(child_e);
		}
	}

}
