package com.tfc.v1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tfc.v1.modelo.persistencia.RepositorioUsuario;

/**
 * Clase de configuración para la autenticación y seguridad de la aplicación.
 * 
 * <p>Esta clase configura los componentes necesarios para la autenticación
 * y autorización de usuarios en la aplicación Spring.</p>
 * 
 * @author Pablo Navarro Duro 
 */
@Configuration
public class ApplicationConfig {

	@Autowired
	private RepositorioUsuario repoUsr;

	public ApplicationConfig() {
		super();
	}

	/**
	 * Proporciona el AuthenticationManager para la gestión de autenticaciones.
	 * 
	 * @param config La configuración de autenticación.
	 * @return El AuthenticationManager configurado.
	 * @throws Exception Si ocurre un error al obtener el AuthenticationManager.
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	/**
	 * Proporciona el AuthenticationProvider para la autenticación basada en DAO.
	 * 
	 * @return El DaoAuthenticationProvider configurado.
	 */
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	/**
	 * Proporciona el PasswordEncoder para codificar las contraseñas.
	 * 
	 * @return El BCryptPasswordEncoder.
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Proporciona el UserDetailsService para cargar los detalles del usuario.
	 * 
	 * @return El UserDetailsService configurado.
	 * @throws UsernameNotFoundException Si el usuario no es encontrado.
	 */
	@Bean
	public UserDetailsService userDetailService() {
		return username -> repoUsr.findByNomUsr(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not fournd"));
	}

}
