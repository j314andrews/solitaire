import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

public class KlondikeDrawOnePanel extends JPanel implements MouseListener, ActionListener {
	
	private KlondikeDrawOne kd1 = new KlondikeDrawOne();
	private static final int TOP_SPACE = 50, LEFT_SPACE = 25;
	private static final int CARD_HEIGHT = 140, CARD_WIDTH = 100;
	private static final int HORIZONTAL_SPACE = 25;
	private static final int FOUNDATION_SPACE = 25;
	private static final int FOUNDATION_SPACE_LEFT = LEFT_SPACE + 3 * (CARD_WIDTH + HORIZONTAL_SPACE);
	private static final int TABLEAU_SPACE = 30;
	private static final int ARROWHEAD_LENGTH = 10;
	private static final int ANIMATION_STEPS = 15;
	private static final int TIME_PER_ANIMATED_STEP = 1;
	private int animationStep = 0;
	private int colToMoveTo = -1;
	private int xDiffAnimation = 0, yDiffAnimation = 0;
	private HashSet<Card> toAnimate = null;
	private Card dragDropTarget = null;
	private JButton showMoves = new JButton("Show Moves");
	private JButton newGame = new JButton("New Game");
	private JButton highScores = new JButton("High Scores");
	private int repaintX, repaintY;
	private boolean drawMoves = false;
	private boolean scoreSubmitted = false;
	private ArrayList<HighScore> highScoresList = new ArrayList<>();
	private Timer animationTimer = new Timer(TIME_PER_ANIMATED_STEP, this);
	private TimerPanel panel = new TimerPanel(7 * CARD_WIDTH + 2 * LEFT_SPACE + 6 * HORIZONTAL_SPACE, 75);
	private Image[][] cardPictures;
    private Image cardBackPicture;
	
	
	public KlondikeDrawOnePanel() { 
		newGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonAction("New Game");
			}
		});
		add(newGame);
		showMoves.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonAction("Show Moves");
			}
		});
		add(showMoves);
		highScores.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonAction("High Scores");
			}
		});
		add(highScores);
		panel.start();
		cardPictures = new Image[13][4];
		String ranks = "A23456789TJQK";
		String suits = "SHCD";
		for (int i = 0; i < 13; i++) {
				for (int j = 0; j < 4; j++) {
				char rank = ranks.charAt(i);
				char suit = suits.charAt(j);
				String imageFile = "cardimages/" + rank + suit + ".png";
				try {
					Image image = ImageIO.read(new File(imageFile));
					Image scaled = image.getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_DEFAULT);
					cardPictures[i][j] = scaled;
				} catch (IOException e) {
					System.out.println("File " + imageFile + " not found.");
				}
			}
		}
		try {
			cardBackPicture = ImageIO.read(new File("cardimages/1B.png")).getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_DEFAULT);
		} catch (IOException e) {
			System.out.println("File cardimages/1B.png not found.");
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		dragDropTarget = null;
		repaint();
	}
	
	public void buttonAction(String str) {
		if (str.equals("New Game")) {
			kd1 = new KlondikeDrawOne();
			repaint();
			panel.reset();
			drawMoves = false;
			scoreSubmitted = false;
		} else if (str.equals("Show Moves")) {
			// drawMoves = true;
			repaint();
		} else {
			JFrame frame = new JFrame("High Scores");
			frame.add(new HighScorePanel(highScoresList));
			frame.setSize(200, 400);
			frame.setVisible(true);
		}
	}
	
	public Point getCoordinates(Card c) {
		int pile = kd1.findCard(c);
		if (pile == -1) {
			return null;
		} else if (pile >= 0 && pile < 7) {
			int x = LEFT_SPACE + pile * (CARD_WIDTH + HORIZONTAL_SPACE) + CARD_WIDTH / 2;
			int index = 0;
			while (!kd1.getTableau(pile).get(index).equals(c)) {
				index++;
			}
			int y = TOP_SPACE + CARD_HEIGHT + FOUNDATION_SPACE + index * TABLEAU_SPACE;
			if (index == kd1.getTableau(pile).size() - 1) {
				y += CARD_HEIGHT / 2;
			} else {
				y += TABLEAU_SPACE / 2;
			}
			return new Point(x, y);
		} else {
			return new Point(LEFT_SPACE + CARD_WIDTH * 3 / 2 + HORIZONTAL_SPACE, TOP_SPACE + CARD_HEIGHT / 2);
		}
	}
	
	public void mouseEntered(MouseEvent e) {
		
	}
	
	public void mouseExited(MouseEvent e) {
		
	}
	
	public void mousePressed(MouseEvent e) {
		if (toAnimate == null) {
			e = SwingUtilities.convertMouseEvent(e.getComponent(), e, this);
			Point p = e.getPoint();
			boolean left = SwingUtilities.isLeftMouseButton(e);
			if (left) {
				dragDropTarget = getCardFromLocation(p);
			} 
			repaint();
		}
	}
	
	public int getColumn(Point p) {
		int col = (p.x - LEFT_SPACE) / (CARD_WIDTH + HORIZONTAL_SPACE);
		int offset = (p.x - LEFT_SPACE) % (CARD_WIDTH + HORIZONTAL_SPACE);
		if (offset > 0 && offset < CARD_WIDTH && p.y > TOP_SPACE + FOUNDATION_SPACE + CARD_HEIGHT) {
			return col;
		} else {
			return -1;
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		if (toAnimate == null) {
			boolean left = SwingUtilities.isLeftMouseButton(e);
			e = SwingUtilities.convertMouseEvent(e.getComponent(), e, this);
			Point p = e.getPoint();
			Card releaseCard = getCardFromLocation(p);
			if (left) {
				if (dragDropTarget != null && dragDropTarget.faceUp()) {
					boolean toFoundation = false;
					int pile = kd1.findCard(dragDropTarget);
					Point initial = getCoordinates(dragDropTarget);
					if (p.y >= TOP_SPACE && p.y <= TOP_SPACE + CARD_HEIGHT && p.x > FOUNDATION_SPACE_LEFT) {
						if ((p.x - FOUNDATION_SPACE_LEFT) / (CARD_WIDTH + HORIZONTAL_SPACE) < 4 && (p.x - FOUNDATION_SPACE_LEFT) % (CARD_WIDTH + HORIZONTAL_SPACE) <= CARD_WIDTH) {
							int suit = (p.x - FOUNDATION_SPACE) / (CARD_WIDTH + HORIZONTAL_SPACE);
							//if (suit == dragDropTarget.getSuit()) {
								toFoundation = kd1.moveToFoundation(dragDropTarget);
							//}
						}
						if (toFoundation) {
							toAnimate = new HashSet<>();
							toAnimate.add(dragDropTarget);
							animationTimer.start();
							int endX = FOUNDATION_SPACE_LEFT + dragDropTarget.getSuit() * (CARD_WIDTH + HORIZONTAL_SPACE) + CARD_WIDTH / 2;
							int endY = TOP_SPACE + CARD_HEIGHT / 2;
							xDiffAnimation = initial.x - endX;
							yDiffAnimation = initial.y - endY;
						}
						drawMoves = false;
					}
					if (releaseCard == null && dragDropTarget.getRank() == 13) {
						int moveTo = getColumn(p);
						if (moveTo >= 0 && kd1.getTableau(moveTo).isEmpty()) {
							int colFrom = kd1.findCard(dragDropTarget);
							if (colFrom >= 0 && colFrom < 7) {
								int index = 0;
								while (!kd1.getTableau(colFrom).get(index).equals(dragDropTarget)) {
									index++;
								} 
								toAnimate = new HashSet<>();
								for (int i = index; i < kd1.getTableau(colFrom).size(); i++) {
									Card x = kd1.getTableau(colFrom).get(i);
									toAnimate.add(x);
								}
								animationTimer.start();
								xDiffAnimation = (colFrom - moveTo) * (CARD_WIDTH + HORIZONTAL_SPACE);
								yDiffAnimation = index * TABLEAU_SPACE;
								kd1.moveToTableau(dragDropTarget, moveTo);
								colToMoveTo = moveTo;
								drawMoves = false;
							} else if (colFrom == 10) {
								toAnimate = new HashSet<>();
								toAnimate.add(kd1.topOfWaste());
								animationTimer.start();
								xDiffAnimation = (1 - moveTo) * (CARD_WIDTH + HORIZONTAL_SPACE);
								yDiffAnimation = -(FOUNDATION_SPACE + CARD_HEIGHT);
								kd1.moveToTableau(dragDropTarget, moveTo);
								colToMoveTo = kd1.findCard(releaseCard);
								drawMoves = false;
							}
						}
					} else if (releaseCard != null) {
						if (releaseCard.faceUp()) {
							if (releaseCard.getRank() == dragDropTarget.getRank() + 1 && dragDropTarget.color() != releaseCard.color()) {
								if (releaseCard.equals(kd1.getTableau(kd1.findCard(releaseCard)).get(kd1.getTableau(kd1.findCard(releaseCard)).size() - 1))) {
									int colFrom = kd1.findCard(dragDropTarget);
									int index = 0;
									if (colFrom >= 0 && colFrom < 7) {
										while (!kd1.getTableau(colFrom).get(index).equals(dragDropTarget)) {
											index++;
										} 
										toAnimate = new HashSet<>();
										for (int i = index; i < kd1.getTableau(colFrom).size(); i++) {
											Card x = kd1.getTableau(colFrom).get(i);
											toAnimate.add(x);
										}
										animationTimer.start();
										xDiffAnimation = (colFrom - kd1.findCard(releaseCard)) * (CARD_WIDTH + HORIZONTAL_SPACE);
										yDiffAnimation = (index - kd1.getTableau(kd1.findCard(releaseCard)).size()) * TABLEAU_SPACE;
										kd1.moveToTableau(dragDropTarget, kd1.findCard(releaseCard), true);
										colToMoveTo = kd1.findCard(releaseCard);
										drawMoves = false;
									} else if (colFrom == 10) {
										toAnimate = new HashSet<>();
										toAnimate.add(kd1.topOfWaste());
										animationTimer.start();
										xDiffAnimation = (1 - kd1.findCard(releaseCard)) * (CARD_WIDTH + HORIZONTAL_SPACE);
										yDiffAnimation = -(kd1.getTableau(kd1.findCard(releaseCard)).size() * TABLEAU_SPACE + FOUNDATION_SPACE + CARD_HEIGHT);
										kd1.moveToTableau(dragDropTarget, kd1.findCard(releaseCard), true);
										colToMoveTo = kd1.findCard(releaseCard);
										drawMoves = false;
									}
								} 
							}
						}
					}
				}
			}
		} 
		dragDropTarget = null;
		repaint();
	}
	
	public void mouseClicked(MouseEvent e) {
		if (toAnimate == null) {
			e = SwingUtilities.convertMouseEvent(e.getComponent(), e, this);
			Point p = e.getPoint();
			boolean left = SwingUtilities.isLeftMouseButton(e);
			boolean right = SwingUtilities.isRightMouseButton(e);
			if (left) {
				if (p.y >= TOP_SPACE && p.y <= TOP_SPACE + CARD_HEIGHT) {
					if (p.x >= LEFT_SPACE && p.x <= LEFT_SPACE + CARD_HEIGHT) {
						kd1.dealStock();
					}
				}
			}
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(new Color(0, 127, 127));
		int startCol = 0;
		if (colToMoveTo != -1) {
			startCol = (colToMoveTo + 1) % 7;
		}
		if (!kd1.emptyStock()) {
			g.drawImage(cardBackPicture, LEFT_SPACE, TOP_SPACE, CARD_WIDTH, CARD_HEIGHT, null);
		}
		Card waste = kd1.topOfWaste();
		if (waste != null) {
			Image img = cardPictures[waste.getRank() - 1][waste.getSuit()];
			g.drawImage(img, LEFT_SPACE + CARD_WIDTH + HORIZONTAL_SPACE, TOP_SPACE, CARD_WIDTH, CARD_HEIGHT, null);
		}
		
		for (int i = 0; i < 7; i++) {
			int q = (i + startCol) % 7;
			ArrayList<Card> pile = kd1.getTableau(q);
			for (int j = 0; j < pile.size(); j++) {
				Card c = pile.get(j);
				if (c.faceUp()) {
					Image image = cardPictures[c.getRank() - 1][c.getSuit()];
					int x = (CARD_WIDTH + HORIZONTAL_SPACE) * q + LEFT_SPACE;
					int y = TABLEAU_SPACE * j + TOP_SPACE + CARD_HEIGHT + FOUNDATION_SPACE;
					if (toAnimate != null && toAnimate.contains(c)) {
						x += ((ANIMATION_STEPS - animationStep) * xDiffAnimation) / ANIMATION_STEPS;
						y += ((ANIMATION_STEPS - animationStep) * yDiffAnimation) / ANIMATION_STEPS;
						if (repaintX == 0 && repaintY == 0) {
							repaintX = x;
							repaintY = y;
						}
					}
					g.drawImage(image, x, y, CARD_WIDTH, CARD_HEIGHT, null);
				} else {
					int x = (CARD_WIDTH + HORIZONTAL_SPACE) * q + LEFT_SPACE;
					int y = TABLEAU_SPACE * j + TOP_SPACE + CARD_HEIGHT + FOUNDATION_SPACE;
					g.drawImage(cardBackPicture, x, y, CARD_WIDTH, CARD_HEIGHT, null);
				}
			}
		}
		if (dragDropTarget != null && dragDropTarget.faceUp()) {
			if (kd1.topOfWaste() != null && dragDropTarget.equals(kd1.topOfWaste())) {
				int left = LEFT_SPACE + CARD_WIDTH + HORIZONTAL_SPACE;
				int top = TOP_SPACE;
				Graphics2D g2D = (Graphics2D) g;
				g2D.setStroke(new BasicStroke(3));
				g2D.setColor(Color.RED);
				g2D.drawRoundRect(left, top, CARD_WIDTH, CARD_HEIGHT, CARD_WIDTH / 15, CARD_WIDTH / 15);
				g2D.setStroke(new BasicStroke(1));
			} else {
				int pile = kd1.findCard(dragDropTarget);
				int left = LEFT_SPACE + pile * (CARD_WIDTH + HORIZONTAL_SPACE);
				int right = left + CARD_WIDTH;
				int bottom = TOP_SPACE + 2 * CARD_HEIGHT + FOUNDATION_SPACE + (kd1.getTableau(pile).size() - 1) * TABLEAU_SPACE; 
				Point p = getCoordinates(dragDropTarget);
				int top = 0;
				if (dragDropTarget.equals(kd1.getTableau(pile).get(kd1.getTableau(pile).size() - 1))) {
					top = p.y - CARD_HEIGHT / 2;
				} else {
					top = p.y - TABLEAU_SPACE / 2;
				} 
				Graphics2D g2D = (Graphics2D) g;
				g2D.setStroke(new BasicStroke(3));
				g2D.setColor(Color.RED);
				g2D.drawRoundRect(left, top, right - left, bottom - top, CARD_WIDTH / 15, CARD_WIDTH / 15);
				g2D.setStroke(new BasicStroke(1));
			}
		}
		for (int j = 0; j < 4; j++) {
			int rank = kd1.getFoundation(j);
			int x = (CARD_WIDTH + HORIZONTAL_SPACE) * j + FOUNDATION_SPACE_LEFT;
			int y = TOP_SPACE;
			String suits = "\u2660\u2665\u2663\u2666";
			if (j % 2 == 1) {
				g.setColor(Color.RED);
			} else {
				g.setColor(Color.BLACK);
			}
			g.drawRoundRect(x, y, CARD_WIDTH - 1, CARD_HEIGHT - 1, CARD_WIDTH / 8, CARD_WIDTH / 8);
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
			g.drawString(suits.charAt(j) + "", x + CARD_WIDTH / 8, y + CARD_HEIGHT / 4);
			Card curr = new Card(rank, j);
			if (rank > 1 && toAnimate != null && toAnimate.contains(curr)) {
				Image image = cardPictures[rank - 2][j];
				g.drawImage(image, x, y, CARD_WIDTH, CARD_HEIGHT, null);
			}
			if (toAnimate != null && toAnimate.contains(curr)) {
				x += ((ANIMATION_STEPS - animationStep) * xDiffAnimation) / ANIMATION_STEPS;
				y += ((ANIMATION_STEPS - animationStep) * yDiffAnimation) / ANIMATION_STEPS;
				if (repaintX == 0 && repaintY == 0) {
					repaintX = x;
					repaintY = y;
				}
				g.drawImage(cardPictures[rank - 1][j], x, y, CARD_WIDTH, CARD_HEIGHT, null);
			}
			if (rank > 0) {
				Image image = cardPictures[rank - 1][j];
				g.drawImage(image, x, y, CARD_WIDTH, CARD_HEIGHT, null);
			}
		}
		if (drawMoves) {
			/*
			ArrayList<Integer> moves = rs.legalNonemptyMoves();
			for (int move : moves) {
				Card dest = rs.getTableau(move).get(rs.getTableau(move).size() - 1);
				Card toMove = new Card(dest.getRank() - 1, dest.getSuit());
				Point start = getCoordinates(toMove);
				Point end = getCoordinates(dest);
				Graphics2D g2D = (Graphics2D) g;
				g2D.setStroke(new BasicStroke(3));
				g2D.setColor(Color.ORANGE);
				g2D.drawLine(start.x, start.y, end.x, end.y);
				double angle = Math.atan2(start.y - end.y, start.x - end.x);
				double arrow1 = angle - Math.PI / 6, arrow2 = angle + Math.PI / 6;
				int arrowX1 = end.x + (int)(ARROWHEAD_LENGTH * Math.cos(arrow1));
				int arrowY1 = end.y + (int)(ARROWHEAD_LENGTH * Math.sin(arrow1));
				int arrowX2 = end.x + (int)(ARROWHEAD_LENGTH * Math.cos(arrow2));
				int arrowY2 = end.y + (int)(ARROWHEAD_LENGTH * Math.sin(arrow2));
				g.drawLine(end.x, end.y, arrowX1, arrowY1);
				g.drawLine(end.x, end.y, arrowX2, arrowY2);
			}
			*/
		}
		if (kd1.gameWon()) {
			g.setColor(Color.WHITE);
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 128));
			g.drawString("YOU WON!", 120, 450);
			panel.stop();
			if (!scoreSubmitted) {
				highScoresList.add(new HighScore("John", panel.timeInSeconds()));
				scoreSubmitted = true;
			}
		}
		if (toAnimate != null) {
			animationStep++;
		} if (animationStep > ANIMATION_STEPS) {
			toAnimate = null;
			animationStep = 0;
			animationTimer.stop();
			repaintX = 0;
			repaintY = 0;
		}
		setVisible(true);
	}
	
	public Card getCardFromLocation(Point p) {
		int xLeftCorner = LEFT_SPACE;
		int yLeftCorner = TOP_SPACE + FOUNDATION_SPACE + CARD_HEIGHT;
		if ((p.x - xLeftCorner) % (CARD_WIDTH + HORIZONTAL_SPACE) <= CARD_WIDTH) {
			if (p.x - xLeftCorner <= 7 * (CARD_WIDTH + HORIZONTAL_SPACE)) {
				int col = (p.x - xLeftCorner) / (CARD_WIDTH + HORIZONTAL_SPACE);
				ArrayList<Card> pile = kd1.getTableau(col);
				int len = pile.size();
				if (len > 0) {
					int topFront = yLeftCorner + (len - 1) * TABLEAU_SPACE;
					int bottomFront = topFront + CARD_HEIGHT;
					if (p.y >= topFront && p.y < bottomFront) {
						return pile.get(len - 1);
					} else {
						int row = (p.y - yLeftCorner) / TABLEAU_SPACE;
						if (row >= 0 && row < len - 1) {
							return pile.get(row);
						}
					}
				}
			}
		} 
		if (p.x >= LEFT_SPACE + CARD_WIDTH + HORIZONTAL_SPACE && p.x <= LEFT_SPACE + 2 * CARD_WIDTH + HORIZONTAL_SPACE) {
			if (p.y >= TOP_SPACE && p.y <= TOP_SPACE + CARD_HEIGHT) {
				return kd1.topOfWaste();
			}
		} 
		return null;
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Klondike Draw 1");
		int width = 7 * CARD_WIDTH + 2 * LEFT_SPACE + 6 * HORIZONTAL_SPACE;
		int height = 2 * TOP_SPACE + 2 * CARD_HEIGHT + FOUNDATION_SPACE + 24 * TABLEAU_SPACE;
		frame.setSize(new Dimension(width, height));
		KlondikeDrawOnePanel panel = new KlondikeDrawOnePanel();
		frame.setResizable(false);
		frame.setLayout(new BorderLayout());
		frame.add(panel.panel, BorderLayout.NORTH);
		frame.add(panel, BorderLayout.CENTER);
		frame.addMouseListener(panel);
		frame.setVisible(true);
	}
}
