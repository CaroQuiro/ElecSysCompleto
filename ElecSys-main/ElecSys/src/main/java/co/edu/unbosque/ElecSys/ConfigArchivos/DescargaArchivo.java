package co.edu.unbosque.ElecSys.ConfigArchivos;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

@Service
public class DescargaArchivo {

    public String guardarArchivo(byte[] archivo, String nombreArchivo, String tipoCarpeta, String prefijo) throws IOException {
        String carpDocumentos = System.getProperty("user.home") + File.separator + "Documents";

        Path carpBase = Paths.get(carpDocumentos, "VC ELECTRICOS CONSTRUCCIONES");

        Path carpTipo = carpBase.resolve(tipoCarpeta);

        String año = String.valueOf(LocalDate.now().getYear());
        Path rutafinal = carpTipo.resolve(prefijo + "-" + año);

        Files.createDirectories(rutafinal);

        Path rutaArchivo = rutafinal.resolve(nombreArchivo);

        Files.write(rutaArchivo, archivo);

        System.out.println("Archivo guardado en " + rutaArchivo.toAbsolutePath());

        return nombreArchivo;
    }

}
