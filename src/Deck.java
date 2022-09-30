import java.util.*;

public class Deck {

    private ArrayList<Card> cards;

    /**
     * Generates a randomly shuffled deck of cards.
     */
    public Deck() {
        cards = new ArrayList<>();
        for (int rank = 1; rank <= 13; rank++) {
            for (int suit = 0; suit <= 3; suit++) {
                cards.add(new Card(rank, suit));
            }
            Collections.shuffle(cards);
        }
    }

    public Deck(String dealCode) {
        cards = new ArrayList<>();
        for (int i = 0; i < 52; i++) {
            String ranks = "A23456789TJQK";
            String suits = "SHCD";
            int rank = ranks.indexOf(dealCode.charAt(2 * i)) + 1;
            int suit = suits.indexOf(dealCode.charAt(2 * i + 1));
            cards.add(new Card(rank, suit));
        }
    }

    public Card getCardAt(int index) {
        return cards.get(index);
    }

    public void flipCardAt(int index) {
        cards.get(index).flip();
    }
}
