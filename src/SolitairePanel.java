import java.awt.*;
import javax.swing.*;
import javax.swing.Timer;

import java.util.*;
import javax.imageio.*;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.awt.event.*;

public class SolitairePanel extends JPanel implements MouseListener, ActionListener {
	
	private RussianSolitaire rs; 
	private ArrayList<Integer> sessionHighScores = new ArrayList<Integer>();
	private boolean addedScore = false;
	private static final int TOP_SPACE = 50, LEFT_SPACE = 30;
	private static final int CARD_HEIGHT = 180, CARD_WIDTH = 120;
	private static final int HORIZONTAL_SPACE = 25;
	private static final int FOUNDATION_SPACE = 25;
	private static final int FOUNDATION_SPACE_LEFT = 200;
	private static final int TABLEAU_SPACE = 30;
	private static final int ARROWHEAD_LENGTH = 10;
	private static final int ANIMATION_STEPS = 15;
	private static final int TIME_PER_ANIMATED_STEP = 1;
	private Image[][] cardPictures;
    private Image cardBackPicture;
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
	private Timer animationTimer = new Timer(TIME_PER_ANIMATED_STEP, this);
	private TimerPanel panel = new TimerPanel(7 * CARD_WIDTH + 2 * LEFT_SPACE + 6 * HORIZONTAL_SPACE, 75);
	private PrintStream scorePrinter;
	
	public SolitairePanel() {
		this(RussianSolitaire.winnableGame(), null);
	}
	
	public SolitairePanel(String dealCode) {
		this(dealCode, null);
	}
	
	public SolitairePanel(String dealCode, PrintStream out) {
		scorePrinter = out;
		rs = new RussianSolitaire(dealCode);
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
			rs = new RussianSolitaire(RussianSolitaire.winnableGame());
			repaint();
			panel.reset();
			drawMoves = false;
		} else if (str.equals("Show Moves")) {
			drawMoves = true;
			repaint();
		}
	}
	
	public Point getCoordinates(Card c) {
		int pile = rs.findCard(c);
		if (pile == -1) {
			return null;
		} else {
			int x = LEFT_SPACE + pile * (CARD_WIDTH + HORIZONTAL_SPACE) + CARD_WIDTH / 2;
			int index = 0;
			while (!rs.getTableau(pile).get(index).equals(c)) {
				index++;
			}
			int y = TOP_SPACE + CARD_HEIGHT + FOUNDATION_SPACE + index * TABLEAU_SPACE;
			if (index == rs.getTableau(pile).size() - 1) {
				y += CARD_HEIGHT / 2;
			} else {
				y += TABLEAU_SPACE / 2;
			}
			return new Point(x, y);
		}
	}
	
	public void mouseEntered(MouseEvent e) {
		
	}
	
	public void mouseExited(MouseEvent e) {
		
	}
	
	public void mousePressed(MouseEvent e) {
		e = SwingUtilities.convertMouseEvent(e.getComponent(), e, this);
		Point p = e.getPoint();
		boolean left = SwingUtilities.isLeftMouseButton(e);
		if (left) {
			dragDropTarget = getCardFromLocation(p);
		} 
		repaint();
	}
	
	public void mouseReleased(MouseEvent e) {
		boolean left = SwingUtilities.isLeftMouseButton(e);
		e = SwingUtilities.convertMouseEvent(e.getComponent(), e, this);
		Point p = e.getPoint();
		Card releaseCard = getCardFromLocation(p);
		if (left) {
			if (dragDropTarget != null && dragDropTarget.faceUp()) {
				if (releaseCard == null && dragDropTarget.getRank() == 13) {
					if (rs.moveKing(dragDropTarget.getSuit())) {
						drawMoves = false;
					}
				} else if (releaseCard != null) {
					if (releaseCard.faceUp()) {
						if (releaseCard.equals(new Card(dragDropTarget.getRank() + 1, dragDropTarget.getSuit()))) {
							if (releaseCard.equals(rs.getTableau(rs.findCard(releaseCard)).get(rs.getTableau(rs.findCard(releaseCard)).size() - 1))) {
								int colFrom = rs.findCard(dragDropTarget);
								int index = 0;
								while (!rs.getTableau(colFrom).get(index).equals(dragDropTarget)) {
									index++;
								} 
								if (rs.legalNonemptyMoves().contains(rs.findCard(releaseCard))) {
									toAnimate = new HashSet<>();
									for (int i = index; i < rs.getTableau(colFrom).size(); i++) {
										Card x = rs.getTableau(colFrom).get(i);
										toAnimate.add(x);
									}
									animationTimer.start();
									xDiffAnimation = (colFrom - rs.findCard(releaseCard)) * (CARD_WIDTH + HORIZONTAL_SPACE);
									yDiffAnimation = (index - rs.getTableau(rs.findCard(releaseCard)).size()) * TABLEAU_SPACE;
									rs.moveTo(rs.findCard(releaseCard));
									colToMoveTo = rs.findCard(releaseCard);
									drawMoves = false;
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
		e = SwingUtilities.convertMouseEvent(e.getComponent(), e, this);
		Point p = e.getPoint();
		boolean left = SwingUtilities.isLeftMouseButton(e);
		boolean right = SwingUtilities.isRightMouseButton(e);
		if (left) {
			Card c = getCardFromLocation(p);
			if (c != null && c.faceUp()) {
				if (c.getRank() == 13 && !rs.legalKingMoves().isEmpty()) {
					if (rs.moveKing(c.getSuit())) {
						drawMoves = false;
					}
				} else if (c.getRank() <= 12 && c.faceUp()) {
					Card next = new Card(c.getRank() + 1, c.getSuit());
					int col = rs.findCard(next);
					if (col >= 0) {
						ArrayList<Card> pile = rs.getTableau(col);
						if (pile.get(pile.size() - 1).equals(next)) {
							if (rs.moveTo(col)) {
								drawMoves = false;
							}
						}
					}
				}
			}
		} else if (right) {
			Card c = getCardFromLocation(p);
			if (c != null && c.faceUp()) {
				int col = rs.findCard(c);
				if (rs.legalNonemptyMoves().contains(col)) {
					ArrayList<Card> pile = rs.getTableau(col);
					if (c.equals(pile.get(pile.size() - 1))) {
						if (rs.moveTo(col)) {
							drawMoves = false;
						}
					}
				}
			}
		}
		repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		rs.autoplay();
		String ranks = "A23456789TJQK";
		String suits = "SHCD";
		setBackground(new Color(0, 127, 127));
		int startCol = 0;
		if (colToMoveTo != -1) {
			startCol = (colToMoveTo + 1) % 7;
		}
		for (int i = 0; i < 7; i++) {
			int q = (i + startCol) % 7;
			ArrayList<Card> pile = rs.getTableau(q);
			for (int j = 0; j < pile.size(); j++) {
				Card c = pile.get(j);
				if (c.faceUp()) {
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
					g.drawImage(cardPictures[c.getRank() - 1][c.getSuit()], x, y, CARD_WIDTH, CARD_HEIGHT, null);
				} else {
					int x = (CARD_WIDTH + HORIZONTAL_SPACE) * q + LEFT_SPACE;
					int y = TABLEAU_SPACE * j + TOP_SPACE + CARD_HEIGHT + FOUNDATION_SPACE;
					g.drawImage(cardBackPicture, x, y, CARD_WIDTH, CARD_HEIGHT, null);
				}
			}
		}
		for (int j = 0; j < 4; j++) {
			int rank = rs.getFoundation(j);
			if (rank > 0) {
				Image image = cardPictures[rank - 1][j];
				int x = (CARD_WIDTH + HORIZONTAL_SPACE) * j + FOUNDATION_SPACE_LEFT;
				int y = TOP_SPACE;
				g.drawImage(image, x, y, CARD_WIDTH, CARD_HEIGHT, null);
			}
		}
		if (dragDropTarget != null && dragDropTarget.faceUp()) {
			int pile = rs.findCard(dragDropTarget);
			int left = LEFT_SPACE + pile * (CARD_WIDTH + HORIZONTAL_SPACE);
			int right = left + CARD_WIDTH;
			int bottom = TOP_SPACE + 2 * CARD_HEIGHT + FOUNDATION_SPACE + (rs.getTableau(pile).size() - 1) * TABLEAU_SPACE; 
			Point p = getCoordinates(dragDropTarget);
			int top = 0;
			if (dragDropTarget.equals(rs.getTableau(pile).get(rs.getTableau(pile).size() - 1))) {
				top = p.y - CARD_HEIGHT / 2;
			} else {
				top = p.y - TABLEAU_SPACE / 2;
			} 
			Graphics2D g2D = (Graphics2D) g;
			g2D.setStroke(new BasicStroke(3));
			g2D.setColor(Color.RED);
			g2D.drawRoundRect(left, top, right - left, bottom - top, CARD_WIDTH / 15, CARD_WIDTH / 15);
			
		}
		if (drawMoves) {
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
		}
		if (rs.isWon()) {
			g.setColor(Color.WHITE);
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 128));
			g.drawString("YOU WON!", 200, 450);
			panel.stop();
			int time = panel.timeInSeconds();
			sessionHighScores.add(time);
			if (scorePrinter != null) {
				try {
					scorePrinter.println(InetAddress.getLocalHost().getAddress());
					scorePrinter.println(time);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
			Collections.sort(sessionHighScores);
		}
		if (toAnimate != null) {
			animationStep++;
		} 
		if (animationStep > ANIMATION_STEPS) {
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
				ArrayList<Card> pile = rs.getTableau(col);
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
		return null;
	}
	
	
	public void run() {
		JFrame frame = new JFrame("Russian Solitaire");
		int width = 7 * CARD_WIDTH + 2 * LEFT_SPACE + 6 * HORIZONTAL_SPACE;
		int height = 2 * TOP_SPACE + 2 * CARD_HEIGHT + FOUNDATION_SPACE + 24 * TABLEAU_SPACE;
		frame.setSize(new Dimension(width, height));
		frame.setResizable(false);
		frame.setLayout(new BorderLayout());
		frame.add(this.panel, BorderLayout.NORTH);
		frame.add(this, BorderLayout.CENTER);
		frame.addMouseListener(this);
		frame.setVisible(true);
	}
}
