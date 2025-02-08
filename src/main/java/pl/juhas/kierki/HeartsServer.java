package pl.juhas.kierki;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class HeartsServer {
    private static final int PORT = 8000;
    private static final Set<ClientHandler> clientHandlers = ConcurrentHashMap.newKeySet();
    private static int clientIdCounter = 1;
    private static final Map<String, String> userCredentials = new ConcurrentHashMap<>();
    private static final Map<String, ClientHandler> clientSessions = new ConcurrentHashMap<>();

    private static  List<Room> rooms = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Hearts Server started...");
        loadUserCredentials("users.csv");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                String clientId = "client-" + clientIdCounter++;
                ClientHandler clientHandler = new ClientHandler(clientSocket, clientId);
                clientHandlers.add(clientHandler);

                System.out.println("New client connected: " + clientId);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadUserCredentials(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            userCredentials.putAll(br.lines()
                    .map(line -> line.split(","))
                    .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1])));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean authenticate(String username, String password) {
        return password.equals(userCredentials.get(username));
    }

    public static void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendMessage(message);
        }
    }

    public static List<Room> getRooms() {
        return rooms;
    }

    public static void setRooms(List<Room> rooms) {
        HeartsServer.rooms = rooms;
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private final String clientId;
        private PrintWriter out;
        private String username;

        public ClientHandler(Socket socket, String clientId) {
            this.socket = socket;
            this.clientId = clientId;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                out = new PrintWriter(socket.getOutputStream(), true);

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(clientId + ": " + message);
                    if (message.startsWith("LOGIN ")) {
                        handleLogin(message.substring(6));
                    } else if (message.startsWith("REGISTER ")) {
                        handleRegister(message.substring(9));
                    }else if (message.startsWith("CREATE_ROOM")) {
                        handleCreateRoom();
                    } else if (message.startsWith("GET_ROOMS")) {
                        handleGetRooms();
                    }
                    else {
                        HeartsServer.broadcastMessage("Error "+ clientId + ": " + message);
                    }
                }
            } catch (IOException e) {
                System.out.println("Client " + clientId + " disconnected.");
            } finally {
                clientHandlers.remove(this);
                if (username != null) {
                    clientSessions.remove(username);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleCreateRoom() {
            Room room = new Room(UUID.randomUUID().toString());
            rooms.add(room);
            broadcastMessage("ROOM_CREATED " + room.getId());
        }

        private void handleGetRooms() {
            String roomsString = rooms.stream()
                    .map(Room::getId)
                    .collect(Collectors.joining("\n"));
            sendMessage(roomsString);
        }

        private void handleLogin(String credentials) {
            String[] parts = credentials.split(" ");
            if (parts.length == 2 && HeartsServer.authenticate(parts[0], parts[1])) {
                username = parts[0];
                clientSessions.put(username, this);
                sendMessage("1");
            } else {
                sendMessage("0");
            }
        }

        private void handleRegister(String credentials) {
            String[] parts = credentials.split(" ");
            if (parts.length == 2 && !HeartsServer.authenticate(parts[0], parts[1])) {
                userCredentials.put(parts[0], parts[1]);
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.csv", true))) {
                    writer.write("\n" + parts[0] + "," + parts[1]);
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sendMessage("1");
            } else {
                sendMessage("0");
            }
        }

        public void sendMessage(String message) {
            if (out != null) {
                out.println(message);
            }
        }
    }
}