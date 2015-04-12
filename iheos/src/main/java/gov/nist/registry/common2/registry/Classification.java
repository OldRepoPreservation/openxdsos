package gov.nist.registry.common2.registry;

import gov.nist.registry.common2.exception.MetadataException;

import org.apache.axiom.om.OMElement;

public class Classification {
	String classification_scheme;
	String code_value;
	String code_display_name;
	String coding_scheme;
	String classification_node;
	OMElement classification_ele;
	
	public Classification(OMElement cl) {
		parse(cl);
	}
	
	void parse(OMElement cl)  {
		this.classification_ele = cl;
		this.classification_scheme = cl.getAttributeValue(MetadataSupport.classificationscheme_qname);
		this.classification_node = cl.getAttributeValue(MetadataSupport.classificationnode_qname);
		this.code_value = cl.getAttributeValue(MetadataSupport.noderepresentation_qname);
		OMElement name_ele = MetadataSupport.firstChildWithLocalName(cl, "Name") ;
		Metadata m = new Metadata();
		this.coding_scheme = m.getSlotValue(cl, "codingScheme", 0);
		this.code_display_name = m.getNameValue(cl);
		
		if (code_value == null) code_value = "";
		if (code_display_name == null) code_display_name = "";
		if (coding_scheme == null) coding_scheme = "";
	}
	
	public String getCodeValue() { return code_value; }
	public String getCodeDisplayName() { return code_display_name; }
	public String getCodeScheme() { return coding_scheme; }
	public String getClassificationScheme() { return classification_scheme; }
	public String getClassificationNode() { return classification_node; }
	
	public String identifying_string() {
		return "Classification (classificationScheme=" + classification_scheme + " codingScheme=" + coding_scheme + ") of object " + parent_id(); 
	}
	
	public String parent_id() {
		OMElement parent = (OMElement) classification_ele.getParent();
		if (parent == null) return "Unknown";
		return parent.getAttributeValue(MetadataSupport.id_qname);
	}
	
}
