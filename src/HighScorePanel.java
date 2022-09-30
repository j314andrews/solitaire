import java.util.*;
import javax.swing.*;
import java.awt.*;

public class HighScorePanel extends JPanel {

	private ArrayList<HighScore> scores;
	
	public HighScorePanel(ArrayList<HighScore> highScores) {
		scores = highScores;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Collections.sort(scores);
		for (int i = 0; i < Math.min(10, scores.size()); i++) {
			HighScore hs = scores.get(i);
			int ranking = i + 1;
			String name = hs.getName();
			int time = hs.getScore();
			int mins = time / 60, secs = time % 60;
			String nameDisplay = ranking + ". " + name + " ";
			String timeDisplay = "";
			if (mins < 10) {
				timeDisplay += "0";
			} 
			timeDisplay += mins + ":";
			if (secs < 10) {
				timeDisplay += "0";
			}
			timeDisplay += secs;
			g.drawString(nameDisplay, 25, i * 25 + 35);
			g.drawString(timeDisplay, 125, 25 * i + 35);
		}
	}
}
