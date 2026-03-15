package co.edu.unbosque.ElecSys.notificacion.envioEmail;


import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Properties;

/**
 * Componente encargado de la configuración técnica y envío de correos electrónicos mediante el protocolo SMTP.
 * Utiliza las librerías de Jakarta Mail para gestionar sesiones, autenticación y contenido multimedia.
 */
@Component
public class ConfiguracionEmail {

    private String email;
    private String contrasena;
    private Properties mProperties;
    private Session mSession;
    private MimeMessage mCorreo;

    /**
     * Constructor que inicializa las credenciales de envío y las propiedades base del sistema.
     */
    public ConfiguracionEmail() {
        this.email = "ao818872@gmail.com";
        this.contrasena = "kuldvbpkgeuyvwfb";
        this.mProperties = new Properties();
    }

    /**
     * Prepara el mensaje de correo configurando los parámetros SMTP, el contenido HTML y los adjuntos.
     * @param nombreUsuario Nombre para mostrar en el campo 'From'.
     * @param correo Dirección del destinatario.
     * @param asunto Título del correo.
     * @param mensaje Contenido en formato HTML.
     * @param archivos Archivo adjunto opcional procesado como MultipartFile.
     */
    public void crearEmail(String nombreUsuario, String correo, String asunto, String mensaje, MultipartFile archivos) {
        mProperties.put("mail.smtp.host", "smtp.gmail.com");
        mProperties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        mProperties.setProperty("mail.smtp.starttls.enable", "true");
        mProperties.setProperty("mail.smtp.port", "587");
        mProperties.setProperty("mail.smtp.user", email);
        mProperties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        mProperties.setProperty("mail.smtp.auth", "true");

        mSession = Session.getInstance(mProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, contrasena);
            }
        });

        try {
            MimeMultipart multipart = new MimeMultipart();

            MimeBodyPart cuerpo = new MimeBodyPart();
            cuerpo.setContent(mensaje, "text/html; charset=utf-8");
            multipart.addBodyPart(cuerpo);

            if (archivos != null && !archivos.isEmpty()) {

                MimeBodyPart adjunto = new MimeBodyPart();

                File archivo = File.createTempFile("adjunto", archivos.getOriginalFilename());
                archivos.transferTo(archivo);

                adjunto.setDataHandler(new DataHandler(new FileDataSource(archivo)));
                adjunto.setFileName(archivos.getOriginalFilename());

                multipart.addBodyPart(adjunto);
            }


            mCorreo = new MimeMessage(mSession);
            mCorreo.setFrom(new InternetAddress(email, nombreUsuario));
            mCorreo.setRecipient(Message.RecipientType.TO, new InternetAddress(correo));
            mCorreo.setSubject(asunto, "UTF-8");
            mCorreo.setContent(multipart);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Ejecuta el transporte y envío real del correo previamente construido.
     * @return Mensaje de éxito del envío o descripción del error en caso de fallo.
     */
    public String envioEmail() {
        try (Transport mTransport = mSession.getTransport("smtp")) {
            mTransport.connect(email, contrasena);
            mTransport.sendMessage(mCorreo, mCorreo.getAllRecipients());
            return "Correo enviado exitosamente.";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "Error al enviar el correo: " + ex.getMessage();
        }
    }
}