/**
 * 
 * @author Ishan Gulhane
 *
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
/**
 * 
 * CANNode : Implements the Peer and FileInterface
 */
public class CANNode implements  Peer, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static double lx, ly, hx, hy;
	static double midx, midy;
	static String ip;
	static ArrayList<Neighbours> neighbours;
	static ArrayList<Files> files;

	String location;
	/**
	 * Initialize the Node
	 */
	public CANNode() throws RemoteException {
		try {
			lx = 0;
			ly = 0;
			hx = 0;
			hy = 0;
			midx = 0;
			midy = 0;
			ip = InetAddress.getLocalHost().getHostAddress();
			location = ip;
			neighbours = new ArrayList<Neighbours>();
			files = new ArrayList<Files>();
		} catch (UnknownHostException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Set the coordinates of Node 
	 */
	public void setCoordinates(double x1, double x2, double y1, double y2,
			String ipAddress) throws RemoteException {
		lx = x1;
		ly = y1;
		hx = x2;
		hy = y2;
		midx = (lx + hx) / 2;
		midy = (ly + hy) / 2;
		ip = ipAddress;
	}
	
	/**
	 * Returns x-coordinate for the given string 
	 */
	public int gethashX(String string) throws RemoteException {
		char ch[];
		ch = string.toCharArray();
		int i, sum;
		for (sum = 0, i = 0; i < string.length(); i++)
			sum += ch[i];
		sum = (sum * 10) % 99;
		if (sum == 0) {
			return 10;
		} else {
			return sum;
		}

	}
	
	/**
	 * Returns the y-coordinate for the given string 
	 */
	public int gethashY(String string) {
		char ch[];
		ch = string.toCharArray();
		int i, sum;
		for (sum = 0, i = 0; i < string.length(); i++)
			sum += ch[i];
		sum = (sum * 30) % 99;
		if (sum == 0) {
			return 10;
		} else {
			return sum;
		}
	}

	/**
	 * Add files to the given node
	 */
	public void addFiles(Files F) throws RemoteException {
		boolean hasFile = false;
		/**
		 * For checks, if the file is already present
		 */
		for (int i = 0; i < this.files.size(); i++) {
			if (files.get(i).name.equals(F.name)) {
				hasFile = true;
				break;
			}
		}
		if (!hasFile) {
			this.files.add(F);
		}
	}

	/**
	 * Removes the file from the Node list
	 */
	public void removeFiles(Files F) throws RemoteException {
		ArrayList<Files> n = this.getFiles();
		for (int i = 0; i < n.size(); i++) {
			if (n.get(i).name.equals(F.name)) {
				//System.out.println("File removed successfully....");
				this.files.remove(n.get(i));
			}
		}
	}
	
	/**
	 * Adds new neighbour to the list
	 */
	public boolean AddNeighbours(Neighbours node) throws RemoteException {
		boolean has = false;
		for (int i = 0; i < this.neighbours.size(); i++) {
			if (this.neighbours.get(i).ip.equals(node.ip)) {
				has = true;
				break;
			}
		}
		if (!has) {
			this.neighbours.add(node);
		}
		return has;
	}
	
	/**
	 * Removes the node from the neighbour list 
	 */
	
	public void RemoveNeighbours(Neighbours node) throws RemoteException {
		ArrayList<Neighbours> n = this.getNeighbours();
		for (int i = 0; i < n.size(); i++) {
			if (n.get(i).ip.equals(node.ip)) {
				//System.out.println("removed successfully....");
				this.neighbours.remove(n.get(i));
			}
		}
	}
	
	/**
	 * Initiate the transfer for receiving the file
	 */
	public void ReceiveFile(String s, String filename) throws RemoteException {
		try {
			//Get the remote of source object
			Registry registry = LocateRegistry.getRegistry(s, 8000);
			Peer peer = (Peer) registry.lookup("peerNode");
			byte[] filedata = peer.downloadFile(filename);
			File file = new File(filename);
			BufferedOutputStream output;
			try {
				output = new BufferedOutputStream(new FileOutputStream(file.getName()));
				output.write(filedata, 0, filedata.length);
				output.flush();
				output.close();
				System.out.println("File Received");
			} catch (IOException e) {

				e.printStackTrace();
			}

		} catch (NotBoundException e) {
			System.out.println("error in receive transfer");
			e.printStackTrace();
		}
	}
	
	/**
	 * Download the required file
	 */
	public byte[] downloadFile(String fileName) throws RemoteException {
		try {
			File file = new File(fileName);
			byte buffer[] = new byte[(int) file.length()];
			BufferedInputStream input = new BufferedInputStream(new FileInputStream(fileName));
			input.read(buffer, 0, buffer.length);
			input.close();
			return (buffer);
		} catch (Exception e) {
			System.out.println("FileImpl: " + e.getMessage());
			e.printStackTrace();
			return (null);
		}
	}

	/**
	 * Search for the coordinates of the file
	 */
	public void search(int option, int x, int y, String fileName,String ipAddress) throws RemoteException {
		if (this.lx <= x && this.hx >= x && this.ly <= y && this.hy >= y) {
			//option 2 : For searching the file in network
			if (option == 2) {
				boolean contains = false;
				//For checks the presence of File in current list of files
				for (int i = 0; i < this.files.size(); i++) {
					if (this.files.get(i).name.equals(fileName)) {
						contains = true;
						break;
					}
				}
				//if checks the presence of the file in the network
				if (contains) {
					System.out.println("Transfering files to "+ipAddress);
					Registry registry = LocateRegistry.getRegistry(ipAddress,8000);
					try {
						Peer canNode = (Peer) registry.lookup("peerNode");
						canNode.print("Receiving file from :" + this.getIp());
						canNode.ReceiveFile(this.getIp(), fileName);
					} catch (NotBoundException e) {
						e.printStackTrace();
					}

				} else {
					Registry registry = LocateRegistry.getRegistry(ipAddress,8000);
					try {
						Peer canNode = (Peer) registry.lookup("peerNode");
						canNode.print("File Not present in network");
					} catch (NotBoundException e) {
						e.printStackTrace();
					}
				}

			} else {// Option =3 . For Inserting the file in the network

				Files file = new Files();
				file.setName(fileName);
				file.setX(x);
				file.setY(y);
				this.addFiles(file);
				//System.out.println("Receiving file from :" + ipAddress);
				this.ReceiveFile(ipAddress, fileName);
				Registry registry = LocateRegistry.getRegistry(ipAddress, 8000);
				try {
					Peer canNode = (Peer) registry.lookup("peerNode");
					canNode.print("File inserted on " + this.ip);
				} catch (NotBoundException e) {
					e.printStackTrace();
				}
			}

		} else {// Routing to neighbours for file search

			//System.out.println("Calling neighbours");
			//System.out.println("number of neighbours :" + neighbours.size());
			double dist = 0;
			double minDist = 0;
			Neighbours node = this.neighbours.get(0);
			minDist = Math.pow((node.midx - x), 2)+ Math.pow((node.midy - y), 2);
			minDist = Math.sqrt(minDist);
			for (int i = 1; i < neighbours.size(); i++) {
				dist = Math.pow((this.neighbours.get(i).midx - x), 2)
						+ Math.pow((this.neighbours.get(i).midy - y), 2);
				dist = Math.sqrt(dist);
				if (dist < minDist) {
					minDist = dist;
					node = this.neighbours.get(i);
				}
			}
			try {
				//Routing to the closest neighbour
				Registry registry = LocateRegistry.getRegistry(node.ip, 8000);
				Peer canNode = (Peer) registry.lookup("peerNode");
				canNode.search(option, x, y, fileName, ipAddress);
			} catch (NotBoundException e) {
				System.out.println("Error in search...");
				System.out.println(e.getMessage());

			}
		}
	}

	/**
	 * Adds the node to the network
	 */
	public void join() throws RemoteException {
		try {
			//Call to Bootstrap Server 
			Registry reg = LocateRegistry.getRegistry("129.21.30.38", 8000);
			Bootstrap obj = (Bootstrap) reg.lookup("server");
			String bootStrapIp = obj.getBootStrapNode();
			//System.out.println("bootStrapIp :" + (bootStrapIp));
			//System.out.println("Starting join....");
			
			//If checks the presence of nodes in the network
			if (bootStrapIp == null) {
				//Add the first node in the network and update the bootstrap server
				this.initializeGrid();
				obj.BootStrapNode(this.ip);
				Peer bootstrap = (Peer) UnicastRemoteObject.exportObject(this,8000);
				Registry reg1 = LocateRegistry.createRegistry(8000);
				reg1.rebind("peerNode", this);
			} else {
				Registry reg2 = LocateRegistry.getRegistry(bootStrapIp, 8000);
				Peer canNode = (Peer) reg2.lookup("peerNode");
				Random ran = new Random();
				int x = ran.nextInt(100);
				int y = ran.nextInt(100);
				//System.out.println("x " + x + " y " + y);
				//System.out.println("Adding : " + this.ip);
				Peer bootstrap = (Peer) UnicastRemoteObject.exportObject(this,8000);
				Registry reg1 = LocateRegistry.createRegistry(8000);
				reg1.rebind("peerNode", this);
				
				//Routing the for adding the node
				canNode.route(x, y, this.ip);
			}
		} catch (Exception e) {
			System.out.println("Error while joining ");
			System.out.println(e.getMessage());
			// e.printStackTrace();
		}
	}

	/**
	 * Route finds the correct region for inserting the node
	 */
	public void route(int x, int y, String ip) throws RemoteException {
		//Base case to check the correct region
		if (this.lx <= x && this.hx >= x && this.ly <= y && this.hy >= y) {
			this.split(x, y, ip);
		} else {
			//Find the closest neighbour 
			double dist = 0;
			double minDist = 0;
			Neighbours node = this.neighbours.get(0);
			minDist = Math.sqrt(Math.pow((node.midx - x), 2)+ Math.pow((node.midy - y), 2));

			for (int i = 1; i < neighbours.size(); i++) {
				dist = Math.sqrt( Math.pow((this.neighbours.get(i).midx - x), 2)
						+ Math.pow((this.neighbours.get(i).midy - y), 2));
				if (dist < minDist) {
					minDist = dist;
					node = this.neighbours.get(i);
				}
			}
			try {
				//Routing to closest neighbour
				Registry registry = LocateRegistry.getRegistry(node.ip, 8000);
				Peer canNode = (Peer) registry.lookup("peerNode");
				canNode.route(x, y, ip);
			} catch (NotBoundException e) {
				System.out.println("Error in recursive route call...");
				System.out.println(e.getMessage());

			}
		}
	}
	
	/**
	 * Check if the coordinates of file are present in current area.
	 */
	public boolean insideArea(Files files) throws RemoteException {
		if (files.x >= this.lx && files.x <= this.hx && files.y >= this.ly
				&& files.y <= this.hy) {
			return true;
		}
		return false;
	}

	/**
	 * Split the current zone to add the new node
	 */
	public void split(int x, int y, String ipAddress) throws RemoteException {
		{
			Neighbours c = new Neighbours();
			Neighbours n = new Neighbours();
			try {
				Registry registry = LocateRegistry.getRegistry(ipAddress, 8000);
				Peer node = (Peer) registry.lookup("peerNode");
				
				//Check the length and breadth of current zone and split along the larger part.
				if (Math.abs(this.getHx() - this.getLx()) >= Math.abs(this
						.getHy() - this.getLy())) {
					//Splitting along the x-axis
					double a = ((this.getHx() + this.getLx()) / 2);
					node.setCoordinates(a, hx, ly, hy, ipAddress);
					n.setCoordinates(a, hx, ly, hy, ipAddress);
					hx = (a);
					midx = (hx + lx) / 2;
					midy = (hy + ly) / 2;
					c.setCoordinates(lx, a, ly, hy, ip);
					/*System.out.println("Stting old co-ordinates : " + lx + " "
							+ a + " " + ly + " " + hy + " " + ip);
					System.out.println("Adding along x....");*/
					ArrayList<Neighbours> oldList = new ArrayList<Neighbours>();
					ArrayList<Neighbours> newList = new ArrayList<Neighbours>();
					ArrayList<Neighbours> arrayList = this.neighbours;
					
					//Transferring the files from the old zone to new zone
					if (files.size() != 0) {
						ArrayList<Files> list = files;
						for (int i = 0; i < list.size(); i++) {
							//Check if the file belongs to new zone
							if (node.insideArea(list.get(i))) {
								node.addFiles(list.get(i));
								node.ReceiveFile(this.ip, list.get(i).name);
								this.removeFiles(list.get(i));
							}
						}
					}
					// Update the neighbours of old and new zone
					if (arrayList.size() != 0) {

						for (int i = 0; i < arrayList.size(); i++) {

							try {
								Neighbours neighbours2 = arrayList.get(i);
								if ((neighbours2.lx <= node.getLx() && neighbours2.hx <= node.getLx())) {
									Registry reg1 = LocateRegistry.getRegistry(neighbours2.ip, 8000);
									Peer tempnode = (Peer) reg1.lookup("peerNode");
									ArrayList<Neighbours> al = tempnode.getNeighbours();
									oldList.add(neighbours2);
									for (int j = 0; j < al.size(); j++) {
										if (al.get(j).ip.equals(this.ip)) {
											tempnode.RemoveNeighbours(al.get(j));
										}
									}
									tempnode.AddNeighbours(c);
								} else if (neighbours2.lx >= node.getLx()
										&& neighbours2.hx >= node.getLx()) {
									newList.add(neighbours2);
									Registry reg1 = LocateRegistry.getRegistry(neighbours2.ip, 8000);
									Peer tempnode = (Peer) reg1.lookup("peerNode");
									ArrayList<Neighbours> al = tempnode.getNeighbours();
									for (int j = 0; j < al.size(); j++) {
										if (al.get(j).ip.equals(this.ip)) {
											tempnode.RemoveNeighbours(al.get(j));
										}
									}
									tempnode.AddNeighbours(n);

								} else if ((neighbours2.lx < node.getLx() && neighbours2.hx > node.getLx())
										&& (neighbours2.ly == node.getHy() || neighbours2.hy == node.getLy())) {
									Registry reg1 = LocateRegistry.getRegistry(neighbours2.ip, 8000);
									Peer tempnode = (Peer) reg1.lookup("peerNode");
									ArrayList<Neighbours> al = tempnode.getNeighbours();
									for (int j = 0; j < al.size(); j++) {
										if (al.get(j).ip.equals(this.ip)) {
											tempnode.RemoveNeighbours(al.get(j));
										}
									}
									tempnode.AddNeighbours(n);
									tempnode.AddNeighbours(c);
									oldList.add(neighbours2);
									newList.add(neighbours2);
								}
							} catch (NotBoundException e) {
								System.out.println("x split error");
								System.out.println(e.getMessage());
							}

						}// end for arraylist.size
						//Update the neighbour for old zone
						this.neighbours = oldList;
						/*System.out.println("Old List : ");
						for (Neighbours neighbours : oldList) {
							System.out.println(neighbours.ip);
						}*/
						
						//Update the neighbour for new zone
						node.setNeighBours(newList);
						/*System.out.println("new List : ");
						for (Neighbours neighbours : newList) {
							System.out.println(neighbours.ip);
						}*/
						this.AddNeighbours(n);
						node.AddNeighbours(c);

					} else {//for the second node in the network

						this.AddNeighbours(n);
						node.AddNeighbours(c);

					}

				} else {// Split the zone along the y-axis

					double a = ((this.getHy() + this.getLy()) / 2);
					/*System.out.println("Splitting point : " + a);
					System.out.println("Stting new co-ordinates : " + lx + " "
							+ hx + " " + a + " " + hy + " " + ipAddress);*/
					node.setCoordinates(lx, hx, a, hy, ipAddress);
					/*System.out.println("Stting new n : " + lx + " " + hx + " "
							+ a + " " + hy + " " + ipAddress);*/
					n.setCoordinates(lx, hx, a, hy, ipAddress);
					hy = (a);
					midx = (hx + lx) / 2;
					midy = (hy + ly) / 2;
					c.setCoordinates(lx, hx, ly, a, ip);
					
					ArrayList<Neighbours> oldList = new ArrayList<Neighbours>();
					ArrayList<Neighbours> newList = new ArrayList<Neighbours>();
					ArrayList<Neighbours> arrayList = this.neighbours;
					
					//Transferring files from old zone to new zone
					if (files.size() != 0) {
						ArrayList<Files> list = files;
						for (int i = 0; i < list.size(); i++) {
							if (node.insideArea(list.get(i))) {
								node.addFiles(list.get(i));
								node.ReceiveFile(this.ip, list.get(i).name);
								this.removeFiles(list.get(i));
							}
						}
					}
					
					//If checks if zone has neighbours or not
					if (arrayList.size() != 0) {
						
						//Update the neighbours of old zone and new zone
						for (int i = 0; i < arrayList.size(); i++) {
							try {
								Neighbours neighbours2 = arrayList.get(i);
								if ((neighbours2.ly <= node.getLy() && neighbours2.hy <= node.getLy())) {
									Registry reg1 = LocateRegistry.getRegistry(neighbours2.ip, 8000);
									Peer tempnode = (Peer) reg1.lookup("peerNode");
									ArrayList<Neighbours> al = tempnode.getNeighbours();
									oldList.add(neighbours2);
									for (int j = 0; j < al.size(); j++) {
										if (al.get(j).ip.equals(this.ip)) {
											tempnode.RemoveNeighbours(al.get(j));

										}
									}
									tempnode.AddNeighbours(c);
								} else if (neighbours2.ly >= node.getLy()
										&& neighbours2.hy >= node.getLy()) {
									newList.add(neighbours2);
									Registry reg1 = LocateRegistry.getRegistry(neighbours2.ip, 8000);
									Peer tempnode = (Peer) reg1.lookup("peerNode");
									ArrayList<Neighbours> al = tempnode.getNeighbours();
									for (int j = 0; j < al.size(); j++) {
										if (al.get(j).ip.equals(this.ip)) {
											tempnode.RemoveNeighbours(al.get(j));
										}
									}
									tempnode.AddNeighbours(n);

								} else if ((neighbours2.ly < node.getLy() && neighbours2.hy > node.getLy())
										&& (neighbours2.lx == node.getHx() || neighbours2.hx == node.getLx())) {
									Registry reg1 = LocateRegistry.getRegistry(neighbours2.ip, 8000);
									Peer tempnode = (Peer) reg1.lookup("peerNode");
									ArrayList<Neighbours> al = tempnode.getNeighbours();
									for (int j = 0; j < al.size(); j++) {
										if (al.get(j).ip.equals(this.ip)) {
											tempnode.RemoveNeighbours(al.get(j));
										}
									}
									tempnode.AddNeighbours(n);
									tempnode.AddNeighbours(c);
									oldList.add(neighbours2);
									newList.add(neighbours2);
								}
							} catch (NotBoundException e) {
								System.out.println("y split error");
								System.out.println(e.getMessage());
							}

						}
						//update the neighbours of old zone
						this.neighbours = oldList;
						/*System.out.println("Old List : ");
						for (Neighbours neighbours : oldList) {
							System.out.println(neighbours.ip);
						}*/
						
						//update the neighbours of new zone
						node.setNeighBours(newList);
						/*System.out.println("new List : ");
						for (Neighbours neighbours : newList) {
							System.out.println(neighbours.ip);
						}*/
						this.AddNeighbours(n);
						node.AddNeighbours(c);

					} else {//For adding the second node in the network
						this.AddNeighbours(n);
						node.AddNeighbours(c);
					}

				}
				// outer Catch end
			} catch (NotBoundException e1) {
				System.out.println("split  error");
				System.out.println(e1.getMessage());

				// e1.printStackTrace();
			}
			//System.out.println("Splitting completed....");
		}
	}
	
	/**
	 * Node leaves the network transferring the files and zone to neighbours
	 */
	public boolean leave() throws RemoteException {
		ArrayList<Neighbours> list = this.getNeighbours();
		Neighbours c = new Neighbours();
		c.setCoordinates(lx, hx, ly, hx, ip);
		//Check if there are more the 1 nodes in CAN
		if (list.size() > 0) {
			
			//Check the shape of region to be splitted. If difference between x and y coordinates 
			//is equal then its a square.
			if (Math.abs(this.hx - this.lx) == Math.abs(this.hy - this.ly)) {
					boolean chk=true;															
					for (int i = 0; i < list.size(); i++) {
					Neighbours n = list.get(i);
						if ((n.lx == this.lx && n.hx == this.hx)
							|| (n.ly == this.ly && n.hy == this.hy)) {// both are vertical
						Registry reg2 = LocateRegistry.getRegistry(n.ip, 8000);
						try {
							Peer canNode = (Peer) reg2.lookup("peerNode");
							Registry reg1 = LocateRegistry.getRegistry("129.21.30.38", 8000);
							Bootstrap obj = (Bootstrap) reg1.lookup("server");
							String s = obj.getBootStrapNode();
							if (s.equals(this.getIp()) && chk) {
								obj.BootStrapNode(n.ip);
								chk =false;
							}
							if ((n.lx == this.lx && n.hx == this.hx))// vertical
							{
								if (n.ly == this.hy) {// neighbour upper one
									canNode.setLy(this.ly);
									n.setLy(this.ly);
								} else {// neighbour lower one
									canNode.setHy(this.hy);
									n.setHy(this.hy);
								}

							} else// horizontal
							{
								if (n.lx == this.hx) {
									canNode.setLx(this.lx);
									n.setLx(this.lx);
								} else {
									canNode.setHx(this.hx);
									n.setHx(this.hx);
								}
							}
							// updating files
							for (int j = 0; j < files.size(); j++) {
								canNode.addFiles(this.files.get(j));
								canNode.ReceiveFile(this.getIp(),this.files.get(j).name);
							}
							this.RemoveNeighbours(n);

							for (int j = 0; j < neighbours.size(); j++) {
								boolean isPresent = canNode.AddNeighbours(neighbours.get(j));
								Registry reg = LocateRegistry.getRegistry(neighbours.get(j).ip, 8000);
								Peer can = (Peer) reg.lookup("peerNode");
								can.RemoveNeighbours(c);
								if (!isPresent) {
									can.AddNeighbours(n);
								}
							}
							canNode.RemoveNeighbours(c);
						} catch (NotBoundException e) {
							e.printStackTrace();

						}
						break;
					}// end if for check

				}// End for loop for list

			} else {
				boolean chk=true;
				if (this.lx == 0 && this.hx == 100) {// horizontal node leaving
					for (int j = 0; j < neighbours.size(); j++) {
						Neighbours n = neighbours.get(j);
						Registry reg2 = LocateRegistry.getRegistry(n.ip, 8000);
						try {
							Peer canNode = (Peer) reg2.lookup("peerNode");
							Registry reg1 = LocateRegistry.getRegistry("129.21.30.38", 8000);
							Bootstrap obj = (Bootstrap) reg1.lookup("server");
							String s = obj.getBootStrapNode();
							if (s.equals(this.getIp()) && chk) {
								obj.BootStrapNode(n.ip);
								chk=false;
							}					
							
						if(this.hy== n.ly){
								n.ly=this.ly;
								canNode.setLy(this.ly);
						}else{
								n.hy=this.hy;
								canNode.setHy(this.hy);
						}
						for (int k = 0; k < files.size(); k++) {
							canNode.addFiles(files.get(k));
						}
						canNode.RemoveNeighbours(c);
						} catch (NotBoundException e) {
							e.printStackTrace();
						}
					
					}	
					
				} else {// vertical node leaving

					
					for (int j = 0; j < neighbours.size(); j++) {
						Neighbours n = neighbours.get(j);
						Registry reg2 = LocateRegistry.getRegistry(n.ip, 8000);
						try {
							Peer canNode = (Peer) reg2.lookup("peerNode");
							Registry reg1 = LocateRegistry.getRegistry("129.21.30.38", 8000);
							Bootstrap obj = (Bootstrap) reg1.lookup("server");
							String s = obj.getBootStrapNode();
							if (s.equals(this.getIp()) && chk) {
								obj.BootStrapNode(n.ip);
								chk=false;
							}					
							
						if(this.hx== n.lx){
								n.lx=this.lx;
								canNode.setLx(this.lx);
						}else{
								n.hx=this.hx;
								canNode.setHx(this.hx);
						}
						for (int k = 0; k < files.size(); k++) {
							canNode.addFiles(files.get(k));
						}
						canNode.RemoveNeighbours(c);
						} catch (NotBoundException e) {
							e.printStackTrace();
						}
					
					}
				}// end vertical node leaving
			
			}// end else outer outer if compare
		} else {
			//One node is present in CAN. Remove the node and update the Bootstrap server
			try {
				Registry reg = LocateRegistry.getRegistry("129.21.30.38", 8000);
				Bootstrap obj = (Bootstrap) reg.lookup("server");
				obj.removeBootStrapNode();
			} catch (NotBoundException e) {
				e.printStackTrace();
			}

		}
		return true;
	}
	/**
	 * Initialize the first node in the network
	 */
	public void initializeGrid() throws RemoteException {
		lx = 0;
		ly = 0;
		hx = 100;
		hy = 100;
		midx = 50;
		midy = 50;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Provides the information about the node
	 */
	public static void view() {
		System.out.println("Coordinates are :");
		System.out.println("LX " + lx);
		System.out.println("LY " + ly);
		System.out.println("HX " + hx);
		System.out.println("HY " + hy);
		System.out.println();
		System.out.println("Ip Address :" + ip);
		if (files.size() > 0) {
			System.out.println();
			System.out.println("Files are :");
		}
		for (Files files2 : files) {
			System.out.println("Name :" + files2.name);
			System.out.println("X coordinate :" + files2.x);
			System.out.println("Y coordinate :" + files2.y);
			System.out.println();
		}
		if (neighbours.size() > 0)
		{System.out.println();
			System.out.println("Neighbours are :");
		
		}
		for (Neighbours neighbours2 : neighbours) {
			System.out.println(neighbours2.ip);
		}
		System.out.println();
	}

	public static void main(String[] args) throws RemoteException {
		Scanner scanner = new Scanner(System.in);
		boolean join = false;
		boolean conti = true;
		int option;
		String fileName;
		int x;
		int y;

		CANNode node = new CANNode();
		while (conti) {
			System.out.println("Please select an option...");
			System.out.println("1.Join");
			System.out.println("2.Search");
			System.out.println("3.Insert");
			System.out.println("4.View");
			System.out.println("5.Leave");

			option = scanner.nextInt();

			switch (option) {
			case 1:
				if (!join) {
					node.join();
					System.out.println("Node joined");
					join = true;
					break;
				} else {
					System.out.println("Node Already present in network...");
					break;
				}
			case 2:if (join) {
					System.out.println("Please enter the filename to be searched...");
					fileName = scanner.next();
					x = node.gethashX(fileName);
					y = node.gethashY(fileName);
					node.search(2, x, y, fileName, node.getIp());
				}
				else{
					System.out.println("Node not in network. Please join the network first.");
				}
				break;

			case 3:
				if(join){
				System.out.println("Please enter the name of file to be transfered...");
				fileName = scanner.next();
				x = node.gethashX(fileName);
				y = node.gethashY(fileName);
				node.search(3, x, y, fileName, node.ip);
				}
				else{
					System.out.println("Node not in network. Please join the network first.");
				}
				break;

			case 4:if(join){
					view();
					}
				else{
					System.out.println("Node not in network. Please join the network first.");
				}
				break;				
				
			case 5:if(join){
					conti = node.leave();
						if(conti){
							System.out.println("Leave Successful...");
							System.exit(0);
						}
						else{
							System.out.println("Failure");
						}
					}
					else{
						System.out.println("Node not in network. Please join the network first.");
					}
					break;
			default:System.out.println("Please enter correct option...!!!");
				break;
			}

		}

	}

	/**
	 * Gets the current list of neighbours
	 */
	@Override
	public ArrayList<Neighbours> getNeighbours() throws RemoteException {
		return this.neighbours;
	}
	
	/**
	 * Gets the lower value of x coordinate
	 */
	public double getLx() throws RemoteException {
		return lx;
	}
	
	/**
	 * sets the lower value of x coordinate
	 */
	public void setLx(double lx) throws RemoteException {
		this.lx = lx;
	}

	/**
	 * 
	 */
	public double getLy() throws RemoteException {
		return ly;
	}

	/**
	 * 
	 */
	public void setLy(double ly) throws RemoteException {
		this.ly = ly;
	}

	/**
	 * 
	 */
	public double getHx() throws RemoteException {
		return hx;
	}

	/**
	 * 
	 */
	public void setHx(double a) throws RemoteException {
		this.hx = a;
	}

	/**
	 * 
	 */
	public double getHy() throws RemoteException {
		return hy;
	}

	/**
	 * 
	 */
	public void setHy(double hy) throws RemoteException {
		this.hy = hy;
	}

	/**
	 * 
	 */
	public double getMidx() throws RemoteException {
		return midx;
	}

	/**
	 * 
	 */
	public void setMidx(double midx) throws RemoteException {
		this.midx = midx;
	}

	/**
	 * 
	 */
	public double getMidy() throws RemoteException {
		return midy;
	}

	/**
	 * 
	 */
	public void setMidy(double midy) throws RemoteException {
		this.midy = midy;
	}

	/**
	 * 
	 */
	public String getIp() throws RemoteException {
		return ip;
	}

	/**
	 * 
	 */
	public void setIp(String ip) throws RemoteException {
		this.ip = ip;
	}

	/**
	 * 
	 */
	@Override
	public void setNeighBours(ArrayList<Neighbours> n) throws RemoteException {
		this.neighbours = n;

	}

	/**
	 * 
	 */
	@Override
	public void setLocation(String ip) throws RemoteException {
		this.location = ip;
	}

	/**
	 * 
	 */
	public void print(String s) throws RemoteException {
		System.out.println(s);
	}

	/**
	 * 
	 */
	public ArrayList<Files> getFiles() throws RemoteException {
		return this.files;
	}

}
