import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Linus Karsai (312070209) on 28/05/2014.
 */
public class BlackAndWhite implements Filter {
    @Override
    /**
     * Convert each pixel to black and white
     */
    public BufferedImage apply(BufferedImage img, Object[] options) {
        for (int column = 0; column < img.getHeight(); column++){
            for (int row = 0; row < img.getWidth(); row++){
                Color c = new Color(img.getRGB(row, column));
                int grey = ImageUtilities.getGreyPixel(c);
                c = new Color(grey, grey, grey);
                img.setRGB(row, column, c.getRGB());
            }
        }
        return img;
    }
}
