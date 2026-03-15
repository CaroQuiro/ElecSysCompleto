package co.edu.unbosque.ElecSys.autenticacionSeguridad.seguridadAut;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;

import java.util.Date;

/**
 * Utilidad para la gestión de JSON Web Tokens (JWT).
 * Se encarga de la creación, extracción de datos y validación de vigencia de los tokens.
 */
public class JwtUtil {

    private static final String SECRET = "LaSeguridadDeElecSysEsPrioridadParaVCElectricos";

    /**
     * Genera la llave de firma a partir del secreto definido.
     */
    private static SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Crea un nuevo token JWT con una validez de 1 hora para un usuario específico.
     */
    public static String generarToken(String correo) {
        long diezHorasEnMilis = 36000000;

        return Jwts.builder()
                .setSubject(correo)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + diezHorasEnMilis)) // 1 hora
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extrae el correo electrónico (subject) contenido dentro del token.
     */
    public static String extraerCorreo(String token) {
        return obtenerClaims(token).getSubject();
    }

    /**
     * Verifica que el token sea íntegro, no haya expirado y esté firmado correctamente.
     */
    public static boolean esTokenValido(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            System.out.println("Error validando token: " + e.getMessage());
            return false;
        }
    }

    /**
     * Desglosa el token para obtener sus declaraciones (claims).
     */
    private static Claims obtenerClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
