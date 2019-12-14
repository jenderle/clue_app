import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

/**
 * Created by Jonathan Enderle on 11/21/2016.
 * Player class for clue solver app
 */
public class Player {

    // TODO expand name reading/recognizing capabilities

    /**
     * List of guesses that the player has proven wrong with a card from their hand
     */
    private ArrayList<Boolean[]> guessList = new ArrayList<>();

    /**
     * List of card indices proven to be in possession of *any* player
     */
    private static ArrayList<Integer> provenCards = new ArrayList<>();

    /**
     * List of cards in this player's hand
     */
    private ArrayList<Integer> hand = new ArrayList<>();

    /**
     * Number of cards the player has in their hand; determined by game setup
     */
    public final int MAX_HAND_SIZE;

    /**
     * ID corresponding to the player
     */
    public final String PLAYER_ID;

    /**
     * Player constructor
     * @param playerID ID corresponding to the player
     * @param maxHandSize Number of cards the player has in their hand
     */
    public Player(String playerID, int maxHandSize) {
        PLAYER_ID = playerID;
        MAX_HAND_SIZE = maxHandSize;
    }

    /**
     * Constructor for the solution cards hand
     */
    public Player() {
        this("Solution Cards", 3);
    }

    /**
     * player tester module
     * @param args ignored
     */
    public static void main(String[] args) {
        Player jonathan = new Player("Jonathan", 5);
        System.out.println("adding jonathan's guesses...");
        jonathan.addGuess();
        System.out.println(jonathan.hand);
    }

    /**
     * Adds a card to this player's hand
     * directly adds it using the internally defined card IDs (helper method)
     * @param cardIndex internal cardID
     */
    private void addCard(int cardIndex) {
        if (hand.size() >= MAX_HAND_SIZE) {
            System.out.println("Error: should not have any more cards.");
        } else if(provenCards.indexOf(cardIndex) != -1) {
            System.out.println("Error: adding a card that is already in another player's hand.");
        } else {
            // Add card to hand and proven cards, sorting for later use
            hand.add(cardIndex);
            provenCards.add(cardIndex);
            Collections.sort(hand);
            Collections.sort(provenCards);
        }
    }

    /**
     * Adds a card to this player's hand
     * Interfaces with user to get input
     */
    public void addCard() {
        System.out.println("Please enter the name of the card in camelCase: ");
        String cardName = Player.getInput();
        int cardIndex = Player.getCardID(cardName);
        if(hand.size() >= MAX_HAND_SIZE) {
            System.out.println("Error: should not have any more cards");
        } else if(provenCards.indexOf(cardIndex) != -1) {
            System.out.println("Error: adding a card that is already in another player's hand.");
        } else {
            // Add card to hand and proven cards, sorting for later use
            hand.add(cardIndex);
            provenCards.add(cardIndex);
            Collections.sort(hand);
            Collections.sort(provenCards);
            System.out.println(this.toString());
        } // end if-statement
    } // end addCard()

    /**
     * Adds a guess that the player has proven wrong with a card from their hand
     */
    public void addGuess() {
        // Get card info
        System.out.println("Please enter the suspect: ");
        String personName = Player.getPersonInput();
        System.out.println("Please enter the weapon: ");
        String weaponName = Player.getWeaponInput();
        System.out.println("Please enter the room: ");
        String roomName = Player.getRoomInput();

        // Create array to track guess
        Boolean[] thisGuess = new Boolean[21];
        for(int i = 0; i<21; i++) { // initialize it all to false
            thisGuess[i] = false;
        }
        thisGuess[getCardID(personName)] = true;
        thisGuess[getCardID(weaponName)] = true;
        thisGuess[getCardID(roomName)] = true;
        guessList.add(thisGuess);

        this.inferCard();
    }

    /**
     * Looks at owned cards and the guesses that the player has proven wrong to infer ownership of new cards
     */
    public void inferCard() {
        // TODO infer card from two guesses when only one is left in hand i.e. 1,2,3 & 3,4,5 -> owns 3
        // TODO infer card from three guesses when only 2 left in hand, etc...
        for(int i = 0; i< guessList.size(); i++) {
            Boolean[] guess = guessList.get(i);
            int provenIndex = 0; // keep track of where we are in proven cards list
            int guessCount = 0; // track number of cards that we don't know the ownership of per guess
            int index = 0;
            int indexToAdd = -1; // index of the card to add, if it meets the criteria. defaults to -1 for debugging

            // Find the unowned cards for each guess
            ArrayList<Integer> provenMinusOwned = Player.subtract(provenCards, hand);
            for(Boolean g : guess) {
                if(index != provenMinusOwned.get(provenIndex)) { // translation: if this card is not known to be owned
                    if (g) { // transl.: if this card was a part of the guess (element is true if it was in the guess)
                        guessCount++;
                        indexToAdd = index; // registers this card as one potentially belonging to the player
                        // it'll be overwritten if there's more than one, but if there's more than one in the guess
                        // then we won't be adding it anyway
                    }
                } else if(index == provenMinusOwned.get(provenIndex)){
                    // it matched a card that we already knew belonged to someone; ignore it
                    provenIndex++;

                    //TODO quick & dirty hack
                    if(provenIndex == provenMinusOwned.size()) {
                        provenIndex--; // make it so we don't access out-of-array elements
                    }
                }
                index++;
            }

            // If there is only one card in the guess that is unowned, it must belong to the player
            if (guessCount == 1) {
                System.out.println(this.PLAYER_ID + " has the card: " + Player.getCardName(indexToAdd));
                this.addCard(indexToAdd);
            }
        }
    }

    /**
     * @return ArrayList a minus ArrayList b
     * Used in inferCard() to get the proven cards minus the cards the user actually owns
     * Precondition: B must be a subset of A
     * Precondition: A and B must be sorted
     */
    private static ArrayList<Integer> subtract(ArrayList<Integer> a, ArrayList<Integer> b) {
        int aIndex; // = 0
        int bIndex = 0;
        ArrayList<Integer> c = new ArrayList<>();
        if(b.size() > 0) {
            for (aIndex = 0; aIndex < a.size(); aIndex++) {
                if (a.get(aIndex).equals(b.get(bIndex))) { // If element is in both arrays
                    bIndex++;
                } else { // element is only in array A
                    c.add(a.get(aIndex)); // was gonna un-box using intValue() but IDEA said it was unnecessary
                }
            }
        } else {
            c = a;
        }
        return c;
    }

    /**
     * Removes a card from this player's hand
     * Interfaces with user to get input
     */
    public void removeCard() {
        System.out.println("Please enter the name of the card in camelCase: ");
        if(hand.size() <= 0) {
            System.out.println("Error: player does not have any cards");
        } else {
            String cardName = Player.getInput();
            int cardID = Player.getCardID(cardName);

            // Remove card to hand and proven cards
            // todo check arraylist indexOf() method
            int indexToRemove = hand.indexOf(cardID);
            hand.remove(indexToRemove);
            indexToRemove = provenCards.indexOf(cardID);
            provenCards.remove(indexToRemove);
        }
    }

    /**
     * Prints the list of guesses for the player
     */
    public void displayGuessList() {
        for(Boolean[] guess : this.guessList) {
            System.out.println(Arrays.deepToString(guess));
        }
    }

    /**
     * Gets valid input. Needs to be preceded by a prompt for information.
     * @return valid card name
     */
    private static String getInput() {
        Scanner in = new Scanner(System.in);
        String cardName = in.nextLine();
        boolean validInput = false;
        while(!validInput) {
            switch(cardName) {
                case "colonelMustard":
                case "profPlum":
                case "mrGreen":
                case "mrsPeacock":
                case "missScarlet":
                case "mrsWhite":
                case "knife":
                case "candlestick":
                case "revolver":
                case "rope":
                case "leadPipe":
                case "wrench":
                case "hall":
                case "lounge":
                case "diningRoom":
                case "kitchen":
                case "ballRoom":
                case "conservatory":
                case "billiardRoom":
                case "library":
                case "study":
                    validInput = true;
                    break;
                default:
                    System.out.println("Invalid input. Please try again: ");
                    cardName = in.nextLine();
                    break;
            } // end switch
        } // end while-loop
        return cardName;
    }

    /**
     * Gets valid input. Needs to be preceded by a prompt for information.
     * @return valid person card name
     */
    private static String getPersonInput() {
        Scanner in = new Scanner(System.in);
        String cardName = in.nextLine();
        boolean validInput = false;
        while(!validInput) {
            switch(cardName) {
                case "colonelMustard":
                case "profPlum":
                case "mrGreen":
                case "mrsPeacock":
                case "missScarlet":
                case "mrsWhite":
                    validInput = true;
                    break;
                default:
                    System.out.println("Invalid input. Please try again: ");
                    cardName = in.nextLine();
                    break;
            } // end switch
        } // end while-loop
        return cardName;
    }

    /**
     * Gets valid input. Needs to be preceded by a prompt for information.
     * @return valid weapon card name
     */
    private static String getWeaponInput() {
        Scanner in = new Scanner(System.in);
        String cardName = in.nextLine();
        boolean validInput = false;
        while(!validInput) {
            switch(cardName) {
                case "knife":
                case "candlestick":
                case "revolver":
                case "rope":
                case "leadPipe":
                case "wrench":
                    validInput = true;
                    break;
                default:
                    System.out.println("Invalid input. Please try again: ");
                    cardName = in.nextLine();
                    break;
            } // end switch
        } // end while-loop
        return cardName;
    }

    /**
     * Gets valid input. Needs to be preceded by a prompt for information.
     * @return valid room card name
     */
    private static String getRoomInput() {
        Scanner in = new Scanner(System.in);
        String cardName = in.nextLine();
        boolean validInput = false;
        while(!validInput) {
            switch(cardName) {
                case "hall":
                case "lounge":
                case "diningRoom":
                case "kitchen":
                case "ballRoom":
                case "conservatory":
                case "billiardRoom":
                case "library":
                case "study":
                    validInput = true;
                    break;
                default:
                    System.out.println("Invalid input. Please try again: ");
                    cardName = in.nextLine();
                    break;
            } // end switch
        } // end while-loop
        return cardName;
    }

    /**
     * Converts a name of a card in string form to a number for putting into the array
     * @param cardName name of the card
     * @return ID of card in numeric form
     */
    private static int getCardID(String cardName) {
        int cardID = -1; // Default to -1 for debugging
        switch (cardName) {
            case "colonelMustard":
                cardID = 0;
                break;
            case "profPlum":
                cardID = 1;
                break;
            case "mrGreen":
                cardID = 2;
                break;
            case "mrsPeacock":
                cardID = 3;
                break;
            case "missScarlet":
                cardID = 4;
                break;
            case "mrsWhite":
                cardID = 5;
                break;
            case "knife":
                cardID = 6;
                break;
            case "candlestick":
                cardID = 7;
                break;
            case "revolver":
                cardID = 8;
                break;
            case "rope":
                cardID = 9;
                break;
            case "leadPipe":
                cardID = 10;
                break;
            case "wrench":
                cardID = 11;
                break;
            case "hall":
                cardID = 12;
                 break;
            case "lounge":
                cardID = 13;
                break;
            case "diningRoom":
                cardID = 14;
                break;
            case "kitchen":
                cardID = 15;
                break;
            case "ballRoom":
                cardID = 16;
                break;
            case "conservatory":
                cardID = 17;
                break;
            case "billiardRoom":
                cardID = 18;
                break;
            case "library":
                cardID = 19;
                break;
            case "study":
                cardID = 20;
                break;
        }
        return cardID;
    }

    /**
     * Converts an index to the name of the corresponding card
     * @param cardID card index
     * @return name of card in string form
     */
    private static String getCardName(int cardID) {
        String cardName = null;
        switch(cardID) {
            case 0:
                cardName = "colonelMustard";
                break;
            case 1:
                cardName = "profPlum";
                break;
            case 2:
                cardName = "mrGreen";
                break;
            case 3:
                cardName = "mrsPeacock";
                break;
            case 4:
                cardName = "missScarlet";
                break;
            case 5:
                cardName = "mrsWhite";
                break;
            case 6:
                cardName = "knife";
                break;
            case 7:
                cardName = "candlestick";
                break;
            case 8:
                cardName = "revolver";
                break;
            case 9:
                cardName = "rope";
                break;
            case 10:
                cardName = "leadPipe";
                break;
            case 11:
                cardName = "wrench";
                break;
            case 12:
                cardName = "hall";
                break;
            case 13:
                cardName = "lounge";
                break;
            case 14:
                cardName = "diningRoom";
                break;
            case 15:
                cardName = "kitchen";
                break;
            case 16:
                cardName = "ballRoom";
                break;
            case 17:
                cardName = "conservatory";
                break;
            case 18:
                cardName = "billiardRoom";
                break;
            case 19:
                cardName = "library";
                break;
            case 20:
                cardName = "study";
                break;
        }
        return cardName;
    }

    /**
     * toString method. Prints out name, hand size, and cards in hand
     */
    public String toString() {
        String out = "" + PLAYER_ID + " has a hand of size " + MAX_HAND_SIZE + " , containing: ";
        for(int id : hand) {
            out += "" + Player.getCardName(id) + ", ";
        }
        return out;
    }
}
