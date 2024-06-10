import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class FTPServer {
	int sPort = 5106;    //The server will be listening on this port number
	ServerSocket sSocket;   //serversocket used to lisen on port number 5106
	Socket connection = null; //socket for the connection with the client
	String message;    //message received from the client
	DataOutputStream out;  //stream write to the socket
	DataInputStream in;    //stream read from the socket

    	public void Server() {}

	void run()
	{
		try{
			//create a serversocket
			sSocket = new ServerSocket(sPort, 10);
			//Wait for connection
			System.out.println("Waiting for connection");
			//accept a connection from the client
			connection = sSocket.accept();
			System.out.println("Connection received from " + connection.getInetAddress().getHostName());
			//initialize Input and Output streams
			out = new DataOutputStream(connection.getOutputStream());
			out.flush();
			in  =  new DataInputStream(connection.getInputStream());
			
				while(true)
				{
					//receive the message sent from the client
					message = in.readUTF();
					String[] command = message.split(" ");
					if(command[0].equals("get")) {
						String fileName = command[1];
						System.out.println(fileName);
						File file = new File(fileName);
						if (!file.exists()) {
							out.writeUTF("File not found.");
							continue;
						}
						FileInputStream fileInput = new FileInputStream(file);
						byte[] buffer = new byte[1024];
						int bytesRead = 0;
						while ((bytesRead = fileInput.read(buffer)) > 0) {
							System.out.println(1);
							out.write(buffer, 0, bytesRead);
						}
						fileInput.close();
					}
					else if(command[0].equals("upload")) {
						String outputFileName = "new" + command[1];
						File file = new File(outputFileName);
						FileOutputStream fileOutput = new FileOutputStream(file);
						byte[] buffer = new byte[1024];
                    	int bytesRead = 0;
						while ((bytesRead = in.read(buffer)) > 0) {
							fileOutput.write(buffer, 0, bytesRead);
						}
						fileOutput.close();
					}
					System.out.println("Receive message: " + message);
					
				}
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//Close connections
			try{
				in.close();
				out.close();
				sSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
 	public static void main(String args[]) {
        FTPServer s = new FTPServer();
        s.run();  
 
    }

}
