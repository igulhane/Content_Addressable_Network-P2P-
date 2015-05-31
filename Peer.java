
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Interface for the client node
 */

public interface Peer extends Remote{
	public boolean AddNeighbours(Neighbours node)throws RemoteException;
	public ArrayList<Neighbours> getNeighbours() throws RemoteException;
	public void setCoordinates(double x1, double x2, double y1, double y2, String ipAddress)throws RemoteException;
	public int gethashX(String string)throws RemoteException;
	public int gethashY(String string)throws RemoteException;
	public void RemoveNeighbours(Neighbours node)throws RemoteException;
	public void route(int x, int y, String ip)throws RemoteException;
	public void split(int x, int y, String ip)throws RemoteException;
	public void initializeGrid()throws RemoteException;
	public double getLx() throws RemoteException;
	public void setLx(double lx) throws RemoteException;
	public double getLy() throws RemoteException;
	public void setLy(double ly) throws RemoteException;
	public double getHx() throws RemoteException;
	public void setHx(double hx) throws RemoteException;
	public double getHy() throws RemoteException;
	public void setHy(double hy) throws RemoteException;
	public double getMidx() throws RemoteException;
	public void setMidx(double midx) throws RemoteException;
	public double getMidy() throws RemoteException;
	public void setMidy(double midy) throws RemoteException;
	public String getIp() throws RemoteException;
	public void setIp(String ip) throws RemoteException;
	public void setLocation(String ip) throws RemoteException;
	public void setNeighBours(ArrayList<Neighbours> n) throws RemoteException;
	public void search(int option,int x, int y, String fileName, String ipAddress) throws RemoteException;
	public void print(String s) throws RemoteException;
	public void ReceiveFile(String ip, String fileName) throws RemoteException;
	public byte[] downloadFile(String filename)throws RemoteException;
	public boolean insideArea(Files files)throws RemoteException;
	public void removeFiles(Files F) throws RemoteException;
	public void addFiles(Files F) throws RemoteException;
	public boolean leave() throws RemoteException;
	
}
