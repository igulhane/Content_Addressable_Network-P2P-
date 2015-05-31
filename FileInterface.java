import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileInterface extends Remote {
   public byte[] transfer(String fileName) throws  RemoteException;
}