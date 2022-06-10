import java.io.File;

public class Main {
    public static void main(String[] args) {

        try {
            BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));

            Server server = new Server(8989, engine);
            server.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}