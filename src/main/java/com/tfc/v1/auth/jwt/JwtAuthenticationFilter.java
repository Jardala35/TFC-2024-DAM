package com.tfc.v1.auth.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
/**
 * Filtro de autenticación JWT que se ejecuta una vez por solicitud. Intercepta las solicitudes HTTP
 * para extraer y validar un token JWT del encabezado de autorización y configura la autenticación
 * en el contexto de seguridad de Spring Security.
 * 
 * @author Pablo Navarro Duro 
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{
	@Autowired
	    private JwtService jwtService;
	@Autowired
	    private UserDetailsService userDetailsService;
		/**
	     * Constructor para inyectar las dependencias necesarias.
	     * 
	     * @param jwtService Servicio para manejar operaciones relacionadas con JWT.
	     * @param userDetailsService Servicio para cargar detalles del usuario.
	     */
	    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
			super();
			this.jwtService = jwtService;
			this.userDetailsService = userDetailsService;
		}
	    /**
	     * Método principal del filtro que se ejecuta en cada solicitud. Extrae el token JWT del encabezado
	     * de autorización, lo valida y configura la autenticación en el contexto de seguridad de Spring Security.
	     * 
	     * @param request  La solicitud HTTP.
	     * @param response La respuesta HTTP.
	     * @param filterChain La cadena de filtros.
	     * @throws ServletException Si ocurre un error de servlet.
	     * @throws IOException Si ocurre un error de entrada/salida.
	     */
		protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	            throws ServletException, IOException, java.io.IOException {
	       
	        final String token = getTokenFromRequest(request);
	        final String username;

	        if (token==null)
	        {
	            filterChain.doFilter(request, response);
	            return;
	        }

	        username=jwtService.getUsernameFromToken(token);

	        if (username!=null && SecurityContextHolder.getContext().getAuthentication()==null)
	        {
	            UserDetails userDetails=userDetailsService.loadUserByUsername(username);

	            if (jwtService.isTokenValid(token, userDetails))
	            {
	                UsernamePasswordAuthenticationToken authToken= new UsernamePasswordAuthenticationToken(
	                    userDetails,
	                    null,
	                    userDetails.getAuthorities());

	                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

	                SecurityContextHolder.getContext().setAuthentication(authToken);
	            }

	        }
	        
	        filterChain.doFilter(request, response);
	    }
		 /**
	     * Extrae el token JWT del encabezado de autorización de la solicitud.
	     * 
	     * @param request La solicitud HTTP.
	     * @return El token JWT si está presente y es válido, de lo contrario, null.
	     */
	    private String getTokenFromRequest(HttpServletRequest request) {
	        final String authHeader=request.getHeader(HttpHeaders.AUTHORIZATION);

	        if(StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer "))
	        {
	            return authHeader.substring(7);
	        }
	        return null;
	    }

}
