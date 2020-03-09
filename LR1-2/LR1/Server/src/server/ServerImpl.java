package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.util.*;

public class ServerImpl {

    private final static int PORT = 54321;
    private final static String SEPARATOR = ";";
    private final static String LINE_BREAK = "\n";
    private final static String EXIT = "exit";
    private final static String GET_ALL_TICKETS = "getAllTickets";
    private final static String BUY_TICKET = "buyTicket";
    private final static String GET_LOGS = "getLogs";
    private final static String EQUAL_SIGN = "=";
    private final static String ERROR_MESSAGE = "Can not execute this command";

    private static List<String> logRequests = new LinkedList<>();
    private static List<String> logResponses = new LinkedList<>();
    private static Map<LocalDate, List<String>> tickets;

    private static PrintWriter writer;
    private static BufferedReader reader;

    private static void init() {
        tickets = new HashMap<>();
        tickets.put(LocalDate.parse("2020-02-20"), new ArrayList<>(Arrays.asList("on 12 o'clock")));
        tickets.put(LocalDate.parse("2020-02-21"), new ArrayList<>(Arrays.asList("on 1 o'clock")));
        tickets.put(LocalDate.parse("2020-02-22"), new ArrayList<>(Arrays.asList("on 2 o'clock")));
        tickets.put(LocalDate.parse("2020-02-23"), new ArrayList<>(Arrays.asList("on 3 o'clock")));
    }


    public static void main(String[] args) {

        init();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            Socket socket = serverSocket.accept();

            while (true) {
                writer = new PrintWriter(socket.getOutputStream(), true);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String command = reader.readLine();

                if (command.equals(EXIT))
                    break;

                executeCommand(command);

                writer.flush();

            }
        } catch (UnsupportedOperationException | IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
        } finally {
            writer.close();
            try {
                reader.close();
            } catch (IOException ex) {
                System.out.println("Server exception: " + ex.getMessage());
            }
        }
    }



    private static void executeCommand(String command) {
        String[] commands = command.split(EQUAL_SIGN);
        if (commands.length != 1) {
            switch (commands[0]) {
                case GET_ALL_TICKETS:
                    getAllTicketsForSpecificDate(LocalDate.parse(commands[1]));
                    logRequests.add(command + LINE_BREAK);
                    break;
                case BUY_TICKET:
                    buyTicket(commands[1]);
                    logRequests.add(command + LINE_BREAK);
                    break;
            }
        } else
            if (commands[0].equals(GET_LOGS))
                writeLogs();
            else
                writer.println(ERROR_MESSAGE + LINE_BREAK);
    }

    private static void getAllTicketsForSpecificDate(LocalDate date) {
        StringBuilder temp = new StringBuilder();

        for (String ticket : tickets.get(date)) {
            temp.append(ticket).append(SEPARATOR).append(LINE_BREAK);
        }

        writer.println(temp);

        tickets.get(date).forEach(ticket -> logResponses.add(ticket + SEPARATOR));
    }

    private static void buyTicket(String command) {
        String[] data = command.split(SEPARATOR);
        List<String> dayTickets = tickets.get(LocalDate.parse(data[0]));
        if (dayTickets != null)
            dayTickets.add(data[1]);
        else
            dayTickets = List.of(data[1]);
        tickets.put(LocalDate.parse(data[0]), dayTickets);
        UUID uuid = UUID.randomUUID();
        writer.println(uuid + LINE_BREAK);

        logResponses.add(uuid.toString());
    }

    private static void writeLogs() {
        String temp = logRequests.toString() + logResponses.toString() + LINE_BREAK;
        writer.println(temp);
    }

}
