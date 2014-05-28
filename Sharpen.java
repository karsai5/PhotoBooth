import java.awt.image.BufferedImage;
import java.awt.image.Kernel;

/**
 * Created by linuskarsai on 28/05/2014.
 */
public class Sharpen implements Filter {
    @Override
    public BufferedImage apply(BufferedImage img, Object[] options) {
        float sharpen[] = {
                0, 0, 0, 0, 0,
                0, 0, -1, 0, 0,
                0, -1, 5, -1, 0,
                0, 0, -1, 0, 0,
                0, 0, 0, 0, 0
        };
        return ImageUtilities.convolutionFilter(img, new Kernel(5, 5, sharpen));
    }
}
