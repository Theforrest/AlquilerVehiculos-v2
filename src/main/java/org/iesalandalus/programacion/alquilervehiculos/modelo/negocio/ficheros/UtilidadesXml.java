package org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.ficheros;

import java.io.File;
import java.io.IOException;

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
		return factory.newDocumentBuilder();
	}

	public static void escribirXmlAFichero(Document documento, File salida) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(documento);
		StreamResult result = new StreamResult(salida);

		transformer.transform(source, result);
	}

	public static Document leerXmlDeFichero(File ficheroXml)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder db = crearConstructorDocumentoXml();

		return db.parse(ficheroXml);
	}
}
