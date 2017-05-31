package jpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jputils.Constants;

/** I used the code sample found in an Oracle tutorial on sockets
 * 
 *
 */

public class Server {
    private static final int NUM_WORKERS = 7;

    private static void run(int portNumber) {
        ExecutorService executor = null;

        try (ServerSocket serverSocket = new ServerSocket(portNumber);) {
            executor = Executors.newFixedThreadPool(NUM_WORKERS);
            System.out.println("Waiting for clients");

            while (true) {
                final Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());
                final Runnable worker = new RequestHandler(clientSocket);
                executor.execute(worker);
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        } finally {
            if (executor != null) {
                executor.shutdown();
            }
        }
        
    }

    public static void main(String[] args) {
        run(Constants.PORT);
    }
}
