public class HighScore implements Comparable<HighScore> {
	
	private String name;
	private int score;
	
	public HighScore(String name, int score) {
		this.name = name;
		this.score = score;
	}
	
	public int getScore() {
		return score;
	}
	
	public String getName() {
		return name;
	}
	
	public int compareTo(HighScore other) {
		if (other.score > this.score) {
			return -1;
		} else if (other.score < this.score) {
			return 1;
		} else {
			return 0;
		}
	}
}
