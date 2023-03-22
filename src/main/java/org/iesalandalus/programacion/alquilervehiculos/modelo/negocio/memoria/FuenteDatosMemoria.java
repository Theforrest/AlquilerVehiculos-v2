package org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.memoria;

import org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.IAlquileres;
import org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.IClientes;
import org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.IFuenteDatos;
import org.iesalandalus.programacion.alquilervehiculos.modelo.negocio.IVehiculos;

public class FuenteDatosMemoria implements IFuenteDatos {

	public IClientes crearClientes() {
		return new Clientes();
	}

	public IVehiculos crearVehiculos() {
		return new Vehiculos();
	}

	public IAlquileres crearAlquileres() {
		return new Alquileres();
	}
}
