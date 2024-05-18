package com.tfc.v1.negocio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfc.v1.negocio.restcontroller.AuthController;

@Service
public class Gestor {
	@Autowired
	private AuthController authcontroller;
	
	

	public Gestor(AuthController authcontroller) {
		super();
		this.authcontroller = authcontroller;
		System.out.println("Gestor creado");
	}

	public AuthController getAuthcontroller() {
		return authcontroller;
	}

	public void setAuthcontroller(AuthController authcontroller) {
		this.authcontroller = authcontroller;
	}
	
	
}
