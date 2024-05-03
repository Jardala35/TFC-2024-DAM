package com.tfc.v1.auth;

public class RegisterRequest {
	private String nomUsr;
	private String pass;
	private String nombre;
	private String apellido;
	private String email;
	public RegisterRequest(String nomUsr, String pass, String nombre, String apellido, String email) {
		super();
		this.nomUsr = nomUsr;
		this.pass = pass;
		this.nombre = nombre;
		this.apellido = apellido;
		this.email = email;
	}
	public RegisterRequest() {
		super();
	}
	public String getNomUsr() {
		return nomUsr;
	}
	public void setNomUsr(String nomUsr) {
		this.nomUsr = nomUsr;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
	
}