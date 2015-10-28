package org.openhealthtools.openxds.integrationtests;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openhealthtools.common.utils.OMUtil;

import gov.nist.registry.common2.registry.Metadata;

public class QueryReponseTest extends XdsTest {
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
	 * @throws IOException 
	 * 
	 * @throws  
	 * @throws Exception 
	 */
	@Test
	public void testQueryResponse() throws Exception {
		String query = IOUtils.toString( getClass().getResourceAsStream("/data/query_response.xml"));
		OMElement request = OMUtil.xmlStringToOM(query);
		Map<String, OMElement> map = buildXmlDocumentMap(request);
		OMElement elem = map.get("/RegistryObjectList/ExtrinsicObject");
		System.out.println(elem.getAttributeValue(new QName("status")));
	}
	
	@Test
	public void testGetDocumentLid() throws Exception {
		String docStr = IOUtils.toString( getClass().getResourceAsStream("/data/submit_document_for_update_second.xml"));
		OMElement model = OMUtil.xmlStringToOM(docStr);
		Metadata m = new Metadata(model, true);
		List<OMElement> eos = m.getExtrinsicObjects();
		for (OMElement elem : eos) {
			OMAttribute attrib = elem.getAttribute(new QName("lid"));
			if (attrib != null) {
				System.out.println("Found lid of: " + attrib.getAttributeValue());
			}
		}
	}
	
}
