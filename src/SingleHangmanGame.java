import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class SingleHangmanGame {

    static Random random = new Random();

    int score = 0;

    public SingleHangmanGame() {
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
    public void setScore(int score) {
        this.score = score;
    }
    public int getScore() {
        if (score <= 0) {
            score = 0;
        }
        return score;
    }
    public void startGame(PlayerHandler player) {
        try {
            ArrayList<String> board = new ArrayList<>();
            ArrayList<String> guessedLetters = new ArrayList<>();

            int rand = random.nextInt(loadMovies().size());
            String movie = loadMovies().get(rand);

            for (int i = 0; i < movie.length(); i++) {
                if (movie.charAt(i) == ' ') {
                    board.add(" ");
                } else board.add("_");
            }

            int attempts = 6;

            player.out.println("Welcome to Hangman Game!");
            player.out.println("movie to guess: " + movie);

            player.out.println("Your number of attempts is: " + attempts);
            printBoard(board, player.out);

            while (true) {
                boolean correct = false;
                boolean winner = true;
                boolean lose = false;

                player.out.println("Enter character: ");
                String character = player.in.readLine();

                if (guessedLetters.contains(character)) {
                    player.out.println("You have already guessed this character! Please guess another character.");
                    continue;
                } else {
                    guessedLetters.add(character);
                }

                for (int i = 0; i < movie.length(); i++) {
                    String movieLetter = Character.toString(movie.charAt(i));
                    if (movieLetter.equalsIgnoreCase(character)) {
                        correct = true;
                        board.set(i, String.valueOf(character));
                        setScore(score += 5);
                    }
                }

                if (!correct) {
                    player.out.println("Wrong character! Attempts left: " + (--attempts));
                    setScore(score -= 5);
                }

                for (int k = 0; k < movie.length(); k++) {
                    if (board.get(k).equals("_")) {
                        winner = false;

                    }
                }

                printBoard(board, player.out);

                if (attempts == 0) {
                    lose = true;
                }

                if (winner) {
                    player.out.println("Game Over!");
                    player.out.println("You won!");
                    player.out.println("Your score is: " + getScore());

                    writeScore(player, getScore());

                    break;
                }

                if (lose) {
                    player.out.println("Game Over!");
                    player.out.println("You lost!");
                    player.out.println("The word was: " + movie);
                    player.out.println("Your score is: " + getScore());

                    writeScore(player, getScore());

                    break;
                }
            }
        } catch (IOException e) {
            player.out.println("Error reading input from user");
        }
    }

    public void printBoard(ArrayList<String> board, PrintWriter out) {
        for (int i = 0; i < board.size(); i++) {
            out.print(board.get(i));
        }
        out.println();
    }

    public void writeScore(PlayerHandler player, int score) {
        String playerScore = player.getUsername() + "|" + score;

        try (FileWriter fileWriter = new FileWriter("singlePlayerScore.txt", true);) {
            fileWriter.write("[");
            fileWriter.write(playerScore);
            fileWriter.write("]");

            fileWriter.write(System.lineSeparator());
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }

    public void readScore(PlayerHandler player) {
        boolean found = false;
        try (BufferedReader br = new BufferedReader(new FileReader("singlePlayerScore.txt"))) {
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