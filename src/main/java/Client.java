import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {

    public static void main(String[] args) {

        String wordToSearch = "бизнес";
        start(wordToSearch);

    }

    public static void start(String wordToSearch) {
        try (
                Socket socket = new Socket("localhost", 8989);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()))
        ) {
            out.println(wordToSearch);

            int length = in.readInt();

            byte[] answerByte = new byte[length];
            StringBuilder answer = new StringBuilder(length);
            int totalBytesRead = 0;

            int currentBytesRead = in.read(answerByte);
            totalBytesRead += currentBytesRead;

            if (totalBytesRead <= length) {
                answer.append(new String(answerByte, 0, currentBytesRead, StandardCharsets.UTF_8));
            } else {
                answer.append(new String(
                        answerByte,
                        0,
                        length - totalBytesRead + currentBytesRead,
                        StandardCharsets.UTF_8)
                );
            }

            System.out.println(answer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
