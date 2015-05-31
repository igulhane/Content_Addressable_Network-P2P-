import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface for the BootStrap Server
 */
public interface Bootstrap extends Remote{

	public void BootStrapNode(String ip) throws RemoteException;
	public void removeBootStrapNode() throws RemoteException;
	public String getBootStrapNode() throws RemoteException;
}
