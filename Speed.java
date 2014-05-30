import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by Linus Karsai (312070209) on 28/05/2014.
 */
public class Speed implements Filter {
    ArrayList<BufferedImage> previous = new ArrayList<BufferedImage>();
    int count = 0;
    @Override
    public BufferedImage apply(BufferedImage img, Object[] options) {
        if (previous.size() >= 3){
            BufferedImage previousArray[] = new BufferedImage[previous.size()];
            previous.toArray(previousArray);
            int opacity = 150;
            for (int i = 0; i < previousArray.length; ++i) {
                img = addImages(img,previousArray[i], opacity);
            }
            previous.remove(0);
        }
        previous.add(img);
        ++count;
        return img;
    }

    private BufferedImage addImages(BufferedImage base, BufferedImage overlay, int opacity) {
        BufferedImage combined = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics g = combined.getGraphics();
        g.drawImage(base, 0, 0, null);
        g.drawImage(dropOpacity(overlay, 100), 0, 0, null);

        return combined;
    }

    private BufferedImage dropOpacity(BufferedImage img, int opacity) {
        BufferedImage tmp = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        tmp.getGraphics().drawImage(img, 0, 0, null);
        for (int column = 0; column < img.getHeight(); column++){
            for (int row = 0; row < img.getWidth(); row++){
                Color c = new Color(img.getRGB(row, column));
                Color newColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), 120);
                tmp.setRGB(row, column, newColor.getRGB());
            }
        }
        return tmp;
    }
}
