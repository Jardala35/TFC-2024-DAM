package com.tfc.v1.negocio.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfc.v1.auth.AuthResponse;
import com.tfc.v1.auth.AuthService;
import com.tfc.v1.auth.LoginRequest;
import com.tfc.v1.auth.RegisterRequest;
/**
 * Controlador REST que maneja las solicitudes de autenticación, como iniciar sesión
 * y registrar usuarios.
 * 
 * <p>Este controlador proporciona endpoints para realizar operaciones de autenticación.</p>
 * 
 * @author Pablo Navarro Duro 
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    
	@Autowired
    private AuthService authService;	
    
	/**
     * Constructor de la clase AuthController.
     */
    public AuthController() {
		super();
	}    

    /**
     * Obtiene el servicio de autenticación asociado a este controlador.
     * 
     * @return El servicio de autenticación asociado a este controlador.
     */
	public AuthService getAuthService() {
		return authService;
	}

	/**
     * Maneja las solicitudes de inicio de sesión.
     * 
     * @param request La solicitud de inicio de sesión.
     * @return Una respuesta ResponseEntity que contiene el resultado de la operación de inicio de sesión.
     */
	@PostMapping(value = "login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request)
    {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Maneja las solicitudes de registro de nuevos usuarios.
     * 
     * @param request La solicitud de registro de usuario.
     * @return Una respuesta ResponseEntity que contiene el resultado de la operación de registro de usuario.
     */
    @PostMapping(value = "register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request)
    {
        return ResponseEntity.ok(authService.register(request));
    }
}