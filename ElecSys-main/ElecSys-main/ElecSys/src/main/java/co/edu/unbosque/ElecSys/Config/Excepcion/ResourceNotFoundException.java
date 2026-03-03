package co.edu.unbosque.ElecSys.Config.Excepcion;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
