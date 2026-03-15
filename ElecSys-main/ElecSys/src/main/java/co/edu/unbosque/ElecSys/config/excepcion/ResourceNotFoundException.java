package co.edu.unbosque.ElecSys.config.excepcion;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
