
//Two Phase Commit Protocol CLIENT

package com;

import java.io.*;
import java.net.*;

//implements runnable interface to support multi-threading
public class Client implements Runnable
{  
//	creating required socket, input and output streams
	static Socket clientSock = null;
	static PrintStream ps = null; 
	static DataInputStream dis = null;
	static BufferedReader input = null;
	static boolean closed = false;
	
	public static void main(String[] args)     
	{ 
//		initializing the port number. It can be any 4 digit number
		int port_number=1234;   
//		taking host as local host
		String host="localhost"; 
		try { 
//			connecting to localhost at port_number
			clientSock = new Socket(host, port_number); 
			input = new BufferedReader(new InputStreamReader(System.in));
			ps = new PrintStream(clientSock.getOutputStream());     
			dis = new DataInputStream(clientSock.getInputStream()); 
		}catch (Exception e){
			System.out.println("Exception occurred : "+e.getMessage()); 
		}   
//	checking if server is running	
		if (clientSock != null && ps != null && dis != null){
			try{ 
//				creating a new thread
				new Thread(new Client()).start();   
				while (!closed){  
					ps.println(input.readLine()); 
				}
				ps.close();    
				dis.close();   
				clientSock.close();   
			}catch (IOException e){    
				System.err.println("IOException:  " + e); 
			} 
		} 
	}     
	@SuppressWarnings("deprecation")
	public void run(){   
		String responseLine;     
		try{   
//			reading the input line using buffered read method
			while ((responseLine = dis.readLine()) != null){ 
				System.out.println(responseLine); 
//				committing or aborting depending an the response
				if (responseLine.equalsIgnoreCase("GLOBAL_COMMIT")==true || responseLine.equalsIgnoreCase("GLOBAL_ABORT")==true){     
					break;    
				}     
			}
//			closing the client
			closed=true;    
		}catch (IOException e){    
			System.err.println("IOException:  " + e);      
		}
	}
} //end client

