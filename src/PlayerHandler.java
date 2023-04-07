import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PlayerHandler implements Runnable {
    final Socket client;
    final BufferedReader in;
    final PrintWriter out;
    final Server server;
    public SingleHangmanGame singleGame;
    public MultiHangmanGame multiGame;
    public String username;
    public int score;

    public boolean endGame = false;

    public boolean chosen = false;

    public boolean isChosen() {
        return chosen;
    }

    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }

    public boolean isEndGame() {
        return endGame;
    }

    public void setEndGame(boolean endGame) {
        this.endGame = endGame;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public PlayerHandler(Socket clientSocket) throws IOException {

        this.server = new Server();
        this.client = clientSocket;
        this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.out = new PrintWriter(client.getOutputStream(), true);
        this.singleGame = new SingleHangmanGame();
        this.multiGame = new MultiHangmanGame();
    }

    @Override
    public void run() {
        try {

            while (true) {

                out.println("Choose an Option");
                out.println("1) Register \n" + "2) Login \n" + "press - to exit");

                String request = in.readLine();

                while (!request.equals("-")) {

                    if (request.equals("1")) {
                        // register
                        server.register(this);
                        while (server.isRegister()) {
                            out.println("1) Single Player Hangman \n" + "2) Multiplayer Hangman");

                            String request2 = in.readLine();

                            if (request2.equals("1")) {
                                // single player
                                singleGame.startGame(this);

                            } else if (request2.equals("2")) {
                                // multiplayer
                                server.joinRoom(this);
                                if (server.getMultiPlayers().size() != 4) {
                                    out.println("Waiting for other players to join");
                                }
                                while (true) {
                                    if (server.getMultiPlayers().size() == 4) break;
                                }
                                multiGame.startGame();

                            } else {
                                out.println("Invalid Input");

                            }
                        }
                    } else if (request.equals("2")) {
                        // login
                        server.login(this);
                        while (server.isLogin()) {
                            out.println("1) Single Player Hangman \n" + "2) Multiplayer Hangman \n"
                                    + "3) User history \n" + "press - to exit");

                            String request2 = in.readLine();

                            if (request2.equals("1")) {
                                // single player
                                singleGame.startGame(this);
                            } else if (request2.equals("2")) {
                                // multiplayer
                                server.joinRoom(this);
                                if (server.getMultiPlayers().size() != 4) {
                                    out.println("Waiting for other players to join");
                                }
                                while (true) {
                                    if (server.getMultiPlayers().size() == 4) break;
                                }
                                multiGame.startGame();

                            } else if (request2.equals("3")) {
                                // user history
                                out.println("1) Single Player history \n" + "2) Multiplayer History");

                                String request3 = "";
                                try {
                                    request3 = in.readLine();
                                } catch (IOException ex) {
                                    System.out.println("Error reading message");
                                }

                                if (request3.equals("1")) {
                                    singleGame.readScore(this);
                                } else if (request3.equals("2")) {
                                    multiGame.readScore(this);
                                } else {
                                    out.println("Invalid Input");
                                    //break;
                                }
                            } else if (request2.equals("-")) {
                                server.setLogin(false);
                            } else {
                                out.println("Invalid Input");
                                break;
                            }
                        }
                    } else {
                        out.println("Invalid Input");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("client disconnected");
        } finally {

            out.close();
            try {
                in.close();
            } catch (IOException e) {
                System.out.println("client disconnected");
            }
            try {
                client.close();
            } catch (IOException e) {
                System.out.println("client disconnected");
            }
        }
    }
}
