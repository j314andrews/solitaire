import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;

public class SolitaireSolvingThreadTest {

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            SolitaireSolvingThread t = new SolitaireSolvingThread(null);
            System.out.println("Thread " + i + " starting");
            t.start();
        }
    }
}
