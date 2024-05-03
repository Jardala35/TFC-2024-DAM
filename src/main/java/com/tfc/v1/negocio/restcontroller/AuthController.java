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

@RestController
@RequestMapping("/auth")
public class AuthController {
    
	@Autowired
    private AuthService authService;
	
	
    
    public AuthController() {
		super();
	}

	@PostMapping(value = "login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request)
    {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(value = "register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request)
    {
        return ResponseEntity.ok(authService.register(request));
    }
}