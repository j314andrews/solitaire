public class Card {
    private int rank; // from 1 to 13, 1 = Ace, 11 = Jack, 12 = Queen, 13 = King
    private int suit; // 0 = Spade, 1 = Heart, 2 = Club, 3 = Diamond
    private boolean faceUp;

    public Card(int rank, int suit) {
        this(rank, suit, false);
    }

    public Card(int rank, int suit, boolean faceUp) {
        this.rank = rank;
        this.suit = suit;
        this.faceUp = faceUp;
    }

    public int getRank() {
        return rank;
    }

    public int getSuit() {
        return suit;
    }

    public boolean faceUp() {
        return faceUp;
    }

    public void flip() {
        faceUp = true;
    }
    
    public void unflip() {
    	faceUp = false;
    }

    public boolean equals(Object o) {
    	if (o instanceof Card) {
    		Card other = (Card) o;
    		return this.suit == other.suit && this.rank == other.rank;
    	} else {
    		return false;
    	}
    }

    public String toString() {
        if (!faceUp) {
            return "[///]";
        } else {
            String result = "[";
            if (rank == 1) {
                result += "A ";
            } else if (rank >= 2 && rank <= 9) {
                result += rank + " ";
            } else if (rank == 10) {
                result += "10";
            } else if (rank == 11) {
                result += "J ";
            } else if (rank == 12) {
                result += "Q ";
            } else if (rank == 13) {
                result += "K ";
            }
            if (suit == 0) {
                result += "\u2660";
            } else if (suit == 1) {
                result += "\u2665";
            } else if (suit == 2) {
                result += "\u2663";
            } else if (suit == 3) {
                result += "\u2666";
            }
            result += "]";
            return result;
        }
    }
    
    public int hashCode() {
    	return rank * 100 + suit;
    }
    
    /**
     * @return 0 = Red, 1 = Black
     */
    public int color() {
    	return suit % 2;
    }
}
