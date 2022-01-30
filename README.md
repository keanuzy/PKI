
Creators: Yu Zhang, Sarah Yaw, [@bpurbancic](https://github.com/bpurbancic), [@PaytonSims3](https://github.com/PaytonSims3), [@yu-zha](https://github.com/yu-zha), and [@fchandoo](https://github.com/fchandoo).

NOTE: We will make all changes to the working repo and branches off of it. These changes will be merged into master after group review.

Project Description:
  This will soon be a rough PKI infrastructure for out Cybersecurity class at EMU.
## Run Instructions:

1. Open up the terminal window.
	
2. Type "cd "and then the file path to where the TCPServer.java and TCPClient.java files are located and hit enter.
	>Example: cd Desktop/school/s3/sec/592-PKI/src
			
3. Type "javac TCPServer.java" in the terminal and hit enter. This compiles the program and adds a file called TCPServer.class to your folder.
	
4. Type "java TCPServer" and type in the port then hit enter.
	>Example: java TCPServer -p 20700 -g 2849 -n 381

### To connect as a client:
	
5. Open a second terminal window. It is imperative you do not close the one you have started the server on.
	
6. Type "cd "and then the file path to where the TCPServer.java and TCPClient.java files are located and hit enter.
	>Example: cd Desktop/school/s3/sec/592-PKI/src
	
7. Type "javac TCPClient.java" in the terminal and hit enter. This compiles the program and adds a file called TCPClient.class to your folder.
	
8. Type "java TCPClient" and type in the credentials required. These include the host, port number, and username. Each of these three are optional, but you will be prompted to type in the username if you do not include it here. Each of these values needs a key to be recognized as well. -h for host, -p for port, and -u for user. If an incorrect key is used, the program will complain and terminate.
	>Example: java TCPClient -h hostName -u userName -p portName
	
9. You are now connected to the server and should be able to interact with it. Send whatever messages you want by typing them in and hitting enter to send.
	
10. To disconnect form the server type in "DONE" and hit enter. This must be its own message.

Credits:

  Sarah Yaw - Base code, Administrative tasks, Integration help
  
  [@bpurbancic](https://github.com/bpurbancic) - Certificate Store
  
  [@PaytonSims3](https://github.com/PaytonSims3) - Certificates 
  
  [@yu-zha](https://github.com/yu-zha) - Root Private Key
  
  [@fchandoo](https://github.com/fchandoo) - Certificate Policy
