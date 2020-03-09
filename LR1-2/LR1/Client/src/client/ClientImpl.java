package client;

import java.io.*;
import java.net.Socket;

public class ClientImpl {

    private final static int PORT = 54321;
    private final static String HOST = "localhost";
    private final static String EXIT = "exit";
    private final static String LINE_BREAK = "\n";
    private final static String EMPTY_LINE = "";

    private static BufferedReader consoleReader;
    private static BufferedReader serverReader;
    private static BufferedWriter serverWriter;

    public static void main(String[] args) throws Exception {
        try (Socket clientSocket = new Socket(HOST, PORT)) {

            consoleReader = new BufferedReader(new InputStreamReader(System.in));
            serverReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            serverWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            while (true) {
                String input = consoleReader.readLine();
                if (input.equals(EXIT)) {
                    serverWriter.write(EXIT + LINE_BREAK);
                    break;
                }

                serverWriter.write(input + LINE_BREAK);
                serverWriter.flush();

                String output = serverReader.readLine();
                while (!output.equals(EMPTY_LINE)) {
                    System.out.println(output);
                    output = serverReader.readLine();
                }

            }

        } finally {
            serverWriter.close();
            try {
                serverReader.close();
                consoleReader.close();
            } catch (IOException ex) {
                System.out.println("Server exception: " + ex.getMessage());
            }
        }
    }
}
