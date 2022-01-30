// Programmer: Sarah Yaw
// Client program with added encryption
// File name: TCPClient.java

import java.io.*;
import java.net.*;
import java.util.*;
public class TCPClient
{
	private static InetAddress host;
	private static String input="";
	public static boolean closing=false;
	public static int G, N, myKey, serverKey, a;
	public static void main(String[] args)
	{
		int port = 20700;
		String user = "";
		boolean hasHost=false, hasPort=false, hasUser=false;
		int hostIndex=0, portIndex=0, userIndex=0; 
		Scanner keyb = new Scanner(System.in);
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
					if(args[i].equals("-h"))
					{
						hasHost=true;
						hostIndex = i+1;
					}
					if(args[i].equals("-p"))
					{
						hasPort=true;
						portIndex = i+1;
					}
					if(args[i].equals("-u"))
					{
						hasUser=true;
						userIndex = i+1;
					}
					//if there is an invalid command
					if(!args[i].equals("-u")&&!args[i].equals("-p")&&!args[i].equals("-h")&&args[i].charAt(0)=='-')
					{
						System.out.println("Invalid command "+args[i]);
						System.exit(0);
					} 
				}

				// Get server IP-address
				if(hasHost)
				{
					host = InetAddress.getByName(args[hostIndex]);
				}
				else
				{
					//host = InetAddress.getLocalHost();
					System.out.println("Please enter a host");
					System.exit(0);
				}

				//Get Port
				if(hasPort)
				{
					port = Integer.parseInt(args[portIndex]);
				}
				else
				{
					System.out.println("Default port "+port+" used.");
				}

				// Get username
				if(hasUser)
				{
					user = args[userIndex];
				}
				else
				{
					System.out.print("Please enter a username: ");
					user = keyb.next();
				}
			}
			else if (args.length==0) //if the command line is left empty of arguments aside from running the client
			{
				System.out.println("Please enter the host, port, and username");
				System.exit(0);
				//host = InetAddress.getLocalHost();
				//port = 20700;
				//System.out.print("Please enter a username: ");
				//user = keyb.next();
			}
		}
		catch(UnknownHostException e)
		{
			System.out.println("Host ID not found!");
			System.exit(1);
		}
		runn(port, user);
	}

	private static void runn(int port, String user)
	{
		Socket link = null;
		try
		{
			// Establish a connection to the server
			link = new Socket(host,port);

			// Set up input and output streams for the connection
			BufferedReader in = new BufferedReader(
					new InputStreamReader(link.getInputStream()));
			PrintWriter out = new PrintWriter(
					link.getOutputStream(),true); 

			//send username before anything else
			out.write(user);
			out.write("\n");
			out.flush();

			//Set up stream for keyboard entry
			BufferedReader userEntry = new BufferedReader(new InputStreamReader(System.in));
			String message, response;

			//communication to and from server
			Thread scComm=new Thread(new ServConsole(in));
			Thread usComm=new Thread(new UserServer(out, userEntry));

			//starts
			scComm.start();
			usComm.start();

			//joins - only receives at this point and only gets server exit report
			try
			{
				scComm.join();
				usComm.join();
			}
			catch(Exception e)
			{
				System.out.println(e);
				e.printStackTrace();
			}

			//Receive the final report and close the connection
			//System.out.println("\rCLOSING REPORT");
			message = in.readLine();
			//decrypt
			response = ServConsole.decrypt(message, ServConsole.padd);
			System.out.println("\r"+response+" "+" "+" "+" "+" "+" "+" "+" "+" "+" "+" "+" "+" ");
			do
			{
				message = in.readLine();
				if (message==null){}
				else
				{
					//decrypt
					message = ServConsole.decrypt(message, ServConsole.padd);
					System.out.println(message);
				}
			}while(message!=null && !message.substring(0,3).equals("Serv"));
			//System.out.println("CLOSING REPORT FINISHED");

		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			//actually close the connection
			try
			{
				System.out.println("\n!!!!! Closing connection... !!!!!");
				link.close();
			}

			catch(IOException e)
			{
				System.out.println("Unable to disconnect!");
				System.exit(1);
			}
		}
	}
}
class ServConsole extends Thread
{
	private BufferedReader fromServ;
	//console output is system.out
	private String response;
	public static String padd;
	public ServConsole(BufferedReader sbr)
	{
		fromServ=sbr;
	}

	public static String decrypt(String message, String pad)
	{
		String inp="";
		int parseInt;
		String temo[] = message.split(" ");
		char c;
		for (int i = 0; i<temo.length;i++)
		{
			if(!temo[i].equals(""))
			{
				parseInt = Integer.parseInt(temo[i]) ^ Integer.parseInt(pad,2);
				c = (char)parseInt;
				inp+=c;
			}
		}
		return inp;
	}

	@Override
	public void run()
	{
		// Receive data from the server
		while(!TCPClient.closing)
		{
			try
			{
				response = fromServ.readLine();
				//System.out.println("\rLISTENING -----------");
				if (response!=null)
				{         //the client isnt listening for some reason here
					if (!response.equals("initializing..."))
					{
						//decrypt               
						response = decrypt(response, padd);
						System.out.println("\r"+response+" "+" "+" "+" "+" "+" "+" "+" "+" "+" "+" "+" "+" "+" "+" "+" ");
						System.out.print("Enter message: ");
					}
					else //this runs as soon as it connects to the server
					{
						//System.out.println("initializing...");
						response = fromServ.readLine();
						String temp[] = response.split(" ");
						TCPClient.G = Integer.parseInt(temp[1]); //g
						TCPClient.N = Integer.parseInt(temp[3]); //n
						System.out.print("G: "+TCPClient.G+"; N: "+TCPClient.N);

						TCPClient.a = (int)(Math.random()*100)+100; //a
						TCPClient.myKey = 1;
						for (int i = 0; i<TCPClient.a; i++)
						{
							TCPClient.myKey = (TCPClient.G * TCPClient.myKey)%TCPClient.N;
						}
						UserServer.toServ.println(TCPClient.myKey);//Ak
						UserServer.toServ.flush();

						response = fromServ.readLine();
						TCPClient.serverKey = Integer.parseInt(response); //Bk

						response = fromServ.readLine();//SkB

						TCPClient.myKey = 1;
						for (int i = 0; i<TCPClient.a; i++)
						{
							TCPClient.myKey = (TCPClient.serverKey * TCPClient.myKey)%TCPClient.N; //SkA
						}
						System.out.print("; Session-Key: "+TCPClient.myKey);

						padd = String.format("%8s",Integer.toBinaryString(TCPClient.myKey & 255)).replace(' ', '0');
						System.out.println("; padd: "+padd);    

						System.out.print("Enter message: ");
					}
				}
			}
			catch(Exception e){System.out.println(e); e.printStackTrace();}
		}//System.out.println("\rEXITED MAIN LISTENING LOOP");
	}
}
class UserServer extends Thread
{
	private BufferedReader fromUser;
	public static PrintWriter toServ;
	private String message, padd=ServConsole.padd, bin, word;
	UserServer(PrintWriter pw, BufferedReader cbr)
	{
		fromUser=cbr;
		toServ=pw;
	}

	public String encrypt(String message, String padd)
	{
		bin="";
		String output="";
		if (padd==null) {
			System.out.println("Sorry. You lose. Get a valid certificate, baby");
			System.exit(1);
		}
		
		for (int i = 0; i<message.length(); i++)
			output+= (Integer.valueOf(message.charAt(i)) ^ Integer.parseInt(padd,2))+" ";
		return output;
	}

	@Override
	public void run()
	{
		// Get data from the user and send it to the server
		do
		{
			try
			{
				System.out.print("Enter message: ");
				message = fromUser.readLine();
				//encrypt 
				toServ.println(encrypt(message,ServConsole.padd));
			}
			catch(Exception e){System.out.println(e);
			e.printStackTrace();}
		}while (!message.equals("DONE"));
		TCPClient.closing=true;
	}
}
