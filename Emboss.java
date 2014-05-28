import java.awt.image.BufferedImage;
import java.awt.image.Kernel;

/**
 * Created by linuskarsai on 28/05/2014.
 */
public class Emboss implements Filter{

    @Override
    public BufferedImage apply(BufferedImage img, Object[] options) {
        float emboss[] = {
                -2, -1, 0,
                -1, 1, 1,
                0, 1, 2
        };
        return ImageUtilities.convolutionFilter(img, new Kernel(3, 3, emboss));
    }
}
