package co.edu.unbosque.ElecSys.autenticacionSeguridad.controladorAut;

import co.edu.unbosque.ElecSys.autenticacionSeguridad.dtoAut.LoginRequestDTO;
import co.edu.unbosque.ElecSys.autenticacionSeguridad.dtoAut.LoginResponseDTO;
import co.edu.unbosque.ElecSys.autenticacionSeguridad.dtoAut.VerificacionCodigoDTO;
import co.edu.unbosque.ElecSys.autenticacionSeguridad.servicioAut.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST que gestiona los endpoints de autenticación.
 * Expone las rutas para el inicio de sesión y la verificación de códigos de seguridad.
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/aut")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Endpoint para el login inicial. Valida credenciales y dispara el envío del código.
     * @param dto Objeto con el correo y contraseña del usuario.
     * @return Mensaje de estado (éxito o error).
     */
    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDTO dto) {
        return authService.login(dto);
    }


    /**
     * Endpoint para validar el código de 6 dígitos enviado por correo.
     * @param dto Objeto con el correo y el código ingresado.
     * @return DTO con el token JWT e información básica del trabajador.
     */
    @PostMapping("/verificar")
    public LoginResponseDTO verificar(@RequestBody VerificacionCodigoDTO dto) {
        return authService.verificarCodigo(dto);
    }
}

