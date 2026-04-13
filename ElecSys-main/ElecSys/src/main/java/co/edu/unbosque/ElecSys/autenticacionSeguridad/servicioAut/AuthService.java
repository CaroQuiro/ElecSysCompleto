package co.edu.unbosque.ElecSys.autenticacionSeguridad.servicioAut;

import co.edu.unbosque.ElecSys.autenticacionSeguridad.dtoAut.LoginRequestDTO;
import co.edu.unbosque.ElecSys.autenticacionSeguridad.dtoAut.LoginResponseDTO;
import co.edu.unbosque.ElecSys.autenticacionSeguridad.dtoAut.VerificacionCodigoDTO;
import co.edu.unbosque.ElecSys.autenticacionSeguridad.seguridadAut.JwtUtil;
import co.edu.unbosque.ElecSys.notificacion.envioEmail.ConfiguracionEmail;
import co.edu.unbosque.ElecSys.usuario.trabajador.entidadTra.TrabajadorEntidad;
import co.edu.unbosque.ElecSys.usuario.trabajador.servicioTra.TrabajadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Servicio encargado de la lógica de negocio para la autenticación.
 * Gestiona la validación de usuarios, generación de códigos temporales y envío de correos.
 */
@Service
public class AuthService {

    @Autowired
    private TrabajadorRepository trabajadorRepository;

    @Autowired
    private ConfiguracionEmail configuracionEmail;

    // Almacena temporalmente los códigos generados vinculados al correo del usuario
    private final Map<String, String> codigosTemporales = new HashMap<>();

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Valida la existencia y estado del usuario, verifica la contraseña y
     * envía un código de seguridad por correo electrónico si todo es correcto.
     * @param dto Credenciales de acceso.
     * @return String con el resultado de la operación.
     */
    public String login(LoginRequestDTO dto) {
        String correoLimpio = dto.getCorreo().trim().toLowerCase();
        TrabajadorEntidad trabajador = trabajadorRepository.findByCorreo(correoLimpio).orElse(null);

        //System.out.println(new BCryptPasswordEncoder().encode("JimmyDavidDeicy"));

        if (trabajador == null) return "Usuario no encontrado";

        if (trabajador.getEstado() == null || !trabajador.getEstado().equalsIgnoreCase("ACTIVO")) {
            return "Acceso denegado: Tu cuenta no se encuentra ACTIVA. Contacta al administrador.";
        }
        if (!encoder.matches(dto.getPassword(), trabajador.getPassword()))
            return "Contraseña incorrecta";

        String codigo = generarCodigo();
        codigosTemporales.put(correoLimpio, codigo);

        String cuerpoHtml = generarPlantillaHtml(trabajador.getNombre(), codigo);

        configuracionEmail.crearEmail(
                trabajador.getNombre(),
                correoLimpio,
                "Código de Acceso de Seguridad - ElecSys",
                cuerpoHtml,
                null
        );

        configuracionEmail.envioEmail();

        return "Código enviado al correo";
    }

    /**
     * Compara el código ingresado por el usuario con el almacenado en memoria.
     * Si coincide, genera un token JWT para autorizar las siguientes peticiones.
     * @param dto Correo y código de verificación.
     * @return Información de sesión y token de acceso.
     */
    public LoginResponseDTO verificarCodigo(VerificacionCodigoDTO dto) {
        String correoLimpio = dto.getCorreo().trim().toLowerCase();
        String codigoIngresado = dto.getCodigo().trim();

        String codigoGuardado = codigosTemporales.get(correoLimpio);

        if (codigoGuardado == null) {
            throw new RuntimeException("No hay código generado para este correo.");
        }

        if (!codigoGuardado.equals(codigoIngresado)) {
            throw new RuntimeException("Código incorrecto.");
        }

        codigosTemporales.remove(correoLimpio);
        TrabajadorEntidad trabajador = trabajadorRepository.findByCorreo(correoLimpio).orElse(null);
        String token = JwtUtil.generarToken(correoLimpio, trabajador.getTipo_usuario());

        return new LoginResponseDTO(token, trabajador.getId_trabajador(),trabajador.getNombre(),
                trabajador.getTipo_usuario(), "Acceso concedido");
    }

    /**
     * Genera un número aleatorio de 6 dígitos para la verificación en dos pasos.
     */
    private String generarCodigo() {
        return String.valueOf(
                (int)(Math.random() * 900000) + 100000
        );
    }


    /**
     * Construye la estructura HTML del correo electrónico utilizando un diseño
     * profesional acorde a la identidad de VC Eléctricos Construcciones SAS.
     */
    private String generarPlantillaHtml(String nombre, String codigo) {
        return """
        <div style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
          <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.1);">
            <div style="background-color: #b80000; padding: 30px; text-align: center;">
              <h1 style="color: #ffffff; margin: 0; font-size: 24px;">ElecSys</h1>
              <p style="color: #ffffff; margin: 5px 0 0; opacity: 0.8;">VC Eléctricos Construcciones SAS</p>
            </div>
            <div style="padding: 40px; text-align: center; color: #333333;">
              <h2 style="margin-top: 0;">¡Hola, %s!</h2>
              <p style="font-size: 16px; line-height: 1.6;">Has solicitado un código de acceso para ingresar al sistema administrativo. Por favor, utiliza el siguiente código de verificación:</p>
              <div style="margin: 30px 0; padding: 20px; background-color: #f9f9f9; border: 2px dashed #b80000; border-radius: 10px; display: inline-block;">
                <span style="font-size: 32px; font-weight: bold; color: #b80000; letter-spacing: 5px;">%s</span>
              </div>
              <p style="font-size: 14px; color: #777777;">Este código expirará en 1 hora. Si no solicitaste este acceso, por favor ignora este correo.</p>
            </div>
            <div style="background-color: #f4f4f4; padding: 20px; text-align: center; font-size: 12px; color: #999999;">
              <p style="margin: 0;">&copy; 2026 VC Eléctricos Construcciones S.A.S. | Todos los derechos reservados.</p>
              <p style="margin: 5px 0;">Bogotá, Colombia</p>
            </div>
          </div>
        </div>
        """.formatted(nombre, codigo);
    }
}

