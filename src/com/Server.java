//Two Phase Commit Protocol SERVER
package com;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	boolean closed = false, inputAll = false;
//	creating list of client threads
	List<ClientThread> clients;
//	list of data passed
	List<String> data;

//	initializing lists
	public Server() {
		clients = new ArrayList<ClientThread>();
		data = new ArrayList<String>();
	}

	public static void main(String args[]) 
	{
//		creating sockets, initializing port number
		Socket clientSock = null;
		ServerSocket serverSocket = null;
		int port_number = 1234;
		Server server = new Server();
		try{
//			setting the server port
			serverSocket = new ServerSocket(port_number);
		}catch (IOException e){
			System.out.println(e);
		}
//		checking if server is running
		while (!server.closed){
			try {
//					accepting client 
					clientSock = serverSocket.accept();
//					adding client to thread list
					ClientThread clientThread = new ClientThread(server, clientSock);
					(server.clients).add(clientThread);
					System.out.println("\nNow Total clients are : " + (server.clients).size());
//					setting the data
					(server.data).add("NOT_SENT");
//					starting the thread
					clientThread.start();
			} catch (IOException e) { }
		}
		try {
			
			serverSocket.close();
		} catch (Exception e1) { }
	}
}
// class that creates threads
class ClientThread extends Thread 
{
	DataInputStream is = null;
	String line;
	String destClient = "";
	String name;
	PrintStream os = null;
	Socket clientSock = null;
	String clientIdentity;
	Server server;

	public ClientThread(Server server, Socket clientSock) 
	{
		this.clientSock = clientSock;
		this.server = server;
	}

	@SuppressWarnings("deprecation")
	public void run() 
	{
		try {
			is = new DataInputStream(clientSock.getInputStream());
			os = new PrintStream(clientSock.getOutputStream());
			os.println("Enter your name.");
			name = is.readLine();
			clientIdentity = name;
			os.println("Welcome " + name + " to this 2 Phase Application.\nYou will receive a vote Request now...");
			os.println("VOTE_REQUEST\nPlease enter COMMIT or ABORT to proceed : ");
//			prints the name of client when entered
			for (int i = 0; i < (server.clients).size(); i++){
				if ((server.clients).get(i) != this){
					((server.clients).get(i)).os.println("---A new user " + name + " entered the  Appilcation---");
				}
			}
			while(true){
				line = is.readLine();
//				checks if any client has aborted, if they have then the whole application is aborted as pre 2 phase commit
				if (line.equalsIgnoreCase("ABORT")) 
				{
					System.out.println("\nFrom '" + clientIdentity
							+ "' : ABORT\n\nSince aborted we will not wait for inputs from other clients.");
					System.out.println("\nAborted....");
					
					for (int i = 0; i < (server.clients).size(); i++) {
						((server.clients).get(i)).os.println("GLOBAL_ABORT");
						((server.clients).get(i)).os.close();
						((server.clients).get(i)).is.close();
					}
					break;
				}
//				checks if all the client have committed
				if (line.equalsIgnoreCase("COMMIT")) 
				{
					System.out.println("\nFrom '" + clientIdentity + "' : COMMIT");
					if ((server.clients).contains(this)) 
					{
						(server.data).set((server.clients).indexOf(this), "COMMIT");
						for (int j = 0; j < (server.data).size(); j++) 
						{
							if (!(((server.data).get(j)).equalsIgnoreCase("NOT_SENT"))) 
							{
								server.inputAll = true;
								continue;
							} 
							else{
								server.inputAll = false;
								System.out.println("\nWaiting for inputs from other clients.");
								break;
							}
						}
						if (server.inputAll) 
						{
							System.out.println("\n\nCommited....");
							for (int i = 0; i < (server.clients).size(); i++) 
							{
								((server.clients).get(i)).os.println("GLOBAL_COMMIT");
								((server.clients).get(i)).os.close();
								((server.clients).get(i)).is.close();
							}
							break;
						}
					} // if thread.contains
				} // commit
			} // while
			server.closed = true;
			clientSock.close();
		} catch (IOException e) { }
		
	}
}// end class thread