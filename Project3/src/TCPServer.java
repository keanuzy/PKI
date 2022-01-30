

// Multi-threaded Server program with added encryption
// File name: TCPServer.java
// Programmer: Sarah Yaw

import java.io.*;
import java.net.*;
import java.util.*;


public class TCPServer
{
	//initializing the server
	private static ServerSocket servSock;
	private static String input="";
	//    private static CertificateStore certStore = 
	public static File chatLog;
	public static FileWriter log;
	public static ArrayList<ClientHandler> arr = new ArrayList<ClientHandler>();
	public static boolean canUpdate=true, hasG=false, hasN=false, hasPort=false;
	public static int count, G, N, port, gIndex, portIndex, nIndex, numMessages = 0, clientKey, b;
	public static void main(String[] args)
	{
		try
		{
			if(args.length>0)
			{
				//check to see what command is input by user
				for(int i=0; i<args.length;i++)
				{
					if(args[i]==null)
						input=input+args[i];
					else
						input = input+args[i]+" ";
					if(args[i].equals("-g"))
					{
						hasG=true;
						gIndex = i+1;
					}
					if(args[i].equals("-p"))
					{
						hasPort=true;
						portIndex = i+1;
					}
					if(args[i].equals("-n"))
					{
						hasN=true;
						nIndex = i+1;
					}
					//if there is an invalid command
					if(!args[i].equals("-g")&&!args[i].equals("-p")&&!args[i].equals("-n")&&args[i].charAt(0)=='-')
					{
						System.out.println("Invalid command "+args[i]);
						System.exit(0);
					} 
				}

				// Get server IP-address
				if(hasG)
				{
					G = Integer.parseInt(args[gIndex]);
				}

				//Get Port
				if(hasPort)
				{
					port = Integer.parseInt(args[portIndex]);
				}

				// Get username
				if(hasN)
				{
					N = Integer.parseInt(args[nIndex]);
				}
			}

			// Create a server object
			servSock = new ServerSocket(port); 
			//opening port, setting up object, and running forever
			System.out.println("Opening on port "+port+"...");
		}
		catch(IOException e)
		{
			System.out.println("Unable to attach to port "+port+"!");
			System.exit(1);
		}
		do { run(); }while (true);
	}

	private static void run()
	{
		//server operation method
		Socket link = null; 
		try
		{
			// Put the server into a waiting state
			link = servSock.accept();

			//create file and filewriter if no other sockets (which means no file)
			if(arr.isEmpty())
			{
				chatLog = new File("sy_chat.txt");
				chatLog.createNewFile();
				TCPServer.log = new FileWriter("sy_chat.txt");
			}

			// Set up input and output streams for socket
			BufferedReader in = new BufferedReader(new InputStreamReader(link.getInputStream()));
			PrintWriter out = new PrintWriter(link.getOutputStream(),true);

			// print local host name
			String host = InetAddress.getLocalHost().getHostName();     

			//take in username here to print below
			String user = in.readLine();
			System.out.println(user + " has estabished a connection to " + host);

			// Create a thread to handle this connection
			ClientHandler handler = new ClientHandler(link, user);
			arr.add(handler);

			// start serving this connection
			handler.start(); 
		}
		catch(IOException e){ e.printStackTrace(); }
	}
}

class ClientHandler extends Thread
{
	private Socket client;
	private String user, padd;
	private BufferedReader in;
	public PrintWriter out;
	public int index, myKey;
	public Object lock;
	private static long start, finish;
	private CertificateStore certStore = new CertificateStore();

	public ClientHandler(Socket s, String name)


	{
		//      I've hard-coded the privateKey here but we will probably add it to ClientHandler *BPU*
		String privateKey = "privateKey";
		// set up the socket
		client = s;
		user = name;
		this.index=TCPServer.count;
		TCPServer.count++;
		lock = new Object();

		//start the timer
		start = System.nanoTime();
		try
		{
			// Set up input and output streams for socket
			this.in = new BufferedReader(new InputStreamReader(client.getInputStream())); 
			this.out = new PrintWriter(client.getOutputStream(),true); 
			/* *BPU*

			Enter key
			Contains methods checks for user, private key if present,it checks for valid date

			 */
			certStore.createStore();
			if (certStore.contains(user, privateKey)) {
				System.out.println("Certificate is valid. Session started.");
			}
			else {
				System.out.println("Certificate is invalid.");
				this.client.close();
			}
			/*
			 * Client only gets one chance to enter valid user, key. Obviously, don't start
			 * the session if cert info isn't validated. To send msg to client:
			 * this.out.println(encrypt("Invalid cert", this.padd)); this.out.flush();
			 * 
			 * Per Sarah, call this.client.close() to end client session.
			 */

			//send G and N to client
			this.out.println("initializing...");
			this.out.println("G: "+TCPServer.G+" N: "+TCPServer.N); //g n
			this.out.flush();
			System.out.print("G: "+TCPServer.G+"; N: "+TCPServer.N);

			int clientKey = Integer.parseInt(this.in.readLine()); //Ak 

			TCPServer.b = (int)(Math.random()*100)+100; //b
			myKey = 1;
			for (int i = 0; i<TCPServer.b; i++)
			{
				myKey = (TCPServer.G * myKey)%TCPServer.N; //Bk
			} 
			this.out.println(myKey);
			this.out.flush();

			myKey = 1;
			for (int i = 0; i<TCPServer.b; i++)
			{
				myKey = (clientKey * myKey)%TCPServer.N; //SKB
			}
			System.out.print("; Session-Key: "+myKey);
			this.out.println(myKey);
			this.out.flush();

			this.padd = String.format("%8s",Integer.toBinaryString(myKey & 255)).replace(' ', '0');
			System.out.println("; padd: "+padd);    

		}
		catch(IOException e){ e.printStackTrace(); }
	}

	public String encrypt(String message, String padd)
	{
		String bin="";
		String output="";
		for (int i = 0; i<message.length(); i++)
			output+= (Integer.valueOf(message.charAt(i)) ^ Integer.parseInt(padd,2))+" "; 
		return output;
	}

	public String decrypt(String message, String padd)
	{
		String inp="";
		int parseInt;
		String temo[] = message.split(" ");
		char c;
		for (int i = 0; i<temo.length;i++)
		{
			if(!temo[i].equals(""))
			{
				parseInt = Integer.parseInt(temo[i]) ^ Integer.parseInt(padd,2);
				c = (char)parseInt;
				inp+=c;
			}
		}
		return inp;
	}

	// overwrite the method 'run' of the Runnable interface
	public void run()
	{
		Scanner backlog;
		//new join bookkeeping
		try
		{
			//output backlog of chat to new joins
			backlog = new Scanner(TCPServer.chatLog);
			String log;
			while(backlog.hasNextLine())
			{
				//encrypt
				log = backlog.nextLine();
				this.out.println(encrypt(log, this.padd));
				this.out.flush();
			}   

			//join announcement to others
			for(int i=0; i<TCPServer.arr.size();i++)
			{
				ClientHandler temp = TCPServer.arr.get(i);
				if(temp.index!=this.index)
				{
					//encrypt
					temp.out.println(encrypt(user + " has joined the chat!", temp.padd));
					temp.out.flush();
				}
			}
			//join announcement in chatlog
			TCPServer.log.write(user + " has joined the chat!\n");
			TCPServer.log.flush();

		}
		catch(Exception e){System.out.println(e);}

		// Receive and process the incoming data 
		try
		{
			String message = this.in.readLine();
			//decrypt
			message = decrypt( message, this.padd); //added user
			while (!message.equals("DONE"))
			{
				message = user +": "+ message;
				while(!TCPServer.canUpdate)
				{
					//synchronization to prevent collisions between two clients posting at once
					synchronized(this.lock)
					{
						try
						{   
							this.lock.wait();
						}
						catch(Exception e){System.out.println(e);}
					}   
				}
				TCPServer.canUpdate=false;   
				System.out.println(message);
				TCPServer.log.write(message+"\n");
				TCPServer.log.flush();
				TCPServer.numMessages ++;

				//end of synchronization
				TCPServer.canUpdate=true;
				synchronized(this.lock){this.lock.notifyAll();}

				//cycle and broadcast input to !this.out
				for(int i=0; i<TCPServer.arr.size();i++)
				{

					ClientHandler temp = TCPServer.arr.get(i);
					if(temp.index!=this.index)
					{
						//encrypt
						//System.out.println(user + ": "+ message+"; padd: "+Integer.parseInt(temp.padd, 2)+"; before enc");//debug
						message = encrypt(message, temp.padd);//og tag site +"(to:"+Integer.parseInt(temp.padd, 2)+", frm:"+Integer.parseInt(this.padd, 2)+")"
						//System.out.println(user + ": "+ message+"; padd: "+Integer.parseInt(temp.padd, 2)+"; after enc");//debug
						temp.out.println(message);//broadcasting back
						temp.out.flush();//ERROR

						message = decrypt(message, temp.padd);//debug
						//System.out.println(user + ": "+ message+"; padd: "+Integer.parseInt(temp.padd, 2)+"; after dec");//debug
					}
				}
				message = this.in.readLine();
				//decrypt
				message = decrypt(message, this.padd);
			}
			//client has said they're done
			//encrypt
			// Send a report back and close the connection
			this.out.println(encrypt("--Information Received From the Server--", this.padd));
			this.out.flush();
			Scanner file = new Scanner(TCPServer.chatLog);
			while(file.hasNextLine())
			{
				message = file.nextLine();
				//encrypt
				this.out.println(encrypt(message, this.padd));
				this.out.flush();
			}
			//encrypt
			this.out.println(encrypt("Server received " + TCPServer.numMessages + " messages total", this.padd));
			this.out.flush();

			//get the end value of timer
			finish = System.nanoTime();
			double milliseconds,seconds,minutes,hours,val=finish-start;
			hours=Math.floor(val/(36*Math.pow(10, 11)));
			val=val%(36*Math.pow(10, 12));
			minutes=Math.floor(val/(6*Math.pow(10, 10)));
			val=val%(6*Math.pow(10, 10));
			seconds=Math.floor(val/(1*Math.pow(10, 9)));
			val=val%(1*Math.pow(10, 9));
			milliseconds=Math.floor(val/(1*Math.pow(10, 6)));
			//encrypt
			this.out.println(encrypt("Length of session: "+(int)hours+"::"+(int)minutes+"::"+(int)seconds+"::"+(int)milliseconds, this.padd));
			this.out.flush();

			//departure announcement to remaining clients
			for(int i=0; i<TCPServer.arr.size();i++)
			{
				ClientHandler temp = TCPServer.arr.get(i);
				if(temp.index!=this.index)
				{
					//encrypt
					temp.out.println(encrypt(user + " has left the chat.", temp.padd));
					temp.out.flush();
				}
			}
			System.out.println(this.user+" has left the chat.");

			//actual departure from server and arrayList
			TCPServer.arr.remove(TCPServer.arr.indexOf(this));
			TCPServer.count--;

			if(TCPServer.arr.isEmpty())
			{
				System.out.println("Server is empty, clearing logs...");    
				//debugging statement but I like it there^
				out.close();
				file.close();
				TCPServer.chatLog.delete();
			}

		}
		catch(IOException e){ e.printStackTrace(); }
		finally
		{
			try
			{
				TCPServer.log.write(this.user+" has left the chat.\n");
				//departure notification placed in chatlog
				TCPServer.log.flush();
				if(TCPServer.arr.isEmpty()) //if server is empty then delete the chatlog
					TCPServer.log.close();
				this.client.close(); 
			}
			catch(IOException e)
			{
				System.out.println("Unable to disconnect!");
				System.exit(1);
			}
		}
	}
}
