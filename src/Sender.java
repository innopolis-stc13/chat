import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Sender extends Thread {

    private Socket socket;
    private String user;

    public Sender(Socket socket, String user) {
        this.socket = socket;
        this.user = user;
    }

    @Override
    public void run() {
        try (OutputStreamWriter outputStream = new OutputStreamWriter(socket.getOutputStream())) {
            Scanner scanner = new Scanner(System.in);
            String str;
            LocalTime time;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            while (socket != null) {
                if (scanner.hasNextLine()) {
                    str = scanner.nextLine();
                    time = LocalTime.now();
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStream);
                    bufferedWriter.write(time.format(formatter) + " " + user + " writes: " + str);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
