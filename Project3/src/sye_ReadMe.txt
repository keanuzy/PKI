Creator: Sarah Yaw
Project Description:
	Multithreaded client and server programs with encryption. The server is able to support multiple clients coming and going. As long as there is at least one client, the server will update any incoming clients to the chats that happened before that specific arrival. The server will broadcast arrivals and departures of clients as well as their messages.
Run Instructions:
	1)  Open up the terminal window.
	2)  Type "cd "and then the file path to where the sye_TCPServer.java and sye_TCPClient.java files are located and hit enter.
			Example: cd Desktop/school/s1/CommNet/Project2
	3)  Type "javac sye_TCPServer.java" in the terminal and hit enter. This compiles the program and adds a file called sye_TCPServer.class to your folder.
	4)  Type "java sye_TCPServer" and type in the credentials require then hit enter. These include the port (without which, your server will not run), and the Diffe-Hellman G and N values. If you do not include the G and N values, defaults will be used instead.
			Example: java sye_TCPServer -p 20700 -g 2849 -n 381
			Note: If running on Google Cloud the run command will be "nohup java sye_TCPServer -p 20700 -g 2849 -n 381 &"
	5)  Open a second terminal window. It is imperative you do not close the one you have started the server on.
	6)  Type "cd "and then the file path to where the sye_TCPServer.java and sye_TCPClient.java files are located and hit enter.
			Example: cd Desktop/school/s1/CommNet/Project2
	7)  Type "javac sye_TCPClient.java" in the terminal and hit enter. This compiles the program and adds a file called sye_TCPClient.class to your folder.
	8)  Type "java sye_TCPClient" and type in the credentials required. These include the host, port number, and username. Each of these three are optional, but you will be prompted to type in the username if you do not include it here. If the other two values are missing, the program will alert the user and terminate. Each of these values needs a key to be recognized as well. -h for host, -p for port, and -u for user. If an incorrect key is used, the program will complain and terminate.
			Example: java sye_TCPClient -h hostName -u userName -p portName
	9)  You are now connected to the server and should be able to interact with it. Send whatever messages you want by typing them in and hitting enter to send.
	10) To disconnect form the server type in "DONE" and hit enter. This must be its own message.'
Conclusion: 
		I estimate this took 36 hours to complete. I started this project on the 8th, but naturally when it rains it pours and that weekend and a bit into the week had given me a pain flare that again, extended my work time. The implementation was not too bad actually. The most difficult part was making sure the correct keys were sent to the encryption and decryption methods.
                The most difficult part for me was getting the encrypted message to flush from the server to broadcast out. I don't know why it took so long for me to realize that I needed to use println instead of print before the flush, but I swear that took up at least the last third of the time i spent on this trying to figure out what went wrong.
		I'm happy to report that my program works really well! Each project building on this makes me so excited that I've actually built a good chunk of an actual chat program.
		I think this assignment worked out pretty well. I don't really have any changes I would make. 