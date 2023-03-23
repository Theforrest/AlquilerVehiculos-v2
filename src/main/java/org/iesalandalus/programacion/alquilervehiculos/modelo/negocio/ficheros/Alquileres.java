package org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.ficheros;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Alquiler;
import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Cliente;
import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Vehiculo;
import org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.IAlquileres;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Alquileres implements IAlquileres {

	private List<Alquiler> coleccionAlquileres;
	private static Alquileres instancia = new Alquileres();
	private static final File FICHERO_ALQUILERES = new File("datos\\alquileres.xml");
	private static final String RAIZ = "alquileres";
	private static final String ALQUILER = "alquiler";
	private static final String CLIENTE = "cliente";
	private static final String VEHICULO = "vehiculo";
	private static final String FECHA_ALQUILER = "fechaAlquiler";
	private static final String FECHA_DEVOLUCION = "fechaDevolucion";

	public Alquileres() {
		coleccionAlquileres = new ArrayList<>();
	}
	
	static Alquileres getInstancia() {
		return instancia;
	}
	
	@Override
	public void comenzar() {
		try {
			leerDom(UtilidadesXml.leerXmlDeFichero(FICHERO_ALQUILERES));
		} catch (ParserConfigurationException | SAXException | IOException | OperationNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void leerDom(Document documentoXml) throws OperationNotSupportedException {
		NodeList lista = documentoXml.getElementsByTagName(ALQUILER);
		for (int i = 0; i < lista.getLength(); i++) {
			Node nodo = lista.item(i);
			if (nodo.getNodeType() == Node.ELEMENT_NODE) {
				Element elemento = (Element) nodo;
				insertar(getAlquiler(elemento));
			}
		}
	}
	
	private Alquiler getAlquiler (Element elemento) {
		String cliente = elemento.getAttribute(CLIENTE);
		String vehiculo = elemento.getAttribute(VEHICULO);
		String fechaAlquiler = elemento.getAttribute(FECHA_ALQUILER);
		String fechaDevolucion = elemento.getAttribute(FECHA_DEVOLUCION);
		
		Alquiler alquiler = new Alquiler(Clientes.getInstancia().buscar(Cliente.getClienteConDni(cliente)), Vehiculos.getInstancia().buscar(Vehiculo.getVehiculoConMatricula(vehiculo)), LocalDate.parse(fechaAlquiler, DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		
		
		if (!fechaDevolucion.isBlank()) {
			try {
				alquiler.devolver(LocalDate.parse(fechaDevolucion, DateTimeFormatter.ofPattern("dd/MM/yyyy")));
			} catch (OperationNotSupportedException e) {
			}
		}
		
		
		return alquiler;
	}
	@Override
	public void terminar() {
		try {
			UtilidadesXml.escribirXmlAFichero(crearDom(), FICHERO_ALQUILERES);
		} catch (TransformerException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Document crearDom() throws ParserConfigurationException {
		Document documento = UtilidadesXml.crearConstructorDocumentoXml().newDocument();
	    Element raiz = documento.createElement(RAIZ);
	    documento.appendChild(raiz);
	    
	    for(Alquiler alquiler : coleccionAlquileres) {
	    	raiz.appendChild(getElemento(documento, alquiler));
	    }
	    
		return documento;
	}
	
	private Element getElemento(Document documentoXml, Alquiler alquiler) {
	    Element elemento = documentoXml.createElement(ALQUILER);
	    elemento.setAttribute(CLIENTE, alquiler.getCliente().getDni());
	    elemento.setAttribute(FECHA_ALQUILER, alquiler.getFechaAlquiler().toString());
	    if (alquiler.getFechaDevolucion() != null) {
		    elemento.setAttribute(FECHA_DEVOLUCION, alquiler.getFechaDevolucion().toString());

	    }
	    elemento.setAttribute(VEHICULO, alquiler.getVehiculo().getMatricula());

		return elemento;
	}

	@Override
	public List<Alquiler> get() {

		return new ArrayList<>(coleccionAlquileres);
	}

	@Override
	public List<Alquiler> get(Cliente cliente) {

		List<Alquiler> copiaAlquileres = new ArrayList<>();
		for (Alquiler alquiler : coleccionAlquileres) {
			if (alquiler.getCliente().equals(cliente)) {
				copiaAlquileres.add(alquiler);
			}
		}
		return copiaAlquileres;
	}

	@Override
	public List<Alquiler> get(Vehiculo vehiculo) {

		List<Alquiler> copiaAlquileres = new ArrayList<>();
		for (Alquiler alquiler : coleccionAlquileres) {
			if (alquiler.getVehiculo().equals(vehiculo)) {
				copiaAlquileres.add(alquiler);
			}
		}
		return copiaAlquileres;
	}

	private void comprobarAlquiler(Cliente cliente, Vehiculo vehiculo, LocalDate fechaAlquiler)
			throws OperationNotSupportedException {

		for (Alquiler alquiler : get()) {
			if (alquiler.getCliente().equals(cliente)) {
				if (alquiler.getFechaDevolucion() == null) {
					throw new OperationNotSupportedException("ERROR: El cliente tiene otro alquiler sin devolver.");
				} else if (alquiler.getFechaDevolucion().compareTo(fechaAlquiler) >= 0) {
					throw new OperationNotSupportedException("ERROR: El cliente tiene un alquiler posterior.");
				}
			}
			if (alquiler.getVehiculo().equals(vehiculo)) {
				if (alquiler.getFechaDevolucion() == null) {
					throw new OperationNotSupportedException("ERROR: El vehículo está actualmente alquilado.");
				} else if (alquiler.getFechaDevolucion().compareTo(fechaAlquiler) >= 0) {
					throw new OperationNotSupportedException("ERROR: El vehículo tiene un alquiler posterior.");
				}
			}
		}

	}

	@Override
	public void insertar(Alquiler alquiler) throws OperationNotSupportedException {
		if (alquiler == null) {
			throw new NullPointerException("ERROR: No se puede insertar un alquiler nulo.");
		}
		comprobarAlquiler(alquiler.getCliente(), alquiler.getVehiculo(), alquiler.getFechaAlquiler());
		coleccionAlquileres.add(alquiler);
	}

	@Override
	public Alquiler buscar(Alquiler alquiler) {
		if (alquiler == null) {
			throw new NullPointerException("ERROR: No se puede buscar un alquiler nulo.");
		}
		Alquiler busqueda = null;
		int index = coleccionAlquileres.indexOf(alquiler);
		if (index != -1) {
			busqueda = coleccionAlquileres.get(index);
		}
		return busqueda;
	}

	@Override
	public void borrar(Alquiler alquiler) throws OperationNotSupportedException {
		if (alquiler == null) {
			throw new NullPointerException("ERROR: No se puede borrar un alquiler nulo.");
		}
		if (!(coleccionAlquileres.contains(alquiler))) {
			throw new OperationNotSupportedException("ERROR: No existe ningún alquiler igual.");
		}
		coleccionAlquileres.remove(buscar(alquiler));

	}

	@Override
	public void devolver(Cliente cliente, LocalDate fechaDevolucion) throws OperationNotSupportedException {
		Alquiler alquiler = getAlquilerAbierto(cliente);
		if (alquiler == null) {
			throw new OperationNotSupportedException("ERROR: No existe ningún alquiler abierto para ese cliente.");
		}
		alquiler.devolver(fechaDevolucion);

	}

	private Alquiler getAlquilerAbierto(Cliente cliente) {
		if (cliente == null) {
			throw new NullPointerException("ERROR: No se puede devolver un alquiler de un cliente nulo.");
		}
		Iterator<Alquiler> iterador = get(cliente).iterator();
		Alquiler alquiler = null;
		while (iterador.hasNext() && alquiler == null) {
			Alquiler siguiente = iterador.next();
			if (siguiente.getFechaDevolucion() == null) {
				alquiler = siguiente;
			}
		}

		return alquiler;
	}

	@Override
	public void devolver(Vehiculo vehiculo, LocalDate fechaDevolucion) throws OperationNotSupportedException {
		Alquiler alquiler = getAlquilerAbierto(vehiculo);
		if (alquiler == null) {
			throw new OperationNotSupportedException("ERROR: No existe ningún alquiler abierto para ese vehículo.");
		}
		alquiler.devolver(fechaDevolucion);

	}

	private Alquiler getAlquilerAbierto(Vehiculo vehiculo) {
		if (vehiculo == null) {
			throw new NullPointerException("ERROR: No se puede devolver un alquiler de un vehículo nulo.");
		}
		Iterator<Alquiler> iterador = get(vehiculo).iterator();
		Alquiler alquiler = null;
		while (iterador.hasNext() && alquiler == null) {
			Alquiler siguiente = iterador.next();
			if (siguiente.getFechaDevolucion() == null) {
				alquiler = siguiente;
			}
		}

		return alquiler;
	}


}
