package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static int PORT = 8888;
    private static String HOST ="localhost";
    public static void main(String[] args) {
        /**
         * 1. connect to servers port. - what is happening behind the scenes?
         * 2. Print the port number client has opened socket .
         * 3. take inout from the keyboard and send it to the server.
         * 4. also side by side print the server's message
         */

        try(Socket clientSocket = new Socket(HOST,PORT)){
            System.out.println("Connected to server at port : " + clientSocket.getLocalPort());
            BufferedReader readServerMessage = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedReader readClientMessage = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter writeClientMessage = new PrintWriter(clientSocket.getOutputStream(),true);
            new Thread(() ->{
                Thread.currentThread().setName("Roshini threads");
                System.out.println("Printing messages from server");
                String msg ;
                try{
                    while(( msg = readServerMessage.readLine()) != null){
                        System.out.println(msg);
                    }
                    System.out.println("Server has been closed gracefully. ");
                }catch (IOException e){
                    System.out.println("Server disconnected or someone closed the client socket connection");
                }
            }).start();
            System.out.println("Please Enter your message here!");
            String message;
            try{
                while((message = readClientMessage.readLine()) != null){
                    writeClientMessage.println(message);
                }
            }catch (IOException e){
                System.out.println("Server disconnected or someone closed the client socket connection");
            }

        } catch (IOException e) {
            System.out.println("Either sever is not reachable or the connection is closed");
        }
    }
}