import java.util.*;

public class KlondikeDrawOne {

	private Card[] foundation;
	private ArrayList<ArrayList<Card>> tableau;
	private ArrayList<Card> stock;
	private ArrayList<Card> waste;
	private String dealCode;
	
	public KlondikeDrawOne() {
		Deck deck = new Deck();
		tableau = new ArrayList<>();
		foundation = new Card[4];
		stock = new ArrayList<>();
		waste = new ArrayList<>();
		for (int i = 0; i < 7; i++) {
			tableau.add(new ArrayList<>());
			for (int j = 0; j < i + 1; j++) {
				Card c = deck.getCardAt(i * (i + 1) / 2 + j);
				if (i == j) {
					c.flip();
				}
				tableau.get(i).add(c);
			}
		}
		for (int i = 28; i < 52; i++) {
			stock.add(deck.getCardAt(i));
		}
		dealCode = "";
		String ranks = "A23456789TJQK";
		String suits = "SHCD";
		for (int i = 0; i < 52; i++) {
			Card c = deck.getCardAt(i);
			dealCode += ranks.charAt(c.getRank() - 1);
			dealCode += suits.charAt(c.getSuit());
			
		}
	}
	
	public boolean emptyStock() {
		return stock.isEmpty();
	}
	
	public Card topOfWaste() {
		if (waste.isEmpty()) {
			return null;
		} else {
			return waste.get(waste.size() - 1);
		}
	}
	
	public KlondikeDrawOne(String dealCode) {
		this.dealCode = dealCode;
		Deck deck = new Deck(dealCode);
		tableau = new ArrayList<>();
		foundation = new Card[4];
		stock = new ArrayList<>();
		waste = new ArrayList<>();
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j <= i; j++) {
				
				Card c = deck.getCardAt(i * (i + 1) / 2 + j);
				if (i == j) {
					c.flip();
				}
				tableau.get(i).add(c);
			}
		}
		for (int i = 28; i < 52; i++) {
			stock.add(deck.getCardAt(i));
		}
	}
	
	public ArrayList<Card> getTableau(int index) {
		return new ArrayList<>(tableau.get(index));
	}
	
	public int getFoundation(int suit) {
		Card c = foundation[suit];
		if (c == null) {
			return 0;
		} else {
			return c.getRank();
		}
	}
	
	public String toString() {
        String result = "";
        if (!stock.isEmpty()) {
        	result += "[///]";
        } else {
        	result += "[   ]";
        }
        if (!waste.isEmpty()) {
        	result += waste.get(waste.size() - 1);
        } else {
        	result += "[   ]";
        }
        result += "     ";
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
	
	/**
	 * @return 0 to 6 if face up in tableau pile, 10 if face up on waste, -1 if not visible or on foundation.
	 */
	public int findCard(Card c) {
		if (c == null) {
			return -1;
		}
		for (int i = 0; i < 7; i++) {
			ArrayList<Card> pile = tableau.get(i);
			if (!pile.isEmpty()) {
				for (Card p : pile) {
					if (p.equals(c) && p.faceUp()) {
						return i;
					}
				}
			}
		}
		if (!waste.isEmpty() && waste.get(waste.size() - 1).equals(c)) {
			return 10;
		}
		return -1;
	}
	
	public void dealStock() {
		if (!stock.isEmpty()) {
			Card c = stock.remove(0);
			c.flip();
			waste.add(c);
		} else {
			for (Card c : waste) {
				c.unflip();
			}
			stock = waste; 
			waste = new ArrayList<>();
		}
	}
	
	public int foundation(int suit) {
		if (foundation[suit] == null) {
			return 0;
		} else {
			return foundation[suit].getRank();
		}
	}
	
	public boolean moveToFoundation(Card c) {
		if (c == null) {
			return false;
		}
		int suit = c.getSuit();
		if (foundation(suit) != c.getRank() - 1) {
			return false;
		} else {
			int loc = findCard(c);
			if (loc == -1) {
				return false;
			} else if (loc == 10) {
				Card last = topOfWaste();
				foundation[suit] = last;
				waste.remove(waste.size() - 1);
				return true;
			} else {
				ArrayList<Card> pile = tableau.get(loc);
				Card last = pile.get(pile.size() - 1);
				if (last.equals(c)) {
					foundation[suit] = last;
					pile.remove(pile.size() - 1);
					if (!pile.isEmpty()) {
						pile.get(pile.size() - 1).flip();
					}
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean moveToTableau(Card c, int pile) {
		return moveToTableau(c, pile, false);
	}
	
	public boolean moveToTableau(Card c, int pile, boolean kingSpaceMovesOK) {
		if (pile < 0 || pile >= 7 || c == null) {
			return false;
		}
		int loc = findCard(c);
		if (loc == -1) {
			return false;
		}
		else if (loc == 10) {
			ArrayList<Card> stack = tableau.get(pile);
			if (!stack.isEmpty()) {
				Card last = stack.get(stack.size() - 1);
				if (last.getRank() == c.getRank() + 1 && last.color() != c.color()) {
					tableau.get(pile).add(waste.remove(waste.size() - 1));
					return true;
				} else {
					return false;
				}
			} else {
				if (c.getRank() == 13) {
					tableau.get(pile).add(waste.remove(waste.size() - 1));
					return true;
				} else {
					return false;
				}
			}
		} else {
			ArrayList<Card> stack = tableau.get(pile);
			ArrayList<Card> sourceStack = tableau.get(loc);
			if (loc == pile) {
				return false;
			} else {
				if (!stack.isEmpty()) {
					Card last = stack.get(stack.size() - 1);
					if (last.getRank() == c.getRank() + 1 && last.color() != c.color()) {
						int index = 0;
						while (!sourceStack.get(index).equals(c)) {
							index++;
						}
						List<Card> stack1 = sourceStack.subList(0, index);
						if (index >= 1) {
							stack1.get(index - 1).flip();
						}
						List<Card> stack2 = sourceStack.subList(index, sourceStack.size());
						tableau.get(pile).addAll(stack2);
						tableau.set(loc, new ArrayList<>());
						tableau.get(loc).addAll(stack1);
						return true;
					} else {
						return false;
					}
				} else {
					if (c.getRank() == 13) {
						int index = 0;
						while (!sourceStack.get(index).equals(c)) {
							index++;
						}
						if (index == 0 && !kingSpaceMovesOK) {
							return false;
						}
						List<Card> stack1 = sourceStack.subList(0, index);
						if (index >= 1) {
							stack1.get(index - 1).flip();
						}
						List<Card> stack2 = sourceStack.subList(index, sourceStack.size());
						tableau.get(pile).addAll(stack2);
						tableau.set(loc, new ArrayList<>());
						tableau.get(loc).addAll(stack1);
						return true;
					} else {
						return false;
					}
				}
			}
		}
	}
	
	public boolean moveToTableau(Card c) {
		for (int i = 0; i < 7; i++) {
			if (moveToTableau(c, i)) {
				return true;
			}
		}
		return false;
	}
	
	public Card firstFaceUp(int pile) {
		ArrayList<Card> stack = tableau.get(pile);
		if (stack.isEmpty()) {
			return null;
		} else {
			for (int i = 0; i < stack.size(); i++) {
				Card c = stack.get(i);
				if (c.faceUp()) {
					return c;
				}
			}
		}
		return null;
	}
	
	public ArrayList<Card> bridge(int source, int dest) {
		if (tableau.get(source).isEmpty() || tableau.get(dest).isEmpty()) {
			return null; 
		}
		Card end = firstFaceUp(source);
		Card start = tableau.get(dest).get(tableau.get(dest).size() - 1);
		return null;
	}
	
	public static boolean autoplaySafe() {
		boolean moved = false;
		
		return moved;
	}
	
	public boolean inStock(Card c) {
		for (Card k : stock) {
			if (k.equals(c)) {
				return true;
			}
		}
		for (Card w : waste) {
			if (w.equals(c)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean simulateNoPrinting() {
		KlondikeDrawOne kd1 = new KlondikeDrawOne();
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 7; j++) {
				if (!kd1.tableau.get(j).isEmpty()) {
					kd1.moveToFoundation(kd1.tableau.get(j).get(kd1.tableau.get(j).size() - 1));
				}
			}
		}
		for (int x = 0; x < 5; x++) {
			while (!kd1.stock.isEmpty()) {
				kd1.dealStock();
				kd1.moveToFoundation(kd1.topOfWaste());
				for (int j = 0; j < 13; j++) {
					for (int i = 0; i < 7; i++) {
						ArrayList<Card> stack = kd1.tableau.get(i);
						if (!stack.isEmpty()) {
							kd1.moveToFoundation(stack.get(stack.size() - 1));
						}
					}
				}
			}
		}
		for (int x = 0; x < 20; x++) {
			while (!kd1.stock.isEmpty()) {
				kd1.dealStock();
				boolean madeMove = true;
				while (madeMove) {
					madeMove = false;
					int i = 6;
					while (i >= 0 && !madeMove) {
						Card t = kd1.firstFaceUp(i);
						madeMove |= kd1.moveToTableau(t);
						i--;
					}
				}
				Card c = kd1.topOfWaste();
				kd1.moveToFoundation(c);
				kd1.moveToTableau(c);
				for (int j = 0; j < 13; j++) {
					for (int i = 0; i < 7; i++) {
						ArrayList<Card> stack = kd1.tableau.get(i);
						if (!stack.isEmpty()) {
							kd1.moveToFoundation(stack.get(stack.size() - 1));
						}
					}
				}
				for (int q = 0; q < 13; q++) {
					for (int suit = 0; suit <= 3; suit++) {
						if (kd1.foundation(suit) > 0 && kd1.foundation(suit) < 13) {
							int otherColor = 1 - (suit % 2);
							int suit1 = otherColor, suit2 = otherColor + 2;
							Card c1 = new Card(kd1.foundation(suit), suit1);
							Card c2 = new Card(kd1.foundation(suit), suit2);
							kd1.moveToTableau(c1);
							kd1.moveToFoundation(new Card(kd1.foundation(suit) + 1, suit));
							kd1.moveToTableau(c2);
							kd1.moveToFoundation(new Card(kd1.foundation(suit) + 1, suit));
						}
					}
				}
			}
			kd1.dealStock();
		}		
		return kd1.gameWon();
	}
	
	public static boolean simulate() {
		KlondikeDrawOne kd1 = new KlondikeDrawOne();
		System.out.println(kd1 + "\n\n");
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 7; j++) {
				if (!kd1.tableau.get(j).isEmpty()) {
					if (kd1.moveToFoundation(kd1.tableau.get(j).get(kd1.tableau.get(j).size() - 1))) {
						System.out.println(kd1 + "\n\n");
					}
				}
			}
		}
		for (int x = 0; x < 5; x++) {
			while (!kd1.stock.isEmpty()) {
				kd1.dealStock();
				if (kd1.moveToFoundation(kd1.topOfWaste())) {
					System.out.println(kd1 + "\n\n");
				}
				for (int j = 0; j < 13; j++) {
					for (int i = 0; i < 7; i++) {
						ArrayList<Card> stack = kd1.tableau.get(i);
						if (!stack.isEmpty()) {
							if (kd1.moveToFoundation(stack.get(stack.size() - 1))) {
								System.out.println(kd1 + "\n\n");
							}
						}
					}
				}
			}
		}
		for (int x = 0; x < 20; x++) {
			while (!kd1.stock.isEmpty()) {
				kd1.dealStock();
				boolean madeMove = true;
				while (madeMove) {
					madeMove = false;
					int i = 6;
					while (i >= 0 && !madeMove) {
						Card t = kd1.firstFaceUp(i);
						boolean b = kd1.moveToTableau(t);
						madeMove |= b;
						if (b) {
							System.out.println(kd1 + "\n\n");
						}
						i--;
					}
				}
				Card c = kd1.topOfWaste();
				if (kd1.moveToFoundation(c)) {
					System.out.println(kd1 + "\n\n");
				}
				if (kd1.moveToTableau(c)) {
					System.out.println(kd1 + "\n\n");
				}
				for (int j = 0; j < 13; j++) {
					for (int i = 0; i < 7; i++) {
						ArrayList<Card> stack = kd1.tableau.get(i);
						if (!stack.isEmpty()) {
							if (kd1.moveToFoundation(stack.get(stack.size() - 1))) {
								System.out.println(kd1 + "\n\n");;
							}
						}
					}
				}
				for (int q = 0; q < 13; q++) {
					for (int suit = 0; suit <= 3; suit++) {
						if (kd1.foundation(suit) > 0 && kd1.foundation(suit) < 13) {
							int otherColor = 1 - (suit % 2);
							int suit1 = otherColor, suit2 = otherColor + 2;
							Card c1 = new Card(kd1.foundation(suit), suit1);
							Card c2 = new Card(kd1.foundation(suit), suit2);
							if (kd1.moveToTableau(c1)) {
								System.out.println(kd1 + "\n\n");
							}
							if (kd1.moveToFoundation(new Card(kd1.foundation(suit) + 1, suit))) {
								System.out.println(kd1 + "\n\n");
							}
							if (kd1.moveToTableau(c2)) {
								System.out.println(kd1 + "\n\n");
							}
							if (kd1.moveToFoundation(new Card(kd1.foundation(suit) + 1, suit))) {
								System.out.println(kd1 + "\n\n");
							}
						}
					}
				}
			}
			kd1.dealStock();
		}		
		return kd1.gameWon();
	}
	
	public boolean gameWon() {
		for (int suit = 0; suit < 4; suit++) {
			if (foundation(suit) < 13) {
				return false;
			}
		}
		return true;
	}
	
	public static void main(String[] args) {
		simulate();
		/*
		int wins = 0;
		for (int i = 0; i < 10000; i++) {
			if (simulateNoPrinting()) {
				wins++;
			}
		}
		System.out.println("Win rate: " + (wins / 10000.0));
		*/
	}
}
