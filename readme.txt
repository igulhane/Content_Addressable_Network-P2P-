Project 1: Content-Addressable Network (CAN) Implementation
Author: Ishan Gulhane
Aim: To implement CAN P2P network.
Working: 
CAN is a structured P2P network that offers more scalable, efficient, and robust search and management. Bootstrap server is the entry point for each node in the network. Bootstrap server stores the ip-address of the first node that joins the network. Whenever a new node joins the network it first contacts the Bootstrap server, gets the ip-address of the first node in the network and the coordinates of its zone. Node starts with the remote object of the first node and routes to the destination node.  After reaching the destination it splits the zone, updates the respective neighbors and informs them about the splitting. The old zone transfers the file that come under the new zone. Node can perform operation such as search, insert, view and leave. Search operation scans the network for the file. If the file is present in network, it downloads the file from the source node.  Insert operation calculates the storage location of the file. It then routes to the destination and stores the file. View provides information about the current node, its neighbor and files present on the node. Leave operation removes the node from the network and assigns the zone to its neighbors. 

Following are the contents of the folder:

1) Files in the folder.
• Bootstrap.java
• BootStrapServer.java
• CANNode.java
• Files.java
• Neighbours.java
• Peer.java

2) Functions Implemented
a) Join - Adds the node to the network.
b) Insert -Inserts the file in network. Please make sure the file to be inserted is present in the current directory
c) Search – Search for the given file in the network. If present, it downloads it from the destination.
d) View – It provides the details of current node, its neighbors and files present on the node.
e) Leave – Works for network of size 3. 

3) Steps to Run the code
a) Change the IPAddress of the Bootstrap Server in CANNode.java (In Join() method) as required.
b) Compile all the files in the folder, using javac *.java
c) Run the Bootstrap.java first on a separate system. Files required are Bootstrap.java and BootStrapServer.java
d) Run the CANNode.java on each client and perform the operations.
