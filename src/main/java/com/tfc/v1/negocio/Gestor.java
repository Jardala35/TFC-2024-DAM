package com.tfc.v1.negocio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfc.v1.negocio.restcontroller.AuthController;
import com.tfc.v1.negocio.restcontroller.ControladorRest;

@Service
public class Gestor {
	@Autowired
	private AuthController authcontroller;
	@Autowired
	private ControladorRest contRest;	

	public Gestor(AuthController authcontroller, ControladorRest contRest) {
		super();
		this.authcontroller = authcontroller;
		this.contRest = contRest;
		System.out.println("Gestor creado");
	}

	public AuthController getAuthcontroller() {
		return authcontroller;
	}

	public void setAuthcontroller(AuthController authcontroller) {
		this.authcontroller = authcontroller;
	}

	public ControladorRest getContRest() {
		return contRest;
	}

	public void setContRest(ControladorRest contRest) {
		this.contRest = contRest;
	}
	
	
	
	
}
