import java.util.*;
import java.io.*;

/**
 * Rules for Russian Solitaire:
 *
 * Cards are initially dealt into 7 tableau piles as follows:
 * The 1st pile has 1 face-up card, and after that the nth pile has 5 face-up cards dealt on top of n-1 face down cards.
 * Tableau piles are built down in the same suit.  Any pile or sub-pile of face-up cards may be picked up.
 * Empty piles in the tableau may only be filled with a King (or pile of face-up cards with a king at the back).
 * A face-down card in the tableau is flipped face-up when it is no longer covered by any cards.
 *
 * The foundations are built up in suit, from Ace to King.  The game is won if all four foundations are completed.
 *
 * This game is winnable at least 3.1% of the time, with a randomly dealt deck.
 */
public class RussianSolitaire {

    private List<List<Card>> tableau;
    private Card[] foundation;
    private String dealCode;

    /**
        Deals a random game of Russian Solitaire.
     */
    public RussianSolitaire() {
        Deck deck = new Deck();
        deck.flipCardAt(0);
        for (int i = 2; i <= 6; i++) {
            deck.flipCardAt(i);
        }
        for (int i = 9; i <= 13; i++) {
            deck.flipCardAt(i);
        }
        for (int i = 17; i <= 21; i++) {
            deck.flipCardAt(i);
        }
        for (int i = 26; i <= 30; i++) {
            deck.flipCardAt(i);
        }
        for (int i = 36; i <= 40; i++) {
            deck.flipCardAt(i);
        }
        for (int i = 47; i <= 51; i++) {
            deck.flipCardAt(i);
        }
        tableau = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            tableau.add(new ArrayList<>());
        }
        tableau.get(0).add(deck.getCardAt(0));
        for (int i = 1; i <= 6; i++) {
            tableau.get(1).add(deck.getCardAt(i));
        }
        for (int i = 7; i <= 13; i++) {
            tableau.get(2).add(deck.getCardAt(i));
        }
        for (int i = 14; i <= 21; i++) {
            tableau.get(3).add(deck.getCardAt(i));
        }
        for (int i = 22; i <= 30; i++) {
            tableau.get(4).add(deck.getCardAt(i));
        }
        for (int i = 31; i <= 40; i++) {
            tableau.get(5).add(deck.getCardAt(i));
        }
        for (int i = 41; i <= 51; i++) {
            tableau.get(6).add(deck.getCardAt(i));
        }
        dealCode = "";
        for (int i = 0; i < 52; i++) {
            String ranks = "A23456789TJQK";
            String suits = "SHCD";
            Card c = deck.getCardAt(i);
            dealCode += ranks.charAt(c.getRank() - 1);
            dealCode += suits.charAt(c.getSuit());
        }
        foundation = new Card[4];
    }

    /**
     * Deals a game using a deck with the specified order of cards.
     *
     * @param dealCode a String specifying the order of the cards in the deck.
     */
    public RussianSolitaire(String dealCode) {
        Deck deck = new Deck(dealCode);
        deck.flipCardAt(0);
        for (int i = 2; i <= 6; i++) {
            deck.flipCardAt(i);
        }
        for (int i = 9; i <= 13; i++) {
            deck.flipCardAt(i);
        }
        for (int i = 17; i <= 21; i++) {
            deck.flipCardAt(i);
        }
        for (int i = 26; i <= 30; i++) {
            deck.flipCardAt(i);
        }
        for (int i = 36; i <= 40; i++) {
            deck.flipCardAt(i);
        }
        for (int i = 47; i <= 51; i++) {
            deck.flipCardAt(i);
        }
        tableau = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            tableau.add(new ArrayList<>());
        }
        tableau.get(0).add(deck.getCardAt(0));
        for (int i = 1; i <= 6; i++) {
            tableau.get(1).add(deck.getCardAt(i));
        }
        for (int i = 7; i <= 13; i++) {
            tableau.get(2).add(deck.getCardAt(i));
        }
        for (int i = 14; i <= 21; i++) {
            tableau.get(3).add(deck.getCardAt(i));
        }
        for (int i = 22; i <= 30; i++) {
            tableau.get(4).add(deck.getCardAt(i));
        }
        for (int i = 31; i <= 40; i++) {
            tableau.get(5).add(deck.getCardAt(i));
        }
        for (int i = 41; i <= 51; i++) {
            tableau.get(6).add(deck.getCardAt(i));
        }
        this.dealCode = dealCode;
        foundation = new Card[4];
    }

    public String toString() {
        String result = "";
        for (Card c : foundation) {
            if (c == null) {
                result += "[   ]";
            } else {
                result += c;
            }
        }
        result += "\n\n";
        int maxLength = 0;
        for (int i = 0; i < 7; i++) {
            int size = tableau.get(i).size();
            if (size > maxLength) {
                maxLength = size;
            }
        }
        for (int i = 0; i < 7; i++) {
            if (tableau.get(i).isEmpty()) {
                result += "[   ]";
            } else {
                result += tableau.get(i).get(0);
            }
        }
        result += "\n";
        for (int row = 1; row < maxLength; row++) {
            for (int i = 0; i < 7; i++) {
                if (row < tableau.get(i).size()) {
                    result += tableau.get(i).get(row);
                } else {
                    result += "     ";
                }
            }
            result += "\n";
        }
        return result;
    }

    public String getDealCode() {
        return dealCode;
    }

    public int findCard(Card c) {
        for (int i = 0; i < 7; i++) {
            List<Card> stack = tableau.get(i);
            for (Card card : stack) {
                if (card.equals(c)) {
                    if (card.faceUp()) {
                        return i;
                    } else {
                        return -1;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Moves cards directly to another pile.
     * @param col the column to be moved to.
     * @return true if cards were successfully moved to the specified column.
     */
    public boolean moveTo(int col) {
        List<Card> pile = tableau.get(col);
        if (pile.isEmpty()) {
            for (int i = 0; i < 3; i++) {  // Need to change choice of king for empty pile
                Card king = new Card(13, i);
                int location = findCard(king);
                if (location != -1) {
                    List<Card> fromPile = tableau.get(location);
                    int index = -1;
                    for (int j = 0; j < fromPile.size(); j++) {
                        if (fromPile.get(j).equals(king)) {
                            index = j;
                        }
                    }
                    if (index != 0) {
                        Card toUncover = fromPile.get(index - 1);
                        int uRank = toUncover.getRank(), uSuit = toUncover.getSuit();
                        boolean toFoundation = uRank == 1;
                        toFoundation |= (foundation[uSuit] != null && foundation[uSuit].getRank() == uRank - 1);
                        if (!toUncover.faceUp() || toFoundation) {
                            List<Card> tail = fromPile.subList(index, fromPile.size());
                            List<Card> head = fromPile.subList(0, index);
                            tableau.set(location, head);
                            tableau.set(col, tail);
                            if (!head.isEmpty() && !head.get(index - 1).faceUp()) {
                                head.get(index - 1).flip();
                            }
                            return true;
                        }
                    }
                }
            }
            for (int i = 0; i < 3; i++) {  // Need to change choice of king for empty pile
                Card king = new Card(13, i);
                int location = findCard(king);
                if (location != -1) {
                    List<Card> fromPile = tableau.get(location);
                    int index = -1;
                    for (int j = 0; j < fromPile.size(); j++) {
                        if (fromPile.get(j).equals(king)) {
                            index = j;
                        }
                    }
                    if (index != 0) {
                        List<Card> tail = fromPile.subList(index, fromPile.size());
                        List<Card> head = fromPile.subList(0, index);
                        tableau.set(location, head);
                        tableau.set(col, tail);
                        if (!head.isEmpty() && !head.get(index - 1).faceUp()) {
                            head.get(index - 1).flip();
                        }
                        return true;
                    }
                }
            }
        } else {
            Card lastCard = pile.get(pile.size() - 1);
            if (lastCard.getRank() == 1) {
                return false;
            } else {
                Card next = new Card(lastCard.getRank() - 1, lastCard.getSuit(), true);
                int move = findCard(next);
                if (move < 0 || move == col) {
                    return false;
                } else {
                    List<Card> fromPile = tableau.get(move);
                    int index = -1;
                    for (int i = 0; i < fromPile.size(); i++) {
                        if (fromPile.get(i).equals(next)) {
                            index = i;
                        }
                    }
                    List<Card> tail = fromPile.subList(index, fromPile.size());
                    List<Card> head = fromPile.subList(0, index);
                    if (!head.isEmpty() && !head.get(index - 1).faceUp()) {
                        head.get(index - 1).flip();
                    }
                    tableau.set(move, head);
                    pile.addAll(tail);
                    tableau.set(col, pile);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Moves king to empty space, given its suit, if such a move is allowed.
     *
     * @param suit the suit of the king to be moved
     * @return true if the move was successful, otherwise false.
     */
    public boolean moveKing(int suit) {
        int emptyPile = -1;
        for (int i = 6; i >= 0; i--) {
            if (tableau.get(i).isEmpty()) {
                emptyPile = i;
            }
        }
        if (emptyPile == -1) {
            return false;
        } else {
            Card king = new Card(13, suit);
            int colToMove = findCard(king);
            if (colToMove == -1) {
                return false;
            } else {
                List<Card> column = tableau.get(colToMove);
                int index = 0;
                while (!column.get(index).equals(king)) {
                    index++;
                }
                List<Card> head = column.subList(0, index), tail = column.subList(index, column.size());
                if (!head.isEmpty()) {
                    int lastIndex = head.size() - 1;
                    if (!head.get(lastIndex).faceUp()) {
                        head.get(lastIndex).flip();
                    }
                    tableau.set(emptyPile, tail);
                    tableau.set(colToMove, head);
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    public boolean autoplay() {
        boolean changed = false;
        boolean done = false;
        while (!done) {
            done = true;
            for (int i = 0; i < 7; i++) {
                List<Card> pile = new ArrayList<>(tableau.get(i));
                boolean empty = pile.isEmpty();
                if (!empty) {
                    Card lastCard = pile.get(pile.size() - 1);
                    int suit = lastCard.getSuit();
                    if (foundation[suit] == null) {
                        if (lastCard.getRank() == 1) {
                            foundation[suit] = lastCard;
                            pile.remove(pile.size() - 1);
                            done = false;
                            changed = true;
                            if (!pile.isEmpty() && !pile.get(pile.size() - 1).faceUp()) {
                                pile.get(pile.size() - 1).flip();
                            }
                        }
                    } else {
                        if (lastCard.getRank() == foundation[suit].getRank() + 1) {
                            foundation[suit] = lastCard;
                            pile.remove(pile.size() - 1);
                            done = false;
                            changed = true;
                            if (!pile.isEmpty() && !pile.get(pile.size() - 1).faceUp()) {
                                pile.get(pile.size() - 1).flip();
                            }
                        }
                    }
                }
                tableau.set(i, pile);
            }
        }
        return changed;
    }

    public boolean isWon() {
        for (int i = 0; i < 4; i++) {
            if (foundation[i] == null || foundation[i].getRank() != 13) {
                return false;
            }
        }
        return true;
    }

    public static String simulate() {
        RussianSolitaire rs = new RussianSolitaire();
        rs.autoplay();
        System.out.println(rs);
        for (int j = 0; j < 104; j++) {
            boolean uncoveredSuccessful = false;
            int col = 6;
            while (col >= 0 && !uncoveredSuccessful) {
                ArrayList<Integer> seqMoves = rs.findSequenceToFree(col);
                if (!seqMoves.isEmpty()) {
                    System.out.println(seqMoves);
                    for (int x : seqMoves) {
                        uncoveredSuccessful = rs.moveTo(x);
                        rs.autoplay();
                        System.out.println(rs);
                    }
                }
                col--;
            }
            boolean foundationSuccessful = false;
            if (!uncoveredSuccessful) {
                for (int i = 0; i <= 3; i++) {
                    int colToFree;
                    Card cardToFree;
                    if (rs.foundation[i] == null) {
                        cardToFree = new Card(1, i);
                        colToFree = rs.findCard(cardToFree);
                    } else {
                        cardToFree = new Card(rs.foundation[i].getRank() + 1, i);
                        colToFree = rs.findCard(cardToFree);
                    }
                    if (colToFree != -1) {
                        List<Card> colOfInterest = rs.tableau.get(colToFree);
                        int index = colOfInterest.size() - 1;
                        while (!colOfInterest.get(index).equals(cardToFree)) {
                            index--;
                        }
                        if (index < colOfInterest.size() - 1) {
                            Card toMove = colOfInterest.get(index + 1);
                            if (toMove.getRank() != 13) {
                                Card next = new Card(toMove.getRank() + 1, toMove.getSuit());
                                int toMoveTo = rs.findCard(next);
                                if (toMoveTo != -1 && toMoveTo != colToFree) {
                                    List<Card> moveToCol = rs.tableau.get(toMoveTo);
                                    if (moveToCol.get(moveToCol.size() - 1).equals(next)) {
                                        foundationSuccessful = rs.moveTo(toMoveTo);
                                        rs.autoplay();
                                        System.out.println(rs);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            boolean moveSuccessful = false;
            if (!foundationSuccessful) {
                List<Integer> coOrder = Arrays.asList(new Integer[] {0, 1, 2, 3, 4, 5, 6});
                Collections.shuffle(coOrder);
                int co = 0;
                while (co < 7 && !moveSuccessful) {
                    if (!rs.tableau.get(coOrder.get(co)).isEmpty()) {
                        moveSuccessful = rs.moveTo(coOrder.get(co));
                        rs.autoplay();
                        System.out.println(rs);
                    }
                    co++;
                }
            }

            /*
            boolean movedKing = false;
            if (!moveSuccessful && !uncoveredSuccessful) {
                int co = 0;
                while (co < 7 && !movedKing) {
                    if (rs.moveTo(co)) {
                        movedKing = true;
                    }
                    rs.autoplay();
                    co++;
                }
            }
            */

            boolean movedKing = false;
            if (!moveSuccessful && !uncoveredSuccessful) {
                int pile = 6;
                while (pile >= 1 && !movedKing) {
                    int faceDown = rs.faceDownCards(pile);
                    if (rs.faceDownCards(pile) > 0) {
                        Card firstFaceUp = rs.tableau.get(pile).get(faceDown);
                        if (firstFaceUp.getRank() == 13) {
                            int kingSuit = firstFaceUp.getRank();
                            movedKing = rs.moveKing(kingSuit);
                            rs.autoplay();
                        }
                    }
                    pile--;
                }
                int suit = 0;
                while (suit < 4 && !movedKing) {
                    movedKing = rs.moveKing(suit);
                    rs.autoplay();
                    suit++;
                }
                if (movedKing) {
                    System.out.println(rs);
                }
            }

        }
        for (int i = 0; i < 3; i++) {
            if (rs.foundation[i] == null || rs.foundation[i].getRank() != 13) {
                return null;
            }
        }
        return rs.dealCode;
    }

    public ArrayList<Integer> findSequenceToFree(int col) {
        if (tableau.get(col).isEmpty() || tableau.get(col).get(0).faceUp()) {
            return new ArrayList<>();
        } else {
            ArrayList<Integer> moves = new ArrayList<>();
            boolean stillPossible = true;
            Card toMove = tableau.get(col).get(faceDownCards(col));
            while (stillPossible) {
                if (toMove.getRank() == 13) {
                    stillPossible = false;
                } else {

                    Card dest = new Card(toMove.getRank() + 1, toMove.getSuit());
                    int newCol = findCard(dest);
                    if (newCol == col || newCol == -1) {
                        stillPossible = false;
                    } else {
                        List<Card> destCol = tableau.get(newCol);
                        if (destCol.get(destCol.size() - 1).equals(dest)) {
                            moves.add(0, newCol);
                            return moves;
                        } else {
                            int index = destCol.size() - 2;
                            while (!destCol.get(index).equals(dest)) {
                                index--;
                            }
                            toMove = destCol.get(index + 1);
                            moves.add(0, newCol);
                        }
                        col = newCol;
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    public int faceDownCards(int col) {
        if (tableau.get(col).isEmpty()) {
            return 0;
        } else {
            int j = 0;
            while (j < tableau.get(col).size() && !tableau.get(col).get(j).faceUp()) {
                j++;
            }
            return j;
        }
    }

    public static String simulateNoPrinting() {
        RussianSolitaire rs = new RussianSolitaire();
        rs.autoplay();
        for (int j = 0; j < 104; j++) {
            boolean uncoveredSuccessful = false;
            int col = 6;
            while (col >= 0 && !uncoveredSuccessful) {
                ArrayList<Integer> seqMoves = rs.findSequenceToFree(col);
                if (!seqMoves.isEmpty()) {
                    for (int x : seqMoves) {
                        uncoveredSuccessful = rs.moveTo(x);
                        rs.autoplay();
                    }
                }
                col--;
            }
            boolean foundationSuccessful = false;
            if (!uncoveredSuccessful) {
                for (int i = 0; i <= 3; i++) {
                    int colToFree;
                    Card cardToFree;
                    if (rs.foundation[i] == null) {
                        cardToFree = new Card(1, i);
                        colToFree = rs.findCard(cardToFree);
                    } else {
                        cardToFree = new Card(rs.foundation[i].getRank() + 1, i);
                        colToFree = rs.findCard(cardToFree);
                    }
                    if (colToFree != -1) {
                        List<Card> colOfInterest = rs.tableau.get(colToFree);
                        int index = colOfInterest.size() - 1;
                        while (!colOfInterest.get(index).equals(cardToFree)) {
                            index--;
                        }
                        if (index < colOfInterest.size() - 1) {
                            Card toMove = colOfInterest.get(index + 1);
                            if (toMove.getRank() != 13) {
                                Card next = new Card(toMove.getRank() + 1, toMove.getSuit());
                                int toMoveTo = rs.findCard(next);
                                if (toMoveTo != -1 && toMoveTo != colToFree) {
                                    List<Card> moveToCol = rs.tableau.get(toMoveTo);
                                    if (moveToCol.get(moveToCol.size() - 1).equals(next)) {
                                        foundationSuccessful = rs.moveTo(toMoveTo);
                                        rs.autoplay();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            boolean moveSuccessful = false;
            if (!foundationSuccessful) {
                List<Integer> coOrder = Arrays.asList(new Integer[] {0, 1, 2, 3, 4, 5, 6});
                Collections.shuffle(coOrder);
                int co = 0;
                while (co < 7 && !moveSuccessful) {
                    if (!rs.tableau.get(coOrder.get(co)).isEmpty()) {
                        moveSuccessful = rs.moveTo(coOrder.get(co));
                        rs.autoplay();
                    }
                    co++;
                }
            }

            /*
            boolean movedKing = false;
            if (!moveSuccessful && !uncoveredSuccessful) {
                int co = 0;
                while (co < 7 && !movedKing) {
                    if (rs.moveTo(co)) {
                        movedKing = true;
                    }
                    rs.autoplay();
                    co++;
                }
            }
            */

            boolean movedKing = false;
            if (!moveSuccessful && !uncoveredSuccessful) {
                int pile = 6;
                while (pile >= 1 && !movedKing) {
                    int faceDown = rs.faceDownCards(pile);
                    if (rs.faceDownCards(pile) > 0) {
                        Card firstFaceUp = rs.tableau.get(pile).get(faceDown);
                        if (firstFaceUp.getRank() == 13) {
                            int kingSuit = firstFaceUp.getRank();
                            movedKing = rs.moveKing(kingSuit);
                            rs.autoplay();
                        }
                    }
                    pile--;
                }
                int suit = 0;
                while (suit < 4 && !movedKing) {
                    movedKing = rs.moveKing(suit);
                    rs.autoplay();
                    suit++;
                }
            }

        }
        for (int i = 0; i <= 3; i++) {
            if (rs.foundation[i] == null || rs.foundation[i].getRank() != 13) {
                return null;
            }
        }
        return rs.dealCode;
    }

    public static void runSimulations(int n) {
        int wins = 0;
        for (int i = 0; i < n; i++) {
            String dealCodeIfWon = simulateNoPrinting();
            if (dealCodeIfWon != null) {
                wins++;
            }
            if (i % 10000 == 0) {
                System.out.println(i + " games simulated...");
            }
        }
        System.out.print("Win Rate: ");
        double winRate = (double) wins / (double) n;
        System.out.println(winRate);
        double standardError = Math.sqrt(winRate * (1 - winRate) / n);
        System.out.print("Standard Error: ");
        System.out.println(standardError);
    }

    /**
     * Finds n winnable games, and writes the deal codes to a .txt file.
     * @param n the number of desired winnable games
     */
    public static void generateWinnableGames(int n) {
        int wins = 0;
        try {
            while (wins < n) {
                String dealCodeIfWon = simulateNoPrinting();
                if (dealCodeIfWon != null) {
                    wins++;
                    synchronized (RussianSolitaire.class) {
                        BufferedWriter writer = new BufferedWriter(new FileWriter("solvableGames.txt", true));
                        writer.write(dealCodeIfWon + "\n");
                        writer.close();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String winnableGame() {
        String dealCodeIfWon = simulateNoPrinting();
        while (dealCodeIfWon == null) {
            dealCodeIfWon = simulateNoPrinting();
        }
        return dealCodeIfWon;
    }

    public int getFoundation(int suit) {
    	if (foundation[suit] == null) {
    		return 0;
    	} else {
    		return foundation[suit].getRank();
    	}
    }

    public ArrayList<Integer> legalNonemptyMoves() {
        ArrayList<Integer> result =  new ArrayList<>();
        for (int j = 0; j < 7; j++) {
            List<Card> col = tableau.get(j);
            if (!col.isEmpty()) {
                Card last = col.get(col.size() - 1);
                Card next = new Card(last.getRank() - 1, last.getSuit());
                int nextCol = findCard(next);
                if (nextCol != -1 && nextCol != j) {
                    result.add(j);
                }
            }
        }
        return result;
    }

    public ArrayList<Integer> legalKingMoves() {
        boolean emptySpace = false;
        for (List<Card> stack : tableau) {
            if (stack.isEmpty()) {
                emptySpace = true;
            }
        }
        if (!emptySpace) {
            return new ArrayList<>();
        } else {
            ArrayList<Integer> suits = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                Card king = new Card(13, i);
                int kingCol = findCard(king);
                if (kingCol >= 0) {
	                if (!tableau.get(kingCol).get(0).equals(king)) {
	                    suits.add(i);
	                }
                }
            }
            return suits;
        }
    }

    public static String randWinnableDealCode() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("solvableGames.txt"));
            int seed = (int) (Math.random() * 1000.0);
            String dealCode = "";
            for (int i = 0; i < seed; i++) {
                dealCode = reader.readLine();
            }
            return dealCode;
        } catch (FileNotFoundException e) {
            System.out.println("File solvableGames.txt is missing!");
        } catch (IOException e) {
            System.out.println("File ran out of lines!");
        }
        return null;
    }

    /**
     * Plays the solitaire game with the given deal code.
     */
    public static boolean playGameFromDealCode(String dealCode) {
        RussianSolitaire rs = new RussianSolitaire(dealCode);
        rs.autoplay();
        try {
            System.setOut(new PrintStream(System.out, false, "UTF8"));
        } catch (UnsupportedEncodingException e) {
            System.out.println("Unsupported Encoding!");
        }
        Scanner sc = new Scanner(System.in);
        System.out.println("Russian Solitaire");
        System.out.println("Rules: Type 1-7 to move to a pile, or S/D/C/H to move a king of a particular suit onto an empty pile.");
        System.out.println();
        System.out.println(rs);
        System.out.println("Enter your move: ");
        System.out.println();
        String move = sc.next();
        while (!move.equals("Q") && !rs.isWon()) {
            boolean valid = false;
            if (move.equals("S")) {
                valid = rs.moveKing(0);
            } else if (move.equals("H")) {
                valid = rs.moveKing(1);
            } else if (move.equals("C")) {
                valid = rs.moveKing(2);
            } else if (move.equals("D")) {
                valid = rs.moveKing(3);
            } else if (move.equals("1")) {
                valid = rs.moveTo(0);
            } else if (move.equals("2")) {
                valid = rs.moveTo(1);
            } else if (move.equals("3")) {
                valid = rs.moveTo(2);
            } else if (move.equals("4")) {
                valid = rs.moveTo(3);
            } else if (move.equals("5")) {
                valid = rs.moveTo(4);
            } else if (move.equals("6")) {
                valid = rs.moveTo(5);
            } else if (move.equals("7")) {
                valid = rs.moveTo(6);
            }
            if (valid) {
                rs.autoplay();
                System.out.println(rs);
                if (!rs.isWon()) {
                    System.out.println("Enter your move: ");
                }
            } else {
                System.out.println("That move is invalid.");
                System.out.println("Enter your move: ");
            }
            if (!rs.isWon()) {
                move = sc.next();
            }
        }
        if (rs.isWon()) {
            System.out.println();
            System.out.println("You Win!");
            return true;
        } else {
            return false;
        }
    }

    /**
     * Deals a random winnable game from solvableGames.txt.
     */
    public static boolean playWinnableGame() {
        String dealCode = randWinnableDealCode();
        RussianSolitaire rs = new RussianSolitaire(dealCode);
        rs.autoplay();
        Scanner sc = new Scanner(System.in);
        try {
            System.setOut(new PrintStream(System.out, false, "UTF8"));
        } catch (UnsupportedEncodingException e) {
            System.out.println("Unsupported Encoding!");
        }
        System.out.println("Russian Solitaire");
        System.out.println("Rules: Type 1-7 to move to a pile, or S/D/C/H to move a king of a particular suit onto an empty pile.");
        System.out.println();
        System.out.println(rs);
        System.out.println("Enter your move: ");
        System.out.println();
        String move = sc.next();
        while (!move.equals("Q") && !rs.isWon()) {
            boolean valid = false;
            if (move.equals("S")) {
                valid = rs.moveKing(0);
            } else if (move.equals("H")) {
                valid = rs.moveKing(1);
            } else if (move.equals("C")) {
                valid = rs.moveKing(2);
            } else if (move.equals("D")) {
                valid = rs.moveKing(3);
            } else if (move.equals("1")) {
                valid = rs.moveTo(0);
            } else if (move.equals("2")) {
                valid = rs.moveTo(1);
            } else if (move.equals("3")) {
                valid = rs.moveTo(2);
            } else if (move.equals("4")) {
                valid = rs.moveTo(3);
            } else if (move.equals("5")) {
                valid = rs.moveTo(4);
            } else if (move.equals("6")) {
                valid = rs.moveTo(5);
            } else if (move.equals("7")) {
                valid = rs.moveTo(6);
            }
            if (valid) {
                rs.autoplay();
                System.out.println(rs);
                if (!rs.isWon()) {
                    System.out.println("Enter your move: ");
                }
            } else {
                System.out.println("That move is invalid.");
                System.out.println("Enter your move: ");
            }
            if (!rs.isWon()) {
                move = sc.next();
            }
        }
        if (rs.isWon()) {
            System.out.println();
            System.out.println("You Win!");
            return true;
        } else {
            System.out.println();
            System.out.println("You Lost!");
            return false;
        }
    }
        
    public ArrayList<Card> getTableau(int pile) {
    	return new ArrayList<>(tableau.get(pile));
    }
}

