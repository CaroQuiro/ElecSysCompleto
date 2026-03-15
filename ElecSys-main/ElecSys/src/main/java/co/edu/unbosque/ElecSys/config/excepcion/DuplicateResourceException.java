package co.edu.unbosque.ElecSys.config.excepcion;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
