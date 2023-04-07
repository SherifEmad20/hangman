import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Player implements Runnable {
    public static final String IP = "127.0.0.1";
    public static final int PORT = 7000;
    public static BufferedReader in;
    static PrintWriter out;
    public boolean done;

    @Override
    public void run() {

        try {
            Socket socket = new Socket(IP, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out = new PrintWriter(socket.getOutputStream(), true);

            RequestHandler requestHandler = new RequestHandler();
            Thread t = new Thread(requestHandler);
            t.start();

            String inMessage;
            while (true) {
                if ((inMessage = in.readLine()) == null) break;
                System.out.println(inMessage);
            }
        } catch (IOException e) {
            System.out.println("Server is not reachable");
        }


    }

    public static void main(String[] args) {
        Player client = new Player();
        client.run();
    }


    public class RequestHandler implements Runnable {
        @Override
        public void run() {

            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                while (!done) {
                    String request = in.readLine();
                    if (request.equals("-")) {
                        in.close();
                        done = false;
                    } else {
                        out.println(request);
                    }

                }
            } catch (IOException e) {
                System.out.println("Connection closed");
            }

        }
    }
}

