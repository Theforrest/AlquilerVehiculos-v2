package org.iesalandalus.programacion.alquilervehiculos.vista;

import org.iesalandalus.programacion.alquilervehiculos.vista.texto.VistaTexto;

public enum FactoriaVista {

	TEXTO {
		@Override
		public Vista crear() {
			// TODO Auto-generated method stub
			return new VistaTexto();
		}
	};

	public abstract Vista crear();
}
