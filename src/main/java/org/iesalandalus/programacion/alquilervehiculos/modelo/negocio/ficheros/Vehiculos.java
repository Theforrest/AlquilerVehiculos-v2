package org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.ficheros;

import java.io.File; 
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Autobus;
import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Furgoneta;
import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Turismo;
import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Vehiculo;
import org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.IVehiculos;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Vehiculos implements IVehiculos {

	private List<Vehiculo> coleccionVehiculos;
	private static Vehiculos instancia;
	private static final File FICHERO_VEHICULOS = new File(String.format("%s%s%s", "datos", File.separator, "vehiculos.xml"));
	private static final String RAIZ = "vehiculos";
	private static final String VEHICULO = "vehiculo";
	private static final String MARCA = "marca";
	private static final String MODELO = "modelo";
	private static final String MATRICULA = "matricula";
	private static final String CILINDRADA = "cilindrada";
	private static final String PLAZAS = "plazas";
	private static final String PMA = "pma";
	private static final String TIPO = "tipo";
	private static final String TURISMO = "turismo";
	private static final String AUTOBUS = "autobus";
	private static final String FURGONETA = "furgoneta";

	private Vehiculos() {
		coleccionVehiculos = new ArrayList<>();
	}
	
	static Vehiculos getInstancia() {
		if (instancia == null) {
			instancia = new Vehiculos();
		}
		return instancia;
	}
	@Override
	public void comenzar() {
		try {
			leerDom(UtilidadesXml.leerXmlDeFichero(FICHERO_VEHICULOS));
		} catch (ParserConfigurationException | SAXException | IOException | OperationNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void leerDom(Document documentoXml) throws OperationNotSupportedException {
		NodeList lista = documentoXml.getElementsByTagName(VEHICULO);
		for (int i = 0; i < lista.getLength(); i++) {
			Node nodo = lista.item(i);
			if (nodo.getNodeType() == Node.ELEMENT_NODE) {
				Element elemento = (Element) nodo;
				insertar(getVehiculo(elemento));
			}
		}
	}
	
	private Vehiculo getVehiculo (Element elemento) {
		String marca = elemento.getAttribute(MARCA);
		String modelo = elemento.getAttribute(MODELO);
		String matricula = elemento.getAttribute(MATRICULA);
		String tipo = elemento.getAttribute(TIPO);
		
		Vehiculo vehiculo = null;
		switch (tipo) {
		case TURISMO: {
			String cilindrada = elemento.getAttribute(CILINDRADA);

			vehiculo =  new Turismo(marca, modelo, Integer.parseInt(cilindrada), matricula);
			break;
		}
		case AUTOBUS: {
			String plazas = elemento.getAttribute(PLAZAS);

			vehiculo =  new Autobus(marca, modelo, Integer.parseInt(plazas), matricula);
			break;
		}
		case FURGONETA: {
			String plazas = elemento.getAttribute(PLAZAS);
			String pma = elemento.getAttribute(PMA);

			vehiculo =  new Furgoneta(marca, modelo,Integer.parseInt(pma), Integer.parseInt(plazas), matricula);
			break;
		}
		default:
		}
		return vehiculo;
	}
	
	@Override
	public void terminar() {
		try {
			UtilidadesXml.escribirXmlAFichero(crearDom(), FICHERO_VEHICULOS);
		} catch (TransformerException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Document crearDom() throws ParserConfigurationException {
		Document documento = UtilidadesXml.crearConstructorDocumentoXml().newDocument();
	    Element raiz = documento.createElement(RAIZ);
	    documento.appendChild(raiz);
	    
	    for(Vehiculo vehiculo : coleccionVehiculos) {
	    	raiz.appendChild(getElemento(documento, vehiculo));
	    }
	    
		return documento;
	}
	
	private Element getElemento(Document documentoXml, Vehiculo vehiculo) {
	    Element elemento = documentoXml.createElement(VEHICULO);
	    elemento.setAttribute(MARCA, vehiculo.getMarca());
	    elemento.setAttribute(MODELO, vehiculo.getModelo());
	    elemento.setAttribute(MATRICULA, vehiculo.getMatricula());
	    
	    if (vehiculo instanceof Turismo turismo) {
	    	elemento.setAttribute(CILINDRADA, Integer.toString(turismo.getCilindrada()));
		    elemento.setAttribute(TIPO, TURISMO);
	    } else if (vehiculo instanceof Autobus autobus) {
	    	elemento.setAttribute(PLAZAS, Integer.toString(autobus.getPlazas()));
		    elemento.setAttribute(TIPO, AUTOBUS);
	    } else if (vehiculo instanceof Furgoneta furgoneta) {
	    	elemento.setAttribute(PMA, Integer.toString(furgoneta.getPma()));
	    	elemento.setAttribute(PLAZAS, Integer.toString(furgoneta.getPlazas()));
		    elemento.setAttribute(TIPO, FURGONETA);
	    }
	    

		return elemento;
	}
	@Override
	public List<Vehiculo> get() {

		return new ArrayList<>(coleccionVehiculos);
	}

	@Override
	public void insertar(Vehiculo vehiculo) throws OperationNotSupportedException {
		if (vehiculo == null) {
			throw new NullPointerException("ERROR: No se puede insertar un vehículo nulo.");
		}
		if (coleccionVehiculos.contains(vehiculo)) {
			throw new OperationNotSupportedException("ERROR: Ya existe un vehículo con esa matrícula.");
		}
		coleccionVehiculos.add(vehiculo);
	}

	@Override
	public Vehiculo buscar(Vehiculo vehiculo) {
		if (vehiculo == null) {
			throw new NullPointerException("ERROR: No se puede buscar un vehículo nulo.");
		}
		Vehiculo busqueda = null;
		int index = coleccionVehiculos.indexOf(vehiculo);
		if (index != -1) {
			busqueda = coleccionVehiculos.get(index);
		}
		return busqueda;
	}

	@Override
	public void borrar(Vehiculo vehiculo) throws OperationNotSupportedException {
		if (vehiculo == null) {
			throw new NullPointerException("ERROR: No se puede borrar un vehículo nulo.");
		}
		if (!coleccionVehiculos.remove(buscar(vehiculo))) {
			throw new OperationNotSupportedException("ERROR: No existe ningún vehículo con esa matrícula.");
		}
	}

	

}
