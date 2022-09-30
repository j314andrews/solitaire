import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TimerPanel extends JPanel implements ActionListener {
	
	private int time = 0;
	private Timer timer = new Timer(1000, this);
	private int width, height;
	
	public TimerPanel(int x, int y) {
		width = x;
		height = y;
		setPreferredSize(new Dimension(width, height));
	}
	
	public void start() {
		timer.start();
		repaint();
	}
	
	public void stop() {
		timer.stop();
		repaint();
	}
	
	public void reset() {
		time = 0;
		timer.restart();
		repaint();
	}
	
	public void actionPerformed(ActionEvent e) {
		time++;
		repaint();
	}
	
	public int timeInSeconds() {
		return time;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(new Color(0, 127, 127));
		g.setColor(Color.WHITE);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 48));
		String t = (time / 60) + ":";
		int sec = time % 60;
		if (sec < 10) {
			t += "0";
		} 
		t += sec;
		g.drawString(t, 420, 60);
	}
}
