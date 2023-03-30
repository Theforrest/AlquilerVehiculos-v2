package org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.ficheros;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class UtilidadesXml {

	private UtilidadesXml() {
	}

	public static DocumentBuilder crearConstructorDocumentoXml() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// to be compliant, completely disable DOCTYPE declaration:
		factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		// or completely disable external entities declarations:
		factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
		factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		// or prohibit the use of all protocols by external entities:
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
		// or disable entity expansion but keep in mind that this doesn't prevent
		// fetching external entities
		// and this solution is not correct for OpenJDK < 13 due to a bug:
		// https://bugs.openjdk.java.net/browse/JDK-8206132
		factory.setExpandEntityReferences(false);

		return factory.newDocumentBuilder();
	}

	public static void escribirXmlAFichero(Document documento, File salida) throws TransformerException {
		if (salida.exists()) {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			// to be compliant, prohibit the use of all protocols by external entities:
			transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(documento);
			StreamResult result = new StreamResult(salida);

			transformer.transform(source, result);
		}
	}

	public static Document leerXmlDeFichero(File ficheroXml)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder db = crearConstructorDocumentoXml();
		return (db.parse(ficheroXml));
	}
}
