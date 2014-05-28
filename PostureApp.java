import org.bytedeco.javacv.CanvasFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.TimerTask;

/**
 * Created by linuskarsai on 27/05/2014.
 */
public class PostureApp {
    private ImageGrabber imageGrabber;
    private ImageUtilities imageUtilities;
    private JFrame canvas = new CanvasFrame("Web Cam");
    private int frameRate = 0;
    private java.util.Timer frameRateTimer = new java.util.Timer();
    private JLabel frameRateTextArea = new JLabel();
    private JLabel background = new JLabel();
    private float scaleFloat = 1.5f;
    private float brightnessFloat = 20;
    private Histogram histogram = new Histogram();

    public PostureApp() {
        // Set framerate timer to run every second
        frameRateTimer.schedule(new printFrameRate(), 0, 1000);
        imageGrabber = new ImageGrabber(this);
        canvas.setLayout(null);
        canvas.add(background);
        canvas.setSize(640, 480);
        canvas.addKeyListener(new keyManager());
        background.setBounds(0, 0, 640, 480);
        background.setLayout(null);
        background.add(frameRateTextArea);
        imageGrabber.run();
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void imageGrabberHook(BufferedImage img) {
        // img = imageUtilities.toAndyWorhol(img, 90);
//        img = imageUtilities.sharpen(img);
        img = imageUtilities.contrast(img, scaleFloat, brightnessFloat);
        background.setIcon(new ImageIcon(img));
        histogram.drawHistogram(img);

        ++frameRate;
    }

    class printFrameRate extends java.util.TimerTask {
        @Override
        public void run() {
            frameRateTextArea.setText("<html>FrameRate:<br>" + Integer.toString(frameRate)+"</html>");
            frameRateTextArea.setBounds(20, 20, (int)frameRateTextArea.getPreferredSize().getWidth(), (int)frameRateTextArea.getPreferredSize().getHeight());
            frameRate = 0;
        }
    }

    class keyManager extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent event) {
            int keyCode = event.getKeyCode();
            if (keyCode == event.VK_UP) {
                scaleFloat += 0.05f;
            } else if (keyCode == event.VK_DOWN) {
                scaleFloat -= 0.05f;
            } else if (keyCode == event.VK_LEFT) {
                brightnessFloat -= 10;
            } else if (keyCode == event.VK_RIGHT) {
                brightnessFloat += 10;
            } else if (keyCode == event.VK_H) {
                if (histogram.isVisible())
                    histogram.hide();
                else
                    histogram.show();
            } else if (keyCode == event.VK_R) {
                scaleFloat = 1.5f;
                brightnessFloat = 20;
            }
        }
    }

    private void ViewImage() {

    }

    public static void main(String args[]) {
        PostureApp app = new PostureApp();
    }
}
