package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static int PORT = 8888;
    private static Set<Socket> synchronizedSet = Collections.synchronizedSet(new HashSet<>());
    private static ConcurrentHashMap<Socket, PrintWriter> synchronizedMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server has started listening at Port : " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("CLient has connected with port : " + clientSocket.getPort());
                synchronizedSet.add(clientSocket);
                PrintWriter pr = new PrintWriter(clientSocket.getOutputStream(), true);
                synchronizedMap.put(clientSocket, pr);
                new Thread(() -> handleClient(clientSocket)).start();
            }
        }
    }

    private static void handleClient(Socket clientSocket) {

        try (BufferedReader clientMessage = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String msg;
            while ((msg = clientMessage.readLine()) != null) {
                broadCast(msg, clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Io exception thrown by client socket: " + clientSocket.getLocalPort());
        } finally {
            synchronizedSet.remove(clientSocket);
            synchronizedMap.remove(clientSocket);
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Io exception thrown by client socket while closing it: " + clientSocket.getLocalAddress());
            }
            System.out.println("Client socket has been removed " + clientSocket.getLocalAddress());
        }

    }

    private static void broadCast(String msg, Socket clientSocket) {
        synchronized (synchronizedSet) {
            for (Socket socket : synchronizedSet) {
                if (clientSocket == socket) {
                    continue;
                }
                System.out.print("Sending message to client : " + socket.getPort());
                System.out.println(" From client : " + clientSocket.getPort());
                PrintWriter printOut = synchronizedMap.getOrDefault(socket, null);
                if (printOut != null) {
                    try {
                        System.out.println("printOut is not null");
                        printOut.println(msg);
                    } catch (Exception e) {
                        System.out.println("Io exception thrown by client socket: " + clientSocket.getLocalAddress());
                        synchronizedSet.remove(socket);
                        synchronizedMap.remove(socket);
                    }
                }

            }
        }

    }
}