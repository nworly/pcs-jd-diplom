import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Server {
    protected int port;
    protected BooleanSearchEngine engine;

    public Server(int port, BooleanSearchEngine engine) {
        this.port = port;
        this.engine = engine;
    }

    public void start() {
        System.out.println("Starting server at " + port + "...");

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder
                    .setPrettyPrinting()
                    .create();

            while (true) {

                try (
                        Socket clientSocket = serverSocket.accept();
                        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
                ) {
                    String word = in.readLine();

                    List<PageEntry> pageEntries = engine.search(word);

                    String answer = gson.toJson(pageEntries);

                    byte[] answerInBytes = answer.getBytes(StandardCharsets.UTF_8);

                    out.writeInt(answerInBytes.length);
                    out.write(answerInBytes);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
