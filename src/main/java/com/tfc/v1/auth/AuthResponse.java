package com.tfc.v1.auth;

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