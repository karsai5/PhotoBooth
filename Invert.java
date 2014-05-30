import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Linus Karsai (312070209) on 28/05/2014.
 */
public class Invert implements Filter {
    @Override
    public BufferedImage apply(BufferedImage img, Object[] options) {
        for (int column = 0; column < img.getHeight(); column++){
            for (int row = 0; row < img.getWidth(); row++){
                Color c = new Color(img.getRGB(row, column));
                c = new Color(255-c.getRed(), 255-c.getGreen(), 255-c.getBlue());
                img.setRGB(row, column, c.getRGB());
            }
        }
        return img;
    }
}
