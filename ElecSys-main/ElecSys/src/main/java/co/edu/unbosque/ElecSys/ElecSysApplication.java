package co.edu.unbosque.ElecSys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Punto de entrada principal de la aplicación ElecSys.
 * Esta clase inicializa el contexto de Spring Boot y habilita las tareas programadas.
 * * @author Equipo de Desarrollo ElecSys - VC Eléctricos Construcciones S.A.S.
 * @version 4.0
 * @date 2025-10-10
 */
@SpringBootApplication
@EnableScheduling
public class ElecSysApplication {

    /**
     * Método de inicio que arranca la ejecución de la aplicación.
     * @param args Argumentos de línea de comandos.
     */
	public static void main(String[] args) {
		SpringApplication.run(ElecSysApplication.class, args);
	}

}
