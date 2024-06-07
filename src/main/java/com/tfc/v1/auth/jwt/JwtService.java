package com.tfc.v1.auth.jwt;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Servicio para manejar operaciones relacionadas con JSON Web Tokens (JWT).
 * Proporciona métodos para generar, extraer y validar tokens.
 * 
 * @autor Pablo Navarro Duro 
 */
@Service
public class JwtService {

    private static final String SECRET_KEY="586E3272357538782F413F4428472B4B6250655368566B597033733676397924";
    /**
     * Genera un token JWT para el usuario proporcionado.
     * 
     * @param user Detalles del usuario para el cual se generará el token.
     * @return El token JWT generado.
     */
    public String getToken(UserDetails user) {
        return getToken(new HashMap<>(), user);
    }
    /**
     * Genera un token JWT con reclamaciones adicionales para el usuario proporcionado.
     * 
     * @param extraClaims Mapa de reclamaciones adicionales.
     * @param user Detalles del usuario para el cual se generará el token.
     * @return El token JWT generado.
     */
    private String getToken(Map<String,Object> extraClaims, UserDetails user) {
        return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(user.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis()+1000*60*24))
            .signWith(getKey(), SignatureAlgorithm.HS256)
            .compact();
    }
    /**
     * Obtiene la clave secreta utilizada para firmar el token JWT.
     * 
     * @return La clave secreta.
     */
    private Key getKey() {
       byte[] keyBytes=Decoders.BASE64.decode(SECRET_KEY);
       return Keys.hmacShaKeyFor(keyBytes);
    }
    /**
     * Extrae el nombre de usuario del token JWT.
     * 
     * @param token El token JWT del cual se extraerá el nombre de usuario.
     * @return El nombre de usuario extraído del token.
     */
    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }
    /**
     * Valida si el token JWT es válido para el usuario proporcionado.
     * 
     * @param token El token JWT a validar.
     * @param userDetails Detalles del usuario contra el cual se validará el token.
     * @return Verdadero si el token es válido, falso en caso contrario.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username=getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername())&& !isTokenExpired(token));
    }
    /**
     * Extrae todas las reclamaciones del token JWT.
     * 
     * @param token El token JWT del cual se extraerán las reclamaciones.
     * @return Las reclamaciones extraídas del token.
     */
    private Claims getAllClaims(String token)
    {
        return Jwts
            .parserBuilder()
            .setSigningKey(getKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
    /**
     * Extrae una reclamación específica del token JWT.
     * 
     * @param <T> El tipo de la reclamación.
     * @param token El token JWT del cual se extraerá la reclamación.
     * @param claimsResolver Función para resolver la reclamación.
     * @return La reclamación extraída del token.
     */
    public <T> T getClaim(String token, Function<Claims,T> claimsResolver)
    {
        final Claims claims=getAllClaims(token);
        return claimsResolver.apply(claims);
    }
    /**
     * Obtiene la fecha de expiración del token JWT.
     * 
     * @param token El token JWT del cual se extraerá la fecha de expiración.
     * @return La fecha de expiración del token.
     */
    private Date getExpiration(String token)
    {
        return getClaim(token, Claims::getExpiration);
    }
    /**
     * Verifica si el token JWT ha expirado.
     * 
     * @param token El token JWT a verificar.
     * @return Verdadero si el token ha expirado, falso en caso contrario.
     */
    private boolean isTokenExpired(String token)
    {
        return getExpiration(token).before(new Date());
    }
}
