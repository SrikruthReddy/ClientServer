import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

class InternetChatting {
    public static void main(String[] args) throws IOException{
        DataInputStream in;

        //Initiliaze WritingThreadHandler

        // Create a server socket
        ServerSocket serverSocket = new ServerSocket(0);
        // Display the server port number
        System.out.println("Server started at " + new Date() + '\n' +
            "Server port number: " + serverSocket.getLocalPort());
            Thread writingThread = new Thread(() -> {
                Socket connection = null;
                DataOutputStream out = null; // output stream to the client
                String message; // message received from the client
                try{
                    // Receive the port number to connect to from keyboard
                    Scanner keyboard = new Scanner(System.in);
                    System.out.println("Enter the port number to connect to: ");
                    int port = keyboard.nextInt();
                    // Create a socket to connect to the server
                    connection = new Socket("localhost", port);
                    // Initialize output stream
                    out = new DataOutputStream(connection.getOutputStream());
                    out.flush();
                    String name = keyboard.next();
                    out.writeUTF(name);
                    while(true)
                    {
                        // Read message from keyboard
                        message  = keyboard.nextLine();
                        // Send the message
                        out.writeUTF(message);
                        if(!message.isEmpty()) {

                            String firstWord = message;
                            if (message.contains(" ")) {
                                firstWord = message.substring(0, message.indexOf(" "));
                            }
                            if (firstWord.equals("transfer")) {
                                String fileName = message.substring(message.indexOf(" ") + 1);
                                File file = new File(fileName);
                                if (!file.exists()) {
                                    out.writeUTF("File not found.");
                                    continue;
                                }
                                FileInputStream fileInput = new FileInputStream(file);
                                byte[] buffer = new byte[1024];
                                int bytesRead = 0;
                                long fileSize = file.length();
                                int totalBytesSent = 0;
                                out.writeLong(fileSize);
                                while ((bytesRead = fileInput.read(buffer)) > 0) {
                                    out.write(buffer, 0, bytesRead);
                                    totalBytesSent += bytesRead;
                                    if(totalBytesSent == fileSize) {
                                        break;
                                    }
                                }
                                System.out.println("File sent.");
                                fileInput.close();
                            }
                        }
                        out.flush();
                    }
                }
                catch(IOException ioException){
                    System.out.println("Disconnect with Client");
                }
                finally{
                    //Close connections
                    try{
                        out.close();
                        connection.close();
                    }
                    catch(IOException ioException){
                        System.out.println("Disconnect with Client");
                    }
                }
            });
            writingThread.start();
            //new WritingThreadHandler().start();
            // Listen for a new connection request
            System.out.println("Waiting for a client to connect...");
            Socket client = serverSocket.accept();

            //initialize Input stream
            in  =  new DataInputStream(client.getInputStream());
            String name = null;
            while(true) {
                String message2 =  in.readUTF();
                if(!message2.isEmpty()) {
                    name = message2;
                    break;
                }
            }
            System.out.println("Connection received from " + name + "@" + client.getInetAddress().getHostName());
            while(true)
            {
                //receive the message sent from the client
                String message2 = in.readUTF();
                //do something with the message
                if(!message2.isEmpty()) {
                    String firstWord = message2;
                    if(message2.contains(" ")) {
                        firstWord = message2.substring(0, message2.indexOf(" "));
                    }
                    if (firstWord.equals("transfer")) {
                        long fileSize = in.readLong();
                        String outputFileName = "new" + message2.substring(message2.indexOf(" ") + 1);
                        File file = new File(outputFileName);
                        if (file.exists()) {
                            continue;
                        }
                        FileOutputStream fileOutput = new FileOutputStream(file);
                        byte[] buffer = new byte[1024];
                        int bytesRead = 0;
                        int bytesReadFromBuffer = 0;
                        while ((bytesReadFromBuffer = in.read(buffer)) > 0) {
                            fileOutput.write(buffer, 0, bytesReadFromBuffer);
                            bytesRead += bytesReadFromBuffer;
                            if (bytesRead == fileSize) {
                                break;
                            }
                        }
                        fileOutput.close();
                    }
                    System.out.println(name + ": " +  message2);

                }
            }


    }
}

