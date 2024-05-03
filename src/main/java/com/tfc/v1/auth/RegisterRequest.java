package com.tfc.v1.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
	private String nomUsr;
	private String pass;
	private String nombre;
	private String apellido;
	private String email;
}