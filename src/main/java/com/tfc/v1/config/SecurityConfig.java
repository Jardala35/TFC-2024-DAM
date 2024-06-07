package com.tfc.v1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.tfc.v1.auth.jwt.JwtAuthenticationFilter;
/**
 * Clase de configuración para la seguridad web de la aplicación.
 * 
 * 
 * @author Pablo Navarro Duro 
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	@Autowired
    private AuthenticationProvider authProvider;
    
    

    public SecurityConfig() {
		super();
	}


    /**
     * Configura la cadena de filtros de seguridad HTTP.
     * 
     * <p>Deshabilita CSRF, permite el acceso sin autenticación a las rutas bajo "/auth/**",
     * requiere autenticación para cualquier otra solicitud, establece la política de sesiones
     * como stateless, y agrega el filtro de autenticación JWT antes del filtro de autenticación
     * de nombre de usuario y contraseña.</p>
     * 
     * @param http La configuración de seguridad HTTP.
     * @return La cadena de filtros de seguridad configurada.
     * @throws Exception Si ocurre un error al configurar la cadena de filtros.
     */
	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        return http
            .csrf(csrf -> 
                csrf
                .disable())
            .authorizeHttpRequests(authRequest ->
              authRequest
                .requestMatchers("/auth/**").permitAll()
                .anyRequest().authenticated()
                )
            .sessionManagement(sessionManager->
                sessionManager 
                  .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
            
            
    }
}
