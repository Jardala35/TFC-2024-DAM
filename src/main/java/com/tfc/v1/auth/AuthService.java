package com.tfc.v1.auth;
/**
 * 
 * 
 */
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tfc.v1.auth.jwt.JwtService;
import com.tfc.v1.modelo.entidades.usuario.Rol;
import com.tfc.v1.modelo.entidades.usuario.Usuario;
import com.tfc.v1.modelo.persistencia.RepositorioUsuario;

/**
 * Servicio que maneja el login y el registro de un usuario.
 * gestiona la llamada al repositorio
 * 
 * @author Pablo Navarro Duro
 * 
 * @see RepositorioUsuario
 * @see JwtService
 */
@Service
public class AuthService {

    private  RepositorioUsuario userRepository;
    private  JwtService jwtService;
    private  PasswordEncoder passwordEncoder;
    private  AuthenticationManager authenticationManager;
    
    public AuthService(RepositorioUsuario userRepository, JwtService jwtService, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager) {
		super();
		this.userRepository = userRepository;
		this.jwtService = jwtService;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
	}
    
    public RepositorioUsuario getRepositorioUsuario() {
    	return this.userRepository;
    }
    /**
     * Metodo para el login de un usuario
     * @param request
     * @return AuthResponse
     */
	public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails user=userRepository.findByNomUsr(request.getUsername()).orElseThrow();
        String token=jwtService.getToken(user);
        return new AuthResponse(token);

    }
	/**
     * Metodo para el registro de un usuario
     * @param request
     * @return AuthResponse
     */
    public AuthResponse register(RegisterRequest request) {
    	Usuario usr = new Usuario(request.getNomUsr(), passwordEncoder.encode( request.getPass()), 
    			request.getNombre(), request.getApellido(), request.getEmail(), request.getRol());
        

        userRepository.save(usr);
        
        return new AuthResponse(jwtService.getToken(usr));
        
        
    }

}