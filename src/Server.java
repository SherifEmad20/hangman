import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static final int PORT = 7000;
    public static int numberOfThreads = 100;
    public static ArrayList<PlayerHandler> players = new ArrayList<>();
    public static ArrayList<PlayerHandler> multiPlayers = new ArrayList<>();
    public static ExecutorService pool = Executors.newFixedThreadPool(numberOfThreads);
    public boolean login = false;
    public boolean register = false;

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public boolean isRegister() {
        return register;
    }

    public void setRegister(boolean register) {
        this.register = register;
    }

    public static ArrayList<PlayerHandler> getMultiPlayers() {
        return multiPlayers;
    }

    public boolean register(PlayerHandler player) throws IOException {
        boolean flag = false;
        String clientMessage = "";
        boolean writerFlag = true;

        player.out.println("registration: ");
        player.out.println("Enter name: ");
        String name = player.in.readLine();

        player.out.println("Enter username: ");
        String username = player.in.readLine();

        player.out.println("Enter password: ");
        String password = player.in.readLine();

        try {
            BufferedReader br = new BufferedReader(new FileReader("database.txt"));
            // file to write in
            FileWriter fileWriter = new FileWriter("database.txt", true);

            // to get username from client message
            String[] elements = {name, username, password};

            // iterate through file line by line
            String line;

            while ((line = br.readLine()) != null) {

                // to get the username from file
                String[] data = line.split("\\|");
                String nameInFile = data[1];
                if (nameInFile.equals(username)) {
                    clientMessage = ("Username already exists!");
                    flag = false;
                    writerFlag = false;
                } else {
                    continue;
                }
            }


            if (writerFlag) {

                fileWriter.write("[");
                for (String s : elements) {
                    fileWriter.write(s + "|");
                }
                fileWriter.write("]");

                clientMessage = "Successful registration!";
                fileWriter.write(System.lineSeparator());
                fileWriter.close();
                flag = true;
                player.setUsername(username);
            }

        } catch (IOException e) {
            System.out.println("Error reading from file");
        }
        player.out.println(clientMessage);
        setRegister(flag);
        return flag;
    }

    public boolean login(PlayerHandler player) {
        String outMessage = "404 Not Found!";

        try {
            BufferedReader br = new BufferedReader(new FileReader("database.txt"));

            String fileLine = "";
            player.out.println("Login: ");
            player.out.println("Enter username: ");
            String username = player.in.readLine();

            player.out.println("Enter password: ");
            String password = player.in.readLine();

            while ((fileLine = br.readLine()) != null) {

                // data in file
                String[] data = fileLine.split("\\|");
                String name = data[1];
                String pass = data[2];

                if (name.equals(username) && pass.equals(password)) {
                    outMessage = ("Login Successful!");
                    player.out.println(outMessage);
                    setLogin(true);
                    player.setUsername(username);
                    return true;

                } else if (name.equals(username) && !pass.equals(password)) {
                    outMessage = ("401 Unauthorized!");
                    player.out.println(outMessage);
                    return false;
                } else {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        player.out.println(outMessage);
        return false;
    }

    public void joinRoom(PlayerHandler player) {
        multiPlayers.add(player);
    }

    public static void main(String[] args) {
        try {
            ServerSocket listener = new ServerSocket(PORT);
            while (true) {
                System.out.println("[SERVER] Running and Waiting for clients connection..");
                Socket playerSocket = listener.accept();

                System.out.println("[SERVER] Connected to a client");
                PlayerHandler playerThread = new PlayerHandler(playerSocket);
                players.add(playerThread);

                pool.execute(playerThread);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected");
        }
    }
}
