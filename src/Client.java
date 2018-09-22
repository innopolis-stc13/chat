import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        System.out.println("What's your name?");
        Scanner scanner = new Scanner(System.in);
        String clientName = scanner.nextLine();
        try (Socket clientSocket = new Socket("127.0.0.1", Server.SERVER_PORT)) {
            Listener listener = new Listener(clientSocket);
            Sender sender = new Sender(clientSocket, clientName);
            sendSocketName(clientSocket, clientName);
            listener.start();
            sender.start();
            listener.join();
            sender.join();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void sendSocketName(Socket socket, String clientName) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        bufferedWriter.write(clientName);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }
}
