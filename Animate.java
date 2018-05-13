/*
http://www.java2s.com/Code/Java/2D-Graphics-GUI/HowtocreateAnimationPaintandthread.htm
"C:\Program Files\Java\jdk1.8.0_66\bin\javac"  Animate.java -Xlint:deprecation
java Animate &
*/ 
     

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

public class Animate extends JFrame {

  private static int DELAY = 100;

  Insets insets;

  Color colors[] = { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN,
      Color.BLUE, Color.MAGENTA };

  public void paint(Graphics g) {
    super.paint(g);
    if (insets == null) {
      insets = getInsets();
    }
    // Calculate each time in case of resize
    int x = insets.left;
    int y = insets.top;
    int width = getWidth() - insets.left - insets.right;
    int height = getHeight() - insets.top - insets.bottom;
    int start = 0;
    int steps = colors.length;
    int stepSize = 360 / steps;
    synchronized (colors) {
      for (int i = 0; i < steps; i++) {
        g.setColor(colors[i]);
        g.fillArc(x, y, width, height, start, stepSize);
        start += stepSize;
      }
    }
  }

  public void go() {
    TimerTask task = new TimerTask() {
      public void run() {
        Color c = colors[0];
        synchronized (colors) {
          System.arraycopy(colors, 1, colors, 0, colors.length - 1);
          colors[colors.length - 1] = c;
        }
        repaint();
      }
    };
    Timer timer = new Timer();
    timer.schedule(task, 0, DELAY);
  }

  public static void main(String args[]) {
    Animate f = new Animate();
    f.setSize(200, 200);
    // f.show();
	f.setVisible(true);
    f.go();
  }
}
