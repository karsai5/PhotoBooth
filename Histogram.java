import org.bytedeco.javacv.CanvasFrame;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * Created by Linus Karsai (312070209) on 28/05/2014.
 */
public class Histogram {
    private JFrame canvas = new CanvasFrame("Histogram");
    private JLabel histogramLabel = new JLabel();

    public Histogram() {
        canvas.setLayout(null);
        canvas.add(histogramLabel);
        canvas.setFocusableWindowState(false);
        canvas.setVisible(false);
    }

    /**
     * If Histogram dialog is visible
     * draw histogram on it
     * @param img
     */
    public void drawHistogram(BufferedImage img) {
        if (canvas.isVisible()) {
            drawHistogram(ImageUtilities.getHistogram(img));
        }
    }

    /**
     * Get the histogram from ImageUtilities and wrap it
     * in html
     * @param s
     */
    public void drawHistogram(String s) {
        if (canvas.isVisible()) {
            histogramLabel.setText("<html>" + s.replace("\n", "<br>") + "<html>");
            histogramLabel.setBounds(0, 0, (int) histogramLabel.getPreferredSize().getWidth(), (int) histogramLabel.getPreferredSize().getHeight());
            canvas.setSize(histogramLabel.getWidth() + 25, histogramLabel.getHeight() + 25);
        }
    }

    public void hide() {
        canvas.setVisible(false);
    }

    public void show() {
        canvas.setVisible(true);
    }

    public boolean isVisible() {
        return canvas.isVisible();
    }
}

