package org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.ficheros;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.OperationNotSupportedException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.iesalandalus.programacion.alquilervehiculos.modelo.dominio.Cliente;
import org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.IClientes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Clientes implements IClientes {

	private List<Cliente> coleccionClientes;
	private static Clientes instancia = new Clientes();
	private static final File FICHERO_CLIENTES = new File("datos\\clientes.xml");
	private static final String RAIZ = "clientes";
	private static final String CLIENTE = "cliente";
	private static final String NOMBRE = "nombre";
	private static final String DNI = "dni";
	private static final String TELEFONO = "telefono";
	
	private Clientes() {
		coleccionClientes = new ArrayList<>();
	}
	
	static Clientes getInstancia() {
		return instancia;
	}
	@Override
	public void comenzar() {
		try {
			leerDom(UtilidadesXml.leerXmlDeFichero(FICHERO_CLIENTES));
		} catch (ParserConfigurationException | SAXException | IOException | OperationNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void leerDom(Document documentoXml) throws OperationNotSupportedException {
		NodeList lista = documentoXml.getElementsByTagName(CLIENTE);
		for (int i = 0; i < lista.getLength(); i++) {
			Node nodo = lista.item(i);
			if (nodo.getNodeType() == Node.ELEMENT_NODE) {
				Element elemento = (Element) nodo;
				insertar(getCliente(elemento));
			}
		}
	}
	
	private Cliente getCliente (Element elemento) {
		String nombre = elemento.getAttribute(NOMBRE);
		String dni = elemento.getAttribute(DNI);
		String telefono = elemento.getAttribute(TELEFONO);
		
		return new Cliente(nombre, dni, telefono);
	}
	@Override
	public void terminar() {
		try {
			UtilidadesXml.escribirXmlAFichero(crearDom(), FICHERO_CLIENTES);
		} catch (TransformerException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Document crearDom() throws ParserConfigurationException {
		Document documento = UtilidadesXml.crearConstructorDocumentoXml().newDocument();
	    Element raiz = documento.createElement(RAIZ);
	    documento.appendChild(raiz);
	    
	    for(Cliente cliente : coleccionClientes) {
	    	raiz.appendChild(getElemento(documento, cliente));
	    }
	    
		return documento;
	}
	
	private Element getElemento(Document documentoXml, Cliente cliente) {
	    Element elemento = documentoXml.createElement(CLIENTE);
	    elemento.setAttribute(NOMBRE, cliente.getNombre());
	    elemento.setAttribute(DNI, cliente.getDni());
	    elemento.setAttribute(TELEFONO, cliente.getTelefono());

		return elemento;
	}

	@Override
	public List<Cliente> get() {
		return new ArrayList<>(coleccionClientes);
	}

	@Override
	public void insertar(Cliente cliente) throws OperationNotSupportedException {
		if (cliente == null) {
			throw new NullPointerException("ERROR: No se puede insertar un cliente nulo.");
		}
		if (coleccionClientes.contains(cliente)) {
			throw new OperationNotSupportedException("ERROR: Ya existe un cliente con ese DNI.");
		}
		coleccionClientes.add(cliente);
	}

	@Override
	public Cliente buscar(Cliente cliente) {
		if (cliente == null) {
			throw new NullPointerException("ERROR: No se puede buscar un cliente nulo.");
		}
		Cliente busqueda = null;
		int index = coleccionClientes.indexOf(cliente);
		if (index != -1) {
			busqueda = coleccionClientes.get(index);
		}
		return busqueda;
	}

	@Override
	public void borrar(Cliente cliente) throws OperationNotSupportedException {
		if (cliente == null) {
			throw new NullPointerException("ERROR: No se puede borrar un cliente nulo.");
		}
		if (!coleccionClientes.remove(buscar(cliente))) {
			throw new OperationNotSupportedException("ERROR: No existe ningún cliente con ese DNI.");
		}
	}

	@Override
	public void modificar(Cliente cliente, String nombre, String telefono) throws OperationNotSupportedException {
		if (cliente == null) {
			throw new NullPointerException("ERROR: No se puede modificar un cliente nulo.");
		}
		Cliente busqueda = buscar(cliente);
		if (busqueda == null) {
			throw new OperationNotSupportedException("ERROR: No existe ningún cliente con ese DNI.");
		}

		if (!((nombre == null || nombre.isBlank()) && (telefono == null || telefono.isBlank()))) {

			if (nombre == null || nombre.isBlank()) {
				busqueda.setTelefono(telefono);
			} else {
				if (telefono == null || telefono.isBlank()) {
					busqueda.setNombre(nombre);
				} else {
					busqueda.setNombre(nombre);
					busqueda.setTelefono(telefono);
				}
			}

		}

	}
}
