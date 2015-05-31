import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Implementation of BootStrap server.
 */
public class BootStrapServer implements Bootstrap, Serializable {
	int lx, hx, ly, hy;
	boolean hasNeighbours;
	String nodeIp;

	protected BootStrapServer() throws RemoteException {
		super();
		lx = 0;
		hx = 0;
		ly = 0;
		hy = 100;
		hasNeighbours = false;
	}

	public static void main(String[] args) throws IOException {
		try {
			BootStrapServer server = new BootStrapServer();

			Bootstrap bootstrap = (Bootstrap) UnicastRemoteObject.exportObject(server, 8000);
			Registry reg = LocateRegistry.createRegistry(8000);
			reg.rebind("server", server);
			System.out.println();
			//System.out.println(InetAddress.getLocalHost().getHostAddress());
			System.out.println("BootStrap Server Started");
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Stores the ip address of first node in the network 
	 */
	@Override
	public void BootStrapNode(String ip) throws RemoteException {
		nodeIp = ip;
		hasNeighbours = true;
	}
	
	/**
	 * Removes the stored ip address
	 */
	public void removeBootStrapNode() throws RemoteException{
		nodeIp=null;
		hasNeighbours = false;
	
	}
	
	/**
	 * @return Ip address of the first node in the network.
	 */
	@Override
	public String getBootStrapNode() throws RemoteException {
		return nodeIp;
	}

}
