package com.tfc.v1.auth;

/**
 * Clase que contiene los datos para hacer el login
 * @author Pablo Navarro Duro 
 */
public class LoginRequest {
	private String username;
	private String password;
	public LoginRequest(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
	public LoginRequest() {
		super();
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	
}