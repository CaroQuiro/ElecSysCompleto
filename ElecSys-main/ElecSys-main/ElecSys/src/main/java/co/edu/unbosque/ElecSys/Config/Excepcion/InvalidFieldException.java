package co.edu.unbosque.ElecSys.Config.Excepcion;

public class InvalidFieldException extends RuntimeException {
    public InvalidFieldException(String message) {
        super(message);
    }
}
