package gov.nist.registry.common2.xml;

import gov.nist.registry.common2.exception.XMLParserException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;

public class Parse {
	static public OMElement parse_xml_file(String filename) throws FactoryConfigurationError, XMLParserException {
		File infile = new File(filename);

//		create the parser
		XMLStreamReader parser=null;

		try {
			parser = XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(infile));
		} catch (XMLStreamException e) {
			throw new XMLParserException("gov.nist.registry.common2.xml.Parse: Could not create XMLStreamReader from " + filename);
		} catch (FileNotFoundException e) {
			throw new XMLParserException("gov.nist.registry.common2.xml.Parse: Could not find input file " + filename);
		}

//		create the builder
		StAXOMBuilder builder = new StAXOMBuilder(parser);

//		get the root element (in this case the envelope)
		OMElement documentElement =  builder.getDocumentElement();	
		if (documentElement == null) throw new XMLParserException("gov.nist.registry.common2.xml.Parse: No document element");
		return documentElement;
	}

	static public OMElement parse_xml_string(String input_string) throws XMLParserException {
		byte[] ba = input_string.getBytes();

//		create the parser
		XMLStreamReader parser=null;

		try {
			parser = XMLInputFactory.newInstance().createXMLStreamReader(new ByteArrayInputStream(ba));
		} catch (XMLStreamException e) {
			throw new XMLParserException("gov.nist.registry.common2.xml.Parse: Could not create XMLStreamReader from " + "input stream");
		}
//		create the builder
		StAXOMBuilder builder = new StAXOMBuilder(parser);

//		get the root element (in this case the envelope)
		OMElement documentElement =  builder.getDocumentElement();

		return documentElement;
	}

}
