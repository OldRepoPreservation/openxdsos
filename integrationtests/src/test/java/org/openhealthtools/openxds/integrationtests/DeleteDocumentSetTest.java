/**
 *  Copyright (c) 2009-2011 Misys Open Source Solutions (MOSS) and others
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  Contributors:
 *    Misys Open Source Solutions - initial API and implementation
 *    -
 */

package org.openhealthtools.openxds.integrationtests;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openhealthtools.common.utils.OMUtil;

/**
 * This class is an integrated test for IHE transaction ITI-57, namely,
 * UpdateDocumentSet and specifically the update document entry
 * metadata operation.
 *  
 * <p>
 * Before running this test case, be sure to configure the following:
 * <ul>
 *  <li>Both the XDS Repository and Registry servers have to be configured and started.</li>
 *  <li>The repositoryUrl needs be to set.</li>
 * </ul> 
 * 
 * Each test method can be run independently, so the order of each test method 
 * is not important.
 * 
 *  
 */
public class DeleteDocumentSetTest extends XdsTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for ProvideAndRegisterDocumentSet-b (ITI-41)
	 * 
	 * @throws  
	 * @throws Exception 
	 */
	@Test
	public void testDeleteDocument() throws Exception {
		createPatient(patientId);
		
		String submitDoc = IOUtils.toString(getClass().getResourceAsStream("/data/submit_document_for_update_first.xml"));
		String deleteDoc = IOUtils.toString(getClass().getResourceAsStream("/data/delete_document_set.xml"));
		
		//replace document and submission set uniqueId variables with actual uniqueIds. 
		String xdsDocumentEntryUniqueId = "2.16.840.1.113883.3.65.2." + System.currentTimeMillis();
		//replace the document uuid.
		String uuid = getUUID();

		ServiceClient sender = getRegistryServiceClientForRegistration();			

		submitDoc = submitDoc.replace("$XDSDocumentEntry.uniqueId", xdsDocumentEntryUniqueId);
		submitDoc = submitDoc.replace("$XDSSubmissionSet.uniqueId", "1.3.6.1.4.1.21367.2009.1.2.108." + System.currentTimeMillis());
		submitDoc = submitDoc.replace("$patientId", patientId);
		submitDoc = submitDoc.replace("$doc1", uuid);

		OMElement request = OMUtil.xmlStringToOM(submitDoc);
		System.out.println("Request:\n" +request);

		OMElement response = sender.sendReceive( request );
		assertNotNull(response); 
	
		String result = response.toString();
		System.out.println("Result:\n" +result);

		OMAttribute status = response.getAttribute(new QName("status"));
		assertEquals("urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success", status.getAttributeValue());

		sender = getRegistryServiceClient();
		String queryMessageStr = GetDocumentsQuery(uuid, false, "urn:uuid:5c4f972b-d56b-40ac-a5fc-c8ca9b40b9d4");
		OMElement queryRequest = OMUtil.xmlStringToOM(queryMessageStr);
//		OMElement queryResponse = sender.sendReceive( queryRequest );
		
//		Map<String, OMElement> map = buildXmlDocumentMap(queryResponse);
//		OMElement elem = map.get("/RegistryObjectList/ExtrinsicObject");
//		String docStatus = elem.getAttributeValue(new QName("status"));
//		System.out.println("Status of document in query: " + docStatus);
//		assertTrue(docStatus.indexOf("Approved") > 0);
		
		sender = getRegistryForDeleteServiceClient();			

		deleteDoc = deleteDoc.replace("$doc1", uuid);
		deleteDoc = deleteDoc.replace("$ss-to-doc", uuid);
		
		request = OMUtil.xmlStringToOM(deleteDoc);			
        
		System.out.println("Request:\n" +request);

		response = sender.sendReceive( request );
		assertNotNull(response); 

		status = response.getAttribute(new QName("status"));
		assertEquals("urn:oasis:names:tc:ebxml-regrep:ResponseStatusType:Success", status.getAttributeValue());

		result = response.toString();
		System.out.println("Result:\n" +result);
		
//		sender = getRegistryServiceClient();
//		queryMessageStr = GetDocumentsQuery(uuid, false, "urn:uuid:5c4f972b-d56b-40ac-a5fc-c8ca9b40b9d4");
//		queryRequest = OMUtil.xmlStringToOM(queryMessageStr);
//		queryResponse = sender.sendReceive( queryRequest );
//		
//		map = buildXmlDocumentMap(queryResponse);
//		elem = map.get("/RegistryObjectList/ExtrinsicObject");
//		docStatus = elem.getAttributeValue(new QName("status"));
//		System.out.println("Status of document in query after update: " + docStatus);
//		assertTrue(docStatus.indexOf("Deprecated") > 0);
		
	}
	
	private String GetDocumentsQuery(String id, boolean uniqueId, String queryId){
		String request = "<query:AdhocQueryRequest xsi:schemaLocation=\"urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0 ../schema/ebRS/query.xsd\" xmlns:query=\"urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:rim=\"urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0\" xmlns:rs=\"urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0\">\n"+
		              	 " <query:ResponseOption returnComposedObjects=\"true\" returnType=\"LeafClass\"/>\n"+
		              	 "  <rim:AdhocQuery id=\""+queryId+"\">\n";
		if (id != null && uniqueId == true) {
			request +=   "   <rim:Slot name=\"$XDSDocumentEntryUniqueId\">\n"+
			         	 "     <rim:ValueList>\n" + 
			             "       <rim:Value>('"+id+"')</rim:Value>\n" +   //Multiple values are allowed
			             "     </rim:ValueList>\n"+
			             "   </rim:Slot>\n";
		} else {
			request +=   "   <rim:Slot name=\"$XDSDocumentEntryEntryUUID\">\n" +
						 "     <rim:ValueList>\n" + 
						 "       <rim:Value>('"+id+"')</rim:Value>\n" +   //Multiple values are allowed
						 "     </rim:ValueList>\n" +
						 "   </rim:Slot>\n";			
		}
        request +=       "  </rim:AdhocQuery>\n" +
                         "</query:AdhocQueryRequest>";
		
		return request;
	}
}
