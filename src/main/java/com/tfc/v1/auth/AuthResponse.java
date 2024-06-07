package com.tfc.v1.auth;

/**
 * clase auxiliar para la autenticacion, maneja el token de JWT despues de un login o registro
 * @author Pablo Navarro Duro 
 * @see AuthService
 */
public class AuthResponse {
    String token;

	public AuthResponse(String token) {
		super();
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	} 
    
    
    
}