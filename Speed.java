import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by linuskarsai on 28/05/2014.
 */
public class Speed implements Filter {
    ArrayList<BufferedImage> previous = new ArrayList<BufferedImage>();
    int count = 0;
    BufferedImage combined;
    @Override
    public BufferedImage apply(BufferedImage img, Object[] options) {
        ++count;
        if (count%5 == 0) {
            count = 0;
            previous.add(img);
        }
        if (previous.size() > 0) {
            combined = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);

            Graphics g = combined.getGraphics();
            g.drawImage(img, 0, 0, null);
            g.drawImage(dropOpacity(previous.get(0)), 0, 0, null);
            try {
                ImageIO.write(combined, "PNG", new File("./", "combined.png"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return img;
    }

    private BufferedImage dropOpacity(BufferedImage img) {
        for (int column = 0; column < img.getHeight(); column++){
            for (int row = 0; row < img.getWidth(); row++){
                Color c = new Color(img.getRGB(row, column));
                Color newColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), 120);
                img.setRGB(row, column, newColor.getRGB());
            }
        }
        return img;
    }
}
