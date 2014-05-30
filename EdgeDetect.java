import java.awt.image.BufferedImage;
import java.awt.image.Kernel;

/**
 * Created by Linus Karsai (312070209) on 28/05/2014.
 */
public class EdgeDetect implements Filter {
    @Override
    public BufferedImage apply(BufferedImage img, Object[] options) {
        float edgeDetect[] = {
                -1, -1, -1,
                -1, 8, -1,
                -1, -1, -1
        };
        return ImageUtilities.convolutionFilter(img, new Kernel(3, 3, edgeDetect));
    }
}
