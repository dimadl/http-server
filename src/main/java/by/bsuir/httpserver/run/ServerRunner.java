package by.bsuir.httpserver.run;

import by.bsuir.httpserver.config.Configuration;
import by.bsuir.httpserver.session.ClientSession;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The main class of the current application
 */
public class ServerRunner {

    //entry point
    public static void main(String[] args) {

        Configuration config = new Configuration();

        int port = config.getPort();

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port: " + serverSocket.getLocalPort());

        } catch (IOException e) {
            System.out.println("Port " + port + " is blocked.");
            System.exit(-1);
        }

        while (true) {

            try {
                Socket clientSocket = serverSocket.accept();

                ClientSession clientSession = new ClientSession(clientSocket);
                new Thread(clientSession).start();

            } catch (IOException e) {
                System.out.println("Failed to establish connection.");
                System.out.println(e.getMessage());
                System.exit(-1);
            }

        }

    }
}
