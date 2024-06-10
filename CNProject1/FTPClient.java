import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class FTPClient {
	Socket requestSocket;           //socket connect to the server
	DataOutputStream out;         //stream write to the socket
 	DataInputStream in;          //stream read from the socket
	String message;                //message send to the server
	String[] command;
	public void Client() {}

	void run()
	{
		try{
			System.out.println("Enter the server port number");
			//get Input from standard input
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			int serverPort = Integer.parseInt(bufferedReader.readLine()); 
			//create a socket to connect to the server
			requestSocket = new Socket("localhost", serverPort);
			System.out.println("Connected to localhost in port " + serverPort);
			//initialize inputStream and outputStream
			out = new DataOutputStream(requestSocket.getOutputStream());
			out.flush();
			in  =  new DataInputStream(requestSocket.getInputStream());
			
			
			
			while(true)
			{
				System.out.print("Hello, please input a sentence: ");
				//read a sentence from the standard input
				command = bufferedReader.readLine().split(" ");
				if(command[0].equals("get")) {
					String[] fileNameAndExt = command[1].split("\\.");
					out.writeUTF("get " + command[1]);
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
				else if(command[0].equals("upload")) {
					String fileName = command[1];
					out.writeUTF("upload " + command[1]);
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
				else {
					System.out.println("invalid command");
				}
			}
		}
		catch (ConnectException e) {
    			System.err.println("Connection refused. You need to initiate a server first.");
		}
		catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//Close connections
			try{
				in.close();
				out.close();
				requestSocket.close();
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}

	public static void main(String args[])
	{

		FTPClient
	 client = new FTPClient
	();
		client.run();
	}

}
