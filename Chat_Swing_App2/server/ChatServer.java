import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

    private static final int PORT = 5000;
    private static Set<PrintWriter> clientWriters = new HashSet<>();

    public static void main(String[] args) throws Exception {
        System.out.println("Server started on port " + PORT);
        ServerSocket serverSocket = new ServerSocket(PORT);

        while (true) {
            Socket client = serverSocket.accept();
            PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
            clientWriters.add(writer);

            new Thread(() -> handleClient(client, writer)).start();
        }
    }

    private static void handleClient(Socket client, PrintWriter writer) {
        try {
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(client.getInputStream())
            );

            String message;
            while ((message = reader.readLine()) != null) {
                // ❗ EXCLUDE SENDER
                broadcast(message, writer);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            clientWriters.remove(writer);
        }
    }

    // ❗ FIXED BROADCAST
    private static void broadcast(String message, PrintWriter sender) {
        for (PrintWriter writer : clientWriters) {
            if (writer != sender) { // exclude sender
                writer.println(message);
            }
        }
    }
}
