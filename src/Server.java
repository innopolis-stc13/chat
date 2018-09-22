import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    public final static Integer SERVER_PORT = 4999;
    private static List<Socket> sockets = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        System.out.println("Welcome to chat!\nAdd users using the \"Client\" class.");
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        Socket socket = serverSocket.accept();
                        if (socket != null) {
                            sockets.add(socket);
                            String socketName = getSocketName(socket);
                            ServerListener listener = new ServerListener(socket);
                            LocalTime time;
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                            time = LocalTime.now();
                            listener.sendMessage(time.format(formatter) + ": " + socketName + " online");
                            listener.start();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            thread.join();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String getSocketName(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        return reader.readLine();
    }

    private static class ServerListener extends Thread {
        private Socket socket;

        private ServerListener(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (InputStream inputStream = socket.getInputStream()) {
                BufferedReader clientReader = new BufferedReader(new InputStreamReader(inputStream));
                String message;
                while (true) {
                    if (!(message = clientReader.readLine()).endsWith("quit")) {
                        sendMessage(message);
                    } else {
                        String[] containName = message.split(" ");

                        sendMessage(containName[1] + " left the chat");
                        sockets.remove(socket);
                        socket.close();
                        socket = null;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void sendMessage(String message) {
            for (Socket socket : sockets) {
                try {
                    OutputStream outputStream = socket.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
                    bufferedWriter.write(message);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
