import java.util.Scanner;

/**
 * Created by Jonathan Enderle on 11/21/2016.
 * Game class for clue solver app. Uses Player class to keep track of cards in the game
 */
public class Game {
    /**
     * Roster that keeps track of all players in the game
     */
    private Player[] roster;

    /**
     * Main module. Run to use the clue solver app
     * @param args ignored
     */
    public static void main(String[] args) {
        Game clue = new Game();
        clue.play();
    }

    /**
     * Game constructor. Interfaces with player to get number of players, then loops through to construct those players
     */
    public Game() {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter the number of players: ");
        int numberOfPlayers = Game.getInt();
        roster = new Player[numberOfPlayers+1];
        roster[0] = new Player(); // Create 'hand' for the solution cards
        for(int i=1; i<numberOfPlayers+1; i++) {
            System.out.println("Please enter a name for player " + (i));
            String playerID = in.nextLine();
            System.out.println("Please enter the number of cards this player has:");
            int handSize = Game.getInt();
            roster[i] = new Player(playerID, handSize);
        }

        // Print out roster
        for(Player p : roster) {
            System.out.println(p.toString());
        }
    }

    /**
     * Core gameplay loop. Prompts user for action, and implements it using the Player class
     */
    private void play() {
        Scanner in = new Scanner(System.in);
        boolean keepGoing = true;
        System.out.println("Hit enter to continue: ");
        in.nextLine();

        while(keepGoing) {
            System.out.println("\nWhat would you like to do?");
            System.out.println("Enter '1' to add a card to someone's hand");
            System.out.println("Enter '2' to remove a card from someone's hand");
            System.out.println("Enter '3' to register a guess that a player proved wrong");
            System.out.println("Enter '4' to run a card check on all players");
            System.out.println("Enter '5' to print out a roster of all the players");
            System.out.println("Enter '6' to quit");
            String input = in.nextLine();

            int playerID;
            switch (input) {
                case "1":
                    System.out.println("Enter the number of the player that you'd like to add a card to:");
                    playerID = Game.getInt();
                    while(playerID > roster.length || playerID < 1) {
                        System.out.println("Player number out of range. Please try again: ");
                        playerID = Game.getInt();
                    }
                    roster[playerID].addCard();
                    break;
                case "2":
                    System.out.println("Enter the number of the player that you'd like to remove a card from:");
                    playerID = Game.getInt();
                    while(playerID > roster.length || playerID < 1) {
                        System.out.println("Player number out of range. Please try again: ");
                        playerID = Game.getInt();
                    }
                    roster[playerID].removeCard();
                    break;
                case "3":
                    System.out.println("Enter the number of the player that disproved the guess:");
                    playerID = Game.getInt();
                    while(playerID > roster.length || playerID < 1) {
                        System.out.println("Player number out of range. Please try again: ");
                        playerID = Game.getInt();
                    }
                    roster[playerID].addGuess();
                    break;
                case "4":
                    System.out.println("Checking guesses...");
                    for(Player p : roster) {
                        p.inferCard();
                    }
                    break;
                case "5":
                    for(Player p : roster) {
                        System.out.println(p.toString());
                    }
                    break;
                case "6":
                    keepGoing = false;
                    break;
                default:
                    System.out.println("Invalid input. Please try again: ");
                    break;
            }
        }
    }

    /**
     * Makes sure we get an int from the user
     * @return an int parsed from user input, w/o crashing
     */
    private static int getInt() {
        Scanner in = new Scanner(System.in);
        String input = in.nextLine();
        boolean validInput = false;
        while(!validInput) {
            validInput = true;
            for(int i = 0; i<input.length() && validInput; i++) {
                if(!Character.isDigit(input.charAt(i))) {
                    validInput = false;
                }
            }
            if(!validInput) {
                System.out.println("Invalid input. Please try again: ");
                input = in.nextLine();
            }

        }
        return Integer.parseInt(input);
    }
}
