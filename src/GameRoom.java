import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GameRoom {

    ArrayList<PlayerHandler> players = new ArrayList<>();

    ArrayList<PlayerHandler> team1 = new ArrayList<>();
    ArrayList<PlayerHandler> team2 = new ArrayList<>();

    int team1Score = 5;

    int team2Score = 5;

    boolean team1Turn = false;

    boolean team2Turn = false;

    public GameRoom() {
    }

    public ArrayList<PlayerHandler> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<PlayerHandler> players) {
        this.players = players;
    }


    public ArrayList<PlayerHandler> getTeam1() {
        return team1;
    }

    public void setTeam1(PlayerHandler player1, PlayerHandler player2) {
        this.team1.add(player1);
        this.team1.add(player2);
    }

    public ArrayList<PlayerHandler> getTeam2() {
        return team2;
    }

    public void setTeam2(PlayerHandler player1, PlayerHandler player2) {
        this.team2.add(player1);
        this.team2.add(player2);
    }

    public int getTeam1Score() {
        return team1Score;
    }

    public void setTeam1Score(int team1Score) {
        this.team1Score = team1Score;
    }

    public int getTeam2Score() {
        return team2Score;
    }

    public void setTeam2Score(int team2Score) {
        this.team2Score = team2Score;
    }

    public boolean isTeam1Turn() {
        return team1Turn;
    }

    public void setTeam1Turn(boolean team1Turn) {
        this.team1Turn = team1Turn;
    }

    public boolean isTeam2Turn() {
        return team2Turn;
    }

    public void setTeam2Turn(boolean team2Turn) {
        this.team2Turn = team2Turn;
    }

}
