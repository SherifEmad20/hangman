import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class MultiHangmanGame {
    static Random random = new Random();
    private final GameRoom gameRoom;
    int team1Score = 5;
    int team2Score = 5;

    public boolean endGame = false;

    public MultiHangmanGame() {
        this.gameRoom = new GameRoom();
    }

    public ArrayList<String> loadMovies() {
        ArrayList<String> movieList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("movies.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                movieList.add(line.toLowerCase());
            }
        } catch (IOException e) {
            System.out.println("Error loading movies");
        }
        return movieList;
    }

    public int getTeam1Score() {
        if (team1Score <= 0) {
            team1Score = 0;
        }
        return team1Score;
    }

    public int getTeam2Score() {
        if (team2Score <= 0) {
            team2Score = 0;
        }
        return team2Score;
    }

    public void startGame() {
        gameRoom.setPlayers(Server.getMultiPlayers());
        ArrayList<PlayerHandler> players = gameRoom.getPlayers();
        try {
            while (gameRoom.getPlayers().size() != 4) {
                toAll(players, "Waiting for more players to join...");
                for (int i = 0; i < players.size(); i++) {
                    toAll(players, "Players in the room: " + players.get(i).getUsername());
                }
            }

            if (players.size() == 4) {
                toAll(players, "Game is starting...");
                toAll(players, "Welcome to Hangman Game!");
                for (int i = 0; i < players.size(); i++) {
                    toAll(players, "Players in room:");
                    toAll(players, (i + 1) + ") " + players.get(i).getUsername());
                }

                ArrayList<String> board = new ArrayList<>();
                ArrayList<String> guessedLetters = new ArrayList<>();

                int rand = random.nextInt(loadMovies().size());
                String movie = loadMovies().get(rand);

                toAll(players, "movie to guess: " + movie);

                for (int i = 0; i < movie.length(); i++) {
                    if (movie.charAt(i) == ' ') {
                        board.add(" ");
                    } else board.add("_");
                }

                int turn = 0; // counter variable for players' turns
                int numberOfPlayers = players.size();
                int team1Turn = 0;
                int team2Turn = 0;
                int team1Attempts = 6;
                int team2Attempts = 6;

                PlayerHandler currentPlayer = players.get(turn);
                currentPlayer.setChosen(true);

                currentPlayer.out.println("Choose your teammate: ");

                String teamMate = currentPlayer.in.readLine();

                while (numberOfPlayers != 0) {
                    for (int i = 0; i < players.size(); i++) {
                        if (players.get(i).getUsername().equalsIgnoreCase(teamMate)) {
                            gameRoom.setTeam1(currentPlayer, players.get(i));
                            players.get(i).setChosen(true);
                            players.get(i).out.println(currentPlayer.getUsername() + " chose you as his teammate");
                        }
                    }

                    // rotate to the next player's turn
                    turn = (turn + 1) % players.size();
                    currentPlayer = players.get(turn);

                    // if the current is already in a team rotate again
                    if (currentPlayer.isChosen()) {
                        turn = (turn + 1) % players.size();
                    }

                    currentPlayer = players.get(turn);
                    currentPlayer.out.println("Choose your teammate: ");

                    teamMate = currentPlayer.in.readLine();

                    for (int i = 0; i < players.size(); i++) {
                        if (players.get(i).getUsername().equalsIgnoreCase(teamMate)) {
                            gameRoom.setTeam2(currentPlayer, players.get(i));
                            players.get(i).out.println(currentPlayer.getUsername() + " chose you as his teammate");
                        }
                    }

                    // if team 1 is full, break the loop
                    if (gameRoom.team1.size() == 2) {
                        break;
                    }

                    // if team 2 is full, break the loop
                    if (gameRoom.team2.size() == 2) {
                        break;
                    }

                    // if no condition is met keep iterating
                    numberOfPlayers--;
                }

                toAll(players, "Team 1: " + gameRoom.getTeam1().get(0).getUsername() + " and "
                        + gameRoom.getTeam1().get(1).getUsername());

                toAll(players, "Team 2: " + gameRoom.getTeam2().get(0).getUsername() + " and "
                        + gameRoom.getTeam2().get(1).getUsername());


                ArrayList<PlayerHandler> team1 = gameRoom.getTeam1();
                ArrayList<PlayerHandler> team2 = gameRoom.getTeam2();

                gameRoom.setTeam1Turn(true);
                while (true) {
                    boolean correct = false;
                    boolean winner = true;
                    boolean lose = false;

                    // team 1
                    if (gameRoom.isTeam1Turn()) {
                        currentPlayer = team1.get(team1Turn);
                        currentPlayer.out.println("Your number of attempts is: " + team1Attempts);
                        printBoard(board, currentPlayer.out);

                        currentPlayer.out.println("Enter character: ");
                        String character = currentPlayer.in.readLine();

                        if (guessedLetters.contains(character)) {
                            currentPlayer.out.println("You have already guessed this character! Please guess another character.");
                            team1Turn = (team1Turn + 1) % team1.size(); // rotate to the next player's turn
                            continue;
                        } else {
                            guessedLetters.add(character);
                        }

                        for (int i = 0; i < movie.length(); i++) {
                            String movieLetter = Character.toString(movie.charAt(i));
                            if (movieLetter.equalsIgnoreCase(character)) {
                                correct = true;
                                board.set(i, String.valueOf(character));
                                gameRoom.setTeam1Score(team1Score += 5);
                                team1Turn = (team1Turn + 1) % team1.size(); // rotate to the next player's turn
                            }

                        }

                        if (!correct) {
                            currentPlayer.out.println("Wrong character! Attempts left: " + (--team1Attempts));
                            gameRoom.setTeam1Score(team1Score -= 5);
                            gameRoom.setTeam1Turn(false);
                            gameRoom.setTeam2Turn(true);
                        }


                        for (int k = 0; k < movie.length(); k++) {
                            if (board.get(k).equals("_")) {
                                winner = false;
                            }
                        }

                        printBoard(board, currentPlayer.out);

                        if (team1Attempts == 0) {
                            lose = true;
                        }

                        if (winner) {
                            toAll(players, "Game Over!");
                            toAll(players, "Team 1 won!");
                            gameRoom.setTeam1Score(getTeam1Score());
                            toAll(players, "Team 1 score is: " + gameRoom.getTeam1Score());
                            writeScore(team1, gameRoom.getTeam1Score());
                            break;
                        }

                        if (lose) {
                            toAll(players, "Game Over!");
                            toAll(players, "Team 1 lost!");
                            toAll(players, "The word was: " + movie);
                            gameRoom.setTeam1Score(getTeam1Score());
                            toAll(players, "Team 1 score is: " + gameRoom.getTeam1Score());
                            writeScore(team1, gameRoom.getTeam1Score());
                            break;
                        }
                    }

                    // team 2
                    else if (gameRoom.isTeam2Turn()) {
                        currentPlayer = team2.get(team2Turn);
                        currentPlayer.out.println("Your number of attempts is: " + team2Attempts);
                        printBoard(board, currentPlayer.out);

                        currentPlayer.out.println("Enter character: ");
                        String character = currentPlayer.in.readLine();

                        if (guessedLetters.contains(character)) {
                            currentPlayer.out.println("You have already guessed this character! Please guess another character.");
                            team2Turn = (team2Turn + 1) % team2.size(); // rotate to the next player's turn
                            continue;
                        } else {
                            guessedLetters.add(character);
                        }


                        for (int i = 0; i < movie.length(); i++) {
                            String movieLetter = Character.toString(movie.charAt(i));
                            if (movieLetter.equalsIgnoreCase(character)) {
                                correct = true;
                                board.set(i, String.valueOf(character));
                                gameRoom.setTeam2Score(team2Score += 5);
                                team2Turn = (team2Turn + 1) % team2.size(); // rotate to the next player's turn
                            }
                        }

                        if (!correct) {
                            currentPlayer.out.println("Wrong character! Attempts left: " + (--team2Attempts));
                            gameRoom.setTeam2Score(team2Score -= 5);
                            gameRoom.setTeam2Turn(false);
                            gameRoom.setTeam1Turn(true);
                        }

                        for (int k = 0; k < movie.length(); k++) {
                            if (board.get(k).equals("_")) {
                                winner = false;
                            }
                        }

                        printBoard(board, currentPlayer.out);

                        if (team2Attempts == 0) {
                            lose = true;
                        }

                        if (winner) {
                            toAll(players, "Game Over!");
                            toAll(players, "Team 2 won!");
                            gameRoom.setTeam2Score(getTeam2Score());
                            toAll(players, "Team 2 score is: " + gameRoom.getTeam2Score());
                            writeScore(team2, gameRoom.getTeam2Score());
                            break;
                        }

                        if (lose) {
                            toAll(players, "Game Over!");
                            toAll(players, "Team 2 lost!");
                            toAll(players, "The word was: " + movie);
                            gameRoom.setTeam2Score(getTeam2Score());
                            toAll(players, "Team 2 score is: " + gameRoom.getTeam2Score());
                            writeScore(team2, gameRoom.getTeam2Score());
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            for (PlayerHandler player : players) {
                player.out.println("Error reading input from user");
            }
        }

    }

    public void toAll(ArrayList<PlayerHandler> players, String message) {
        for (int i = 0; i < players.size(); i++) {
            players.get(i).out.println(message);
        }
    }

    public void printBoard(ArrayList<String> board, PrintWriter out) {
        for (int i = 0; i < board.size(); i++) {
            out.print(board.get(i));
        }
        out.println();
    }

    public void writeScore(ArrayList<PlayerHandler> players, int score) {
        try {
            FileWriter fileWriter = new FileWriter("multiPlayerScore.txt", true);

            for (int i = 0; i < players.size(); i++) {
                fileWriter.write("[");
                String content = players.get(i).getUsername() + "|" + score;
                fileWriter.write(content);
                fileWriter.write("]");
                fileWriter.write(System.lineSeparator());
            }
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: ");
        }
    }

    public void readScore(PlayerHandler player) {
        boolean found = false;
        try (BufferedReader br = new BufferedReader(new FileReader("multiPlayerScore.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(player.getUsername())) {
                    player.out.println(line);
                    found = true;
                } else continue;
            }
            if (!found) player.out.println("No history found");
        } catch (IOException e) {
            System.out.println("Error retrieving player history");
        }
    }
}


