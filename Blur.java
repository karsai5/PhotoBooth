import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Kernel;

/**
 * Created by linuskarsai on 28/05/2014.
 */
public class Blur implements Filter {
    @Override
    public BufferedImage apply(BufferedImage img, Object[] options) {
        float emboss[] = {
                0, 1, 2, 1, 0,
                1, 2, 3, 2, 1,
                2, 3, 4, 3, 2,
                1, 2, 3, 2, 1,
                0, 1, 2, 1, 0
        };
        img = ImageUtilities.contrast(img, 2.9499986f, -210.0f);
        return ImageUtilities.convolutionFilter(img, new Kernel(5, 5, emboss));
    }
}
