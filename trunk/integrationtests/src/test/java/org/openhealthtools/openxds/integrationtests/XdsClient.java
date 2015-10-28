package org.openhealthtools.openxds.integrationtests;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.client.ServiceClient;
import org.openhealthtools.common.utils.OMUtil;

public class XdsClient extends XdsTest
{

	public static void main(String[] args) {
		try {
			setUpBeforeClass();
			XdsClient client = new XdsClient();
			client.submitOneDocument(XdsTest.patientId);
			client.testFindDocuments_MultipleStatus();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testFindDocuments_MultipleStatus() throws Exception {
		//Generate StoredQuery request message
		patientId = XdsTest.patientId;
		String message = findDocumentsQuery(patientId);
		OMElement request = OMUtil.xmlStringToOM(message);			
		System.out.println("Request:\n" +request);

		//3. Send a StoredQuery
		ServiceClient sender = getRegistryServiceClient();															 
		OMElement response = sender.sendReceive( request );

		//4. Verify the response is correct
		OMAttribute status = response.getAttribute(new QName("status"));

		String result = response.toString();
		System.out.println("Result:\n" +result);
	}

	
	public String findDocumentsQuery(String patientId){
		String request = "<query:AdhocQueryRequest xsi:schemaLocation=\"urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0 ../schema/ebRS/query.xsd\" xmlns:query=\"urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:rim=\"urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0\" xmlns:rs=\"urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0\">\n"+
		              	 " <query:ResponseOption returnComposedObjects=\"true\" returnType=\"ObjectRef\"/>\n"+
		              	 "  <rim:AdhocQuery id=\"urn:uuid:14d4debf-8f97-4251-9a74-a90016b0af0d\">\n";
		if (patientId != null) {
			request +=   "   <rim:Slot name=\"$XDSDocumentEntryPatientId\">\n"+
			         	 "     <rim:ValueList>\n" + 
			             "       <rim:Value>'"+patientId+"'</rim:Value>\n" +
			             "     </rim:ValueList>\n"+
			             "   </rim:Slot>\n";
		}
		
			request +=   "   <rim:Slot name=\"$XDSDocumentEntryStatus\">\n" +
						 "     <rim:ValueList>\n" + 
						 "       <rim:Value>('urn:oasis:names:tc:ebxml-regrep:StatusType:Approved', 'urn:oasis:names:tc:ebxml-regrep:StatusType:Deprecated')</rim:Value>\n" +
						 "     </rim:ValueList>\n" +
						 "   </rim:Slot>\n";			
			
        request +=       "  </rim:AdhocQuery>\n" +
                         "</query:AdhocQueryRequest>";
		
		return request;
	}

}
