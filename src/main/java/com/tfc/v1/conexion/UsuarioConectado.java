package com.tfc.v1.conexion;

import org.springframework.stereotype.Component;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * Componente auxiliar para mantener un control de los usuarios conectados.
 * 
 *  @author Pablo Navarro Duro 
 */
@Component
public class UsuarioConectado {
	private ObservableMap<Integer, String> listaconect;
	
	

	public UsuarioConectado() {
		this.listaconect = FXCollections.observableHashMap();;
	}

	public ObservableMap<Integer, String> getListaconect() {
		return listaconect;
	}

	public void setListaconect(ObservableMap<Integer, String> listaconect) {
		this.listaconect = listaconect;
	}
	
	
	
	
}
