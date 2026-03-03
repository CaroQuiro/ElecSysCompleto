package co.edu.unbosque.ElecSys.Config.Excepcion;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
