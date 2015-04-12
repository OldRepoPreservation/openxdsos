/*
 * XmlUtil.java
 *
 * Created on November 2, 2004, 11:15 AM
 */

package gov.nist.registry.common2.xml;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



/**
 *
 * @author  mccaffrey
 */

public class XmlUtil {
    
    /** Creates a new instance of XmlUtil */
    public XmlUtil() {
    }

    static public String XmlWriter(Node node) {
	return XmlFormatter.format(XmlFormatter.normalize(XmlUtil.getStringFromNode(node)),false);
    }
    
    static public String getStringFromNode(Node node) {

        StringBuffer sb = new StringBuffer();
        if ( node == null ) {
            return null;
        }
        
        int type = node.getNodeType();
        switch ( type ) {
            case Node.DOCUMENT_NODE:
                sb.append("");
                sb.append(XmlUtil.getStringFromNode((((Document)node).getDocumentElement())));
                break;
                
            case Node.ELEMENT_NODE:
                sb.append('<');
                
                sb.append(node.getNodeName());
                NamedNodeMap attrs = node.getAttributes();
                
                for ( int i = 0; i < attrs.getLength(); i++ ) {
                    sb.append(' ');
                    sb.append(attrs.item(i).getNodeName());
                    sb.append("=\"");
                    
                    sb.append(attrs.item(i).getNodeValue());
                    sb.append('"');
                }
                sb.append('>');
                sb.append("\n"); // HACK
                NodeList children = node.getChildNodes();
                if ( children != null ) {
                    int len = children.getLength();
                    for ( int i = 0; i < len; i++ ) {
                        sb.append(XmlUtil.getStringFromNode(children.item(i)));
                    }
                }
                break;
                
            case Node.TEXT_NODE:
                sb.append(node.getNodeValue());
                break;
                
        }
        
        if ( type == Node.ELEMENT_NODE ) {
            sb.append("</");
            sb.append(node.getNodeName());
            sb.append(">");
            sb.append("\n"); // HACK
        }
        
        return sb.toString();
        
        
    }
    
}
